@file:Suppress("SpellCheckingInspection")

package app.argusos.patches.youtube.interaction.playall

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.noTitleUnsortedPreferenceCategory
import app.argusos.patches.youtube.layout.buttons.overlay.addPlayerOverlayPreferences
import app.argusos.patches.youtube.layout.buttons.overlay.playerOverlayButtonsSettingsPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playercontrols.addTopControl
import app.argusos.patches.youtube.misc.playercontrols.initializeTopControl
import app.argusos.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.argusos.patches.youtube.misc.playercontrols.legacyPlayerControlsPatch
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.video.information.videoInformationPatch
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources

private val playAllButtonResourcePatch = resourcePatch {
    dependsOn(
        settingsPatch,
        legacyPlayerControlsPatch
    )

    execute {

        copyResources(
            "playallbutton",
            ResourceGroup(
                "drawable",
                "argusos_play_all_button.xml",
                "argusos_play_all_button_bold.xml"
            )
        )
    }

    finalize {
        addTopControl("playallbutton",
            "@+id/argusos_play_all_button",
            "@+id/argusos_play_all_button")
    }
}

private const val EXTENSION_BUTTON = "Lapp/argusos/extension/youtube/videoplayer/PlayAllButton;"

@Suppress("unused")
val playAllButtonPatch = bytecodePatch(
    name = "Play all",
    description = "Adds an option to play all the videos from a channel and to display play all button in the video player.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        playAllButtonResourcePatch,
        playerOverlayButtonsSettingsPatch,
        videoInformationPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        addPlayerOverlayPreferences(
            noTitleUnsortedPreferenceCategory(
                SwitchPreference("argusos_play_all_button", summary = true),
                ListPreference("argusos_play_all_button_type")
            )
        )

        initializeTopControl(EXTENSION_BUTTON)
        injectVisibilityCheckCall(EXTENSION_BUTTON)
    }
}
