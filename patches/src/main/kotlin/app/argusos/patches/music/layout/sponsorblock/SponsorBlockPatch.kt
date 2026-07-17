/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to Morphe contributions.
 */

package app.argusos.patches.music.layout.sponsorblock

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.all.misc.resources.addAppResources
import app.argusos.patches.all.misc.resources.addResourcesPatch
import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.music.video.information.musicVideoIdHook
import app.argusos.patches.music.video.information.musicVideoInformationPatch
import app.argusos.patches.music.video.information.musicVideoTimeHook
import app.argusos.patches.music.video.information.onMusicCreateHook
import app.argusos.patches.shared.misc.settings.preference.NonInteractivePreference
import app.argusos.patches.shared.misc.settings.preference.PreferenceCategory
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.TextPreference
import app.argusos.patches.youtube.layout.sponsorblock.categoryPreference
import app.morphe.util.findFreeRegister
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionReversedOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

private const val EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS =
    "Lapp/argusos/extension/music/sponsorblock/MusicSponsorBlockConfig;"

@Suppress("unused")
val musicSponsorBlockPatch = bytecodePatch(
    name = "SponsorBlock",
    description = "Adds options to enable and configure SponsorBlock, which can skip non-music segments."
) {
    dependsOn(
        sharedExtensionPatch,
        musicVideoInformationPatch,
        settingsPatch,
        addResourcesPatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        addAppResources("sponsorblock")

        // Configure the shared SponsorBlockApi and clear per-track state. Registered after the
        // VideoInformation initialize hook so player state is ready before SB initializes.
        onMusicCreateHook(EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS, "initialize")

        // Drives the skip-segment logic (~1 Hz).
        musicVideoTimeHook(EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS, "setVideoTime")

        // Triggers the SponsorBlock API fetch when a new track loads.
        musicVideoIdHook("$EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS->setCurrentVideoId(Ljava/lang/String;)V")

        // Compact-player seekbar: hook onMeasure to capture the bounding Rect and draw() to paint
        // segment markers over the timeline.
        val rectField = MusicTimeBarOnMeasureFingerprint.method.run {
            val rectIndex = indexOfFirstInstructionReversedOrThrow(
                implementation!!.instructions.size - 1
            ) {
                opcode == Opcode.IGET_OBJECT &&
                        getReference<FieldReference>()?.type == "Landroid/graphics/Rect;"
            }
            getInstruction<ReferenceInstruction>(rectIndex).reference as FieldReference
        }

        MusicTimeBarDrawFingerprint.method.apply {
            // Inject right after super.draw() so the overlay runs every frame regardless of
            // whether the scrubber thumb is visible. p1 is the Canvas.
            val freeRegister = findFreeRegister(1)
            addInstructions(
                1,
                """
                    iget-object v$freeRegister, p0, $rectField
                    invoke-static {v$freeRegister}, $EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS->setSeekbarRectangle(Landroid/graphics/Rect;)V
                    invoke-static {p1}, $EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS->drawSegmentTimeBars(Landroid/graphics/Canvas;)V
                """
            )
        }

        PreferenceScreen.SPONSORBLOCK.addPreferences(
            SwitchPreference("argusos_sb_enabled", summary = true),
            SwitchPreference("argusos_sb_toast_on_skip", summary = true),

            PreferenceCategory(
                key = "argusos_sb_diff_segments",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    categoryPreference("argusos_sb_sponsor_color"),
                    categoryPreference("argusos_sb_selfpromo_color"),
                    categoryPreference("argusos_sb_interaction_color"),
                    categoryPreference("argusos_sb_intro_color"),
                    categoryPreference("argusos_sb_outro_color"),
                    categoryPreference("argusos_sb_preview_color"),
                    categoryPreference("argusos_sb_hook_color"),
                    categoryPreference("argusos_sb_filler_color"),
                    categoryPreference("argusos_sb_music_offtopic_color")
                )
            ),

            PreferenceCategory(
                key = "argusos_sb_general",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("argusos_sb_toast_on_connection_error", summary = true),
                    TextPreference("argusos_sb_api_url")
                )
            ),

            PreferenceCategory(
                key = "argusos_sb_about",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    NonInteractivePreference(
                        key = "argusos_sb_about_api",
                        tag = "app.argusos.extension.shared.sponsorblock.ui.SponsorBlockAboutPreference",
                        selectable = true
                    )
                )
            )
        )
    }
}
