package app.argusos.patches.youtube.video.quality

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
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources

private val videoQualityButtonResourcePatch = resourcePatch {
    dependsOn(legacyPlayerControlsPatch)

    execute {
        copyResources(
            "qualitybutton",
            ResourceGroup(
                "drawable",
                "argusos_video_quality_dialog_button_rectangle.xml"
            ),
        )

        addLegacyBottomControl("qualitybutton")
    }
}

private const val EXTENSION_BUTTON =
    "Lapp/argusos/extension/youtube/videoplayer/VideoQualityDialogButton;"

val videoQualityDialogButtonPatch = bytecodePatch(
    description = "Adds the option to display video quality dialog button in the video player.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        playerOverlayButtonsSettingsPatch,
        rememberVideoQualityPatch,
        videoQualityButtonResourcePatch,
        playerOverlayButtonsHookPatch,
        legacyPlayerControlsPatch,
    )

    execute {
        addPlayerOverlayPreferences(
            SwitchPreference("argusos_video_quality_dialog_button", summary = true)
        )

        addPlayerBottomButton(EXTENSION_BUTTON)

        initializeLegacyBottomControl(EXTENSION_BUTTON)
        injectVisibilityCheckCall(EXTENSION_BUTTON)
    }
}
