package app.argusos.patches.youtube.video.speed.button

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.youtube.layout.buttons.overlay.addPlayerOverlayPreferences
import app.argusos.patches.youtube.layout.buttons.overlay.playerOverlayButtonsSettingsPatch
import app.argusos.patches.youtube.layout.player.buttons.addPlayerBottomButton
import app.argusos.patches.youtube.layout.player.buttons.playerOverlayButtonsHookPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playercontrols.addLegacyBottomControl
import app.argusos.patches.youtube.misc.playercontrols.initializeLegacyBottomControl
import app.argusos.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.argusos.patches.youtube.misc.playercontrols.legacyPlayerControlsPatch
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.video.information.userSelectedPlaybackSpeedHook
import app.argusos.patches.youtube.video.information.videoInformationPatch
import app.argusos.patches.youtube.video.information.videoSpeedChangedHook
import app.argusos.patches.youtube.video.speed.custom.customPlaybackSpeedPatch
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources

private val playbackSpeedButtonResourcePatch = resourcePatch {
    dependsOn(legacyPlayerControlsPatch)

    execute {
        copyResources(
            "speedbutton",
            ResourceGroup(
                "drawable",
                "argusos_playback_speed_dialog_button_rectangle.xml"
            )
        )

        addLegacyBottomControl("speedbutton")
    }
}

private const val EXTENSION_BUTTON =
    "Lapp/argusos/extension/youtube/videoplayer/PlaybackSpeedDialogButton;"

val playbackSpeedButtonPatch = bytecodePatch(
    description = "Adds the option to display playback speed dialog button in the video player.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        playerOverlayButtonsSettingsPatch,
        customPlaybackSpeedPatch,
        playbackSpeedButtonResourcePatch,
        playerOverlayButtonsHookPatch,
        videoInformationPatch,
    )

    execute {
        addPlayerOverlayPreferences(
            SwitchPreference("argusos_playback_speed_dialog_button", summary = true)
        )

        addPlayerBottomButton(EXTENSION_BUTTON)

        initializeLegacyBottomControl(EXTENSION_BUTTON)
        injectVisibilityCheckCall(EXTENSION_BUTTON)

        videoSpeedChangedHook(EXTENSION_BUTTON, "videoSpeedChanged")
        userSelectedPlaybackSpeedHook(EXTENSION_BUTTON, "videoSpeedChanged")
    }
}
