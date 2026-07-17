/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * Original hard forked code:
 * https://github.com/ReVanced/revanced-patches/commit/724e6d61b2ecd868c1a9a37d465a688e83a74799
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to Morphe contributions.
 */

package app.argusos.patches.youtube.video.quality

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playertype.playerTypeHookPatch
import app.argusos.patches.youtube.misc.playservice.versionCheckPatch
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.VideoQualityChangedFingerprint
import app.argusos.patches.youtube.video.information.onCreateHook
import app.argusos.patches.youtube.video.information.videoInformationPatch
import app.morphe.util.findFieldFromToString
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

private const val EXTENSION_CLASS =
    "Lapp/argusos/extension/youtube/patches/playback/quality/RememberVideoQualityPatch;"

val rememberVideoQualityPatch = bytecodePatch {
    dependsOn(
        sharedExtensionPatch,
        videoInformationPatch,
        playerTypeHookPatch,
        settingsPatch,
        versionCheckPatch,
    )

    execute {
        settingsMenuVideoQualityGroup.addAll(listOf(
            ListPreference(
                key = "argusos_video_quality_default_mobile",
                entriesKey = "argusos_video_quality_default_entries",
                entryValuesKey = "argusos_video_quality_default_entry_values"
            ),
            ListPreference(
                key = "argusos_video_quality_default_wifi",
                entriesKey = "argusos_video_quality_default_entries",
                entryValuesKey = "argusos_video_quality_default_entry_values"
            ),
            SwitchPreference("argusos_remember_video_quality_last_selected", summary = true),

            ListPreference(
                key = "argusos_shorts_quality_default_mobile",
                entriesKey = "argusos_shorts_quality_default_entries",
                entryValuesKey = "argusos_shorts_quality_default_entry_values",
            ),
            ListPreference(
                key = "argusos_shorts_quality_default_wifi",
                entriesKey = "argusos_shorts_quality_default_entries",
                entryValuesKey = "argusos_shorts_quality_default_entry_values"
            ),
            SwitchPreference("argusos_remember_shorts_quality_last_selected", summary = true),
            SwitchPreference("argusos_remember_video_quality_last_selected_toast", summary = true)
        ))

        onCreateHook(EXTENSION_CLASS, "newVideoStarted")

        val initialResolutionField = PlaybackStartParametersToStringFingerprint.method
                .findFieldFromToString(FIXED_RESOLUTION_STRING)

        // Inject a call to override initial video quality.
        Fingerprint(
            classFingerprint = PlaybackStartParametersToStringFingerprint,
            name = "<init>",
            filters = listOf(
                fieldAccess(
                    opcode = Opcode.IPUT_OBJECT,
                    reference = initialResolutionField
                )
            )
        ).let {
            it.method.apply {
                val index = it.instructionMatches.last().index
                val register = getInstruction<TwoRegisterInstruction>(index).registerA

                addInstructions(
                    index,
                    """
                        invoke-static { v$register }, $EXTENSION_CLASS->getInitialVideoQuality(Lj$/util/Optional;)Lj$/util/Optional;
                        move-result-object v$register
                    """
                )
            }
        }

        // Inject a call to remember the selected quality for Shorts.
        VideoQualityItemOnClickFingerprint.method.addInstruction(
            0,
            "invoke-static { p3 }, $EXTENSION_CLASS->userChangedShortsQuality(I)V"
        )

        // Inject a call to remember the user selected quality for regular videos.
        VideoQualityChangedFingerprint.let {
            it.method.apply {
                val index = it.instructionMatches.last().index
                val register = getInstruction<TwoRegisterInstruction>(index).registerA

                addInstruction(
                    index + 1,
                    "invoke-static { v$register }, $EXTENSION_CLASS->userChangedQuality(I)V",
                )
            }
        }
    }
}
