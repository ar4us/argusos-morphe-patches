package app.argusos.patches.youtube.interaction.copyvideolink

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.noTitleUnsortedPreferenceCategory
import app.argusos.patches.youtube.layout.buttons.overlay.addPlayerOverlayPreferences
import app.argusos.patches.youtube.layout.buttons.overlay.playerOverlayButtonsSettingsPatch
import app.argusos.patches.youtube.layout.player.buttons.addPlayerBottomButton
import app.argusos.patches.youtube.layout.player.buttons.playerOverlayButtonsHookPatch
import app.argusos.patches.youtube.misc.playercontrols.addLegacyBottomControl
import app.argusos.patches.youtube.misc.playercontrols.initializeLegacyBottomControl
import app.argusos.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.argusos.patches.youtube.misc.playercontrols.legacyPlayerControlsPatch
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.video.information.videoInformationPatch
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources

private const val EXTENSION_BUTTON = "Lapp/argusos/extension/youtube/videoplayer/CopyVideoLinkButton;"

private val copyVideoLinkButtonResourcePatch = resourcePatch {
    dependsOn(
        settingsPatch,
        legacyPlayerControlsPatch
    )

    execute {
        copyResources(
            "copyvideolinkbutton",
            ResourceGroup(
                resourceDirectoryName = "drawable",
                "argusos_yt_copy.xml",
                "argusos_yt_copy_timestamp.xml",
                "argusos_yt_copy_bold.xml",
                "argusos_yt_copy_timestamp_bold.xml"
            )
        )

        addLegacyBottomControl("copyvideolinkbutton")
    }
}

@Suppress("unused")
val copyVideoLinkButtonPatch = bytecodePatch(
    name = "Copy video link",
    description = "Adds options to display buttons in the video player to copy video links.",
) {
    dependsOn(
        copyVideoLinkButtonResourcePatch,
        playerOverlayButtonsSettingsPatch,
        playerOverlayButtonsHookPatch,
        legacyPlayerControlsPatch,
        videoInformationPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        addPlayerOverlayPreferences(
            noTitleUnsortedPreferenceCategory(
                SwitchPreference("argusos_copy_video_link_button", summary = true),
                SwitchPreference("argusos_copy_video_link_with_timestamp_button", summary = true)
            )
        )

        addPlayerBottomButton(EXTENSION_BUTTON)

        initializeLegacyBottomControl(EXTENSION_BUTTON)
        injectVisibilityCheckCall(EXTENSION_BUTTON)
    }
}
