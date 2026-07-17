package app.argusos.patches.music.layout.compactheader

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val EXTENSION_CLASS = "Lapp/argusos/extension/music/patches/HideFilterBarPatch;"

@Suppress("unused")
val hideFilterBarPatch = bytecodePatch(
    name = "Hide filter bar",
    description = "Adds an option to hide the filter bar at the top of the homepage."
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("argusos_music_hide_filter_bar"),
        )

        ChipCloudFingerprint.method.apply {
            val targetIndex = ChipCloudFingerprint.instructionMatches.last().index
            val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

            addInstruction(
                targetIndex + 1,
                "invoke-static { v$targetRegister }, $EXTENSION_CLASS->hideFilterBar(Landroid/view/View;)V"
            )
        }
    }
}
