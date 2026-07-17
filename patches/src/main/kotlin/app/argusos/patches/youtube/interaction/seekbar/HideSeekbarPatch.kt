package app.argusos.patches.youtube.interaction.seekbar

import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.youtube.layout.seekbar.seekbarColorPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playservice.is_20_28_or_greater
import app.argusos.patches.youtube.misc.playservice.versionCheckPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.SeekbarOnDrawFingerprint
import app.morphe.util.insertLiteralOverride

private const val EXTENSION_CLASS = "Lapp/argusos/extension/youtube/patches/HideSeekbarPatch;"

val hideSeekbarPatch = bytecodePatch(
    description = "Adds an option to hide the seekbar.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        seekbarColorPatch,
        versionCheckPatch
    )

    execute {
        PreferenceScreen.SEEKBAR.addPreferences(
            SwitchPreference("argusos_hide_seekbar"),
            SwitchPreference("argusos_hide_seekbar_thumbnail", summary = true),
            SwitchPreference("argusos_fullscreen_large_seekbar"),
        )

        SeekbarOnDrawFingerprint.method.addInstructionsWithLabels(
            0,
            """
                const/4 v0, 0x0
                invoke-static { }, $EXTENSION_CLASS->hideSeekbar()Z
                move-result v0
                if-eqz v0, :hide_seekbar
                return-void
                :hide_seekbar
                nop
            """
        )

        if (is_20_28_or_greater) {
            FullscreenLargeSeekbarFeatureFlagFingerprint.let {
                it.method.insertLiteralOverride(
                    it.instructionMatches.first().index,
                    "$EXTENSION_CLASS->useFullscreenLargeSeekbar(Z)Z"
                )
            }
        }
    }
}
