/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to Morphe contributions.
 */

package app.argusos.patches.youtube.video.voiceovertranslation

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.settings.preference.NonInteractivePreference
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.TextPreference
import app.argusos.patches.youtube.layout.player.buttons.addPlayerBottomButton
import app.argusos.patches.youtube.layout.player.buttons.playerOverlayButtonsHookPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playercontrols.addLegacyBottomControl
import app.argusos.patches.youtube.misc.playercontrols.initializeLegacyBottomControl
import app.argusos.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.argusos.patches.youtube.misc.playercontrols.legacyPlayerControlsPatch
import app.argusos.patches.youtube.misc.playertype.playerTypeHookPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.video.information.videoInformationPatch
import app.argusos.patches.youtube.video.information.videoTimeHook
import app.argusos.patches.youtube.video.videoid.hookVideoId
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources

private const val EXTENSION_CLASS =
    "Lapp/argusos/extension/youtube/patches/voiceovertranslation/VoiceOverTranslationPatch;"

private const val EXTENSION_BUTTON =
    "Lapp/argusos/extension/youtube/videoplayer/VoiceOverTranslationButton;"

private val voiceOverTranslationResourcePatch = resourcePatch {
    dependsOn(legacyPlayerControlsPatch)

    execute {
        copyResources(
            "voiceovertranslationbutton",
            ResourceGroup(
                "drawable",
                "argusos_yt_vot.xml",
                "argusos_yt_vot_bold.xml",
            )
        )

        addLegacyBottomControl("voiceovertranslationbutton")
    }
}

@Suppress("unused")
val voiceOverTranslationPatch = bytecodePatch(
    name = "Voice over translation",
    description = "Adds additional voice over languages using text-to-speech synchronized to the video playback.",
) {
    dependsOn(
        sharedExtensionPatch,
        videoInformationPatch,
        playerTypeHookPatch,
        playerOverlayButtonsHookPatch,
        legacyPlayerControlsPatch,
        voiceOverTranslationResourcePatch,
        votOriginalVolumeBytecodePatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.VIDEO.addPreferences(
            PreferenceScreenPreference(
                key = "argusos_vot_screen",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("argusos_vot_enabled"),
                    ListPreference("argusos_vot_caption_language"),
                    NonInteractivePreference("argusos_vot_max_speech_rate",
                        tag = "app.argusos.extension.shared.settings.preference.SeekBarPreference",
                        selectable = true),
                    ListPreference("argusos_vot_translation_service"),
                    NonInteractivePreference("argusos_vot_openrouter_info",
                        titleKey = "argusos_vot_service_openrouter",
                        tag = "app.argusos.extension.youtube.settings.preference.VoiceOverTranslationOpenRouterInfoPreference",
                        selectable = true),
                    TextPreference("argusos_vot_openrouter_api_key"),
                    TextPreference("argusos_vot_openrouter_model",
                        summaryKey = null,
                        tag = "app.argusos.extension.youtube.settings.preference.VoiceOverTranslationModelPreference"),
                    NonInteractivePreference("argusos_vot_mymemory_info",
                        titleKey = "argusos_vot_service_mymemory",
                        tag = "app.argusos.extension.youtube.settings.preference.VoiceOverTranslationMyMemoryInfoPreference",
                        selectable = true),
                    TextPreference("argusos_vot_mymemory_email")
                )
            )
        )

        hookVideoId("$EXTENSION_CLASS->newVideoLoaded(Ljava/lang/String;)V")
        videoTimeHook(EXTENSION_CLASS, "videoTimeChanged")

        addPlayerBottomButton(EXTENSION_BUTTON)
        initializeLegacyBottomControl(EXTENSION_BUTTON)
        injectVisibilityCheckCall(EXTENSION_BUTTON)
    }
}
