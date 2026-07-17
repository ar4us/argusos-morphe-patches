package app.argusos.patches.youtube.layout.player.fullscreen

import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.youtube.misc.playertype.playerTypeHookPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.video.information.onCreateHook
import app.argusos.patches.youtube.video.information.playerStatusHook
import app.argusos.patches.youtube.video.information.videoInformationPatch
import app.morphe.util.setExtensionIsPatchIncluded

@Suppress("unused")
val openVideosFullscreenPatch = bytecodePatch(
    name = "Open videos fullscreen",
    description = "Adds options to automatically open videos in fullscreen portrait or landscape mode."
) {
    dependsOn(
        openVideosFullscreenHookPatch,
        settingsPatch,
        videoInformationPatch,
        playerTypeHookPatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            ListPreference("argusos_open_videos_fullscreen")
        )

        setExtensionIsPatchIncluded(EXTENSION_CLASS)
        onCreateHook(EXTENSION_CLASS, "initialize")

        playerStatusHook(EXTENSION_CLASS, "playerStatusChanged")
    }
}
