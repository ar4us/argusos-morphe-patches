package app.argusos.patches.youtube.misc.medianotification

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction

private const val EXTENSION_CLASS =
    "Lapp/argusos/extension/youtube/patches/MediaNotificationControlsPatch;"

@Suppress("unused")
val mediaNotificationControlsPatch = bytecodePatch(
    name = "Media notification controls",
    description = "Adds options to disable the seekbar and previous/next buttons in the " +
            "media notification and headphone controls.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            PreferenceScreenPreference(
                key = "argusos_notification_media_screen",
                preferences = setOf(
                    SwitchPreference("argusos_hide_notification_media_prev_next", summary = true),
                    SwitchPreference("argusos_disable_notification_media_seekbar", summary = true),
                )
            )
        )

        MediaSessionSetPlaybackStateFingerprint.let {
            it.method.apply {
                val index = it.instructionMatches.first().index
                val register = getInstruction<FiveRegisterInstruction>(index).registerD

                addInstructions(
                    index,
                    """
                        invoke-static { v$register }, $EXTENSION_CLASS->changePlaybackState(Landroid/media/session/PlaybackState;)Landroid/media/session/PlaybackState;
                        move-result-object v$register
                    """
                )
            }
        }
    }
}
