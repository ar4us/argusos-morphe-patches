package app.argusos.patches.youtube.interaction.loop

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.youtube.layout.buttons.overlay.addPlayerOverlayPreferences
import app.argusos.patches.youtube.layout.buttons.overlay.playerOverlayButtonsSettingsPatch
import app.argusos.patches.youtube.layout.captions.StartVideoInformerFingerprint
import app.argusos.patches.youtube.layout.player.buttons.playerOverlayButtonsHookPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playercontrols.addTopControl
import app.argusos.patches.youtube.misc.playercontrols.initializeTopControl
import app.argusos.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.argusos.patches.youtube.misc.playercontrols.legacyPlayerControlsPatch
import app.argusos.patches.youtube.misc.playercontrols.legacyPlayerControlsResourcePatch
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources

private val loopVideoButtonResourcePatch = resourcePatch {
    dependsOn(legacyPlayerControlsResourcePatch)

    execute {
        copyResources(
            "loopvideobutton",
            ResourceGroup(
                "drawable",
                "argusos_loop_video_button_on.xml",
                "argusos_loop_video_button_off.xml",
                "argusos_loop_video_button_on_bold.xml",
                "argusos_loop_video_button_off_bold.xml",
                "argusos_loop_video_button_range.xml",
                "argusos_loop_video_button_range_bold.xml"
            )
        )
    }

    finalize {
        addTopControl(
            "loopvideobutton",
            "@+id/argusos_loop_video_button",
            "@+id/argusos_loop_video_button"
        )
    }
}

private const val EXTENSION_BUTTON =
    "Lapp/argusos/extension/youtube/videoplayer/LoopVideoButton;"

internal val loopVideoButtonPatch = bytecodePatch(
    description = "Adds an option to display loop video button in the video player."
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        loopVideoButtonResourcePatch,
        playerOverlayButtonsSettingsPatch,
        legacyPlayerControlsPatch,
        playerOverlayButtonsHookPatch
    )

    execute {
        addPlayerOverlayPreferences(
            SwitchPreference("argusos_loop_video_button")
        )

        initializeTopControl(EXTENSION_BUTTON)
        injectVisibilityCheckCall(EXTENSION_BUTTON)
        StartVideoInformerFingerprint.method.addInstruction(
            0,
            "invoke-static { }, $EXTENSION_BUTTON->resetLoopButton()V"
        )
    }
}
