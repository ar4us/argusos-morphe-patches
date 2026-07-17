package app.argusos.patches.youtube.misc.spoof

import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.settings.preference.NonInteractivePreference
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.TextPreference
import app.argusos.patches.shared.misc.spoof.spoofVideoStreamsPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playservice.is_20_31_or_greater
import app.argusos.patches.youtube.misc.playservice.is_20_39_or_greater
import app.argusos.patches.youtube.misc.playservice.is_21_21_or_greater
import app.argusos.patches.youtube.misc.playservice.versionCheckPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

val spoofVideoStreamsPatch = spoofVideoStreamsPatch(
    extensionClass = "Lapp/argusos/extension/youtube/patches/spoof/SpoofVideoStreamsPatch;",
    mainActivityOnCreateFingerprint = YouTubeActivityOnCreateFingerprint,
    fixMediaFetchHotConfig = {
        true
    },
    fixMediaFetchHotConfigAlternative = {
        // In 20.14 the flag was merged with 20.03 start playback flag.
        false
    },
    fixParsePlaybackResponseFeatureFlag = {
        true
    },
    fixMediaSessionFeatureFlag = {
        is_20_39_or_greater
    },
    fixReelItemWatchResponseFeatureFlag = {
        // Flag has existed since at least 20.05,
        // but only recently has been causing issues.
        is_20_31_or_greater
    },
    hookAccountIdentity = { true },
    useNewRequestBuilderFingerprint = { is_21_21_or_greater },

    block = {
        compatibleWith(COMPATIBILITY_YOUTUBE)

        dependsOn(
            sharedExtensionPatch,
            userAgentClientSpoofPatch,
            settingsPatch,
            versionCheckPatch
        )
    },

    executeBlock = {

        PreferenceScreen.MISC.addPreferences(
            PreferenceScreenPreference(
                key = "argusos_spoof_video_streams_screen",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("argusos_spoof_video_streams", summary = true),
                    ListPreference("argusos_spoof_video_streams_client_type"),
                    NonInteractivePreference(
                        // Requires a key and title but the actual text is chosen at runtime.
                        key = "argusos_spoof_video_streams_about",
                        summaryKey = null,
                        tag = "app.argusos.extension.youtube.settings.preference.SpoofVideoStreamsSideEffectsPreference"
                    ),
                    NonInteractivePreference(
                        key = "argusos_spoof_video_streams_sign_in_android_vr_about",
                        tag = "app.argusos.extension.youtube.settings.preference.SpoofVideoStreamsSignInPreference",
                        selectable = true,
                    ),
                    SwitchPreference("argusos_spoof_video_streams_av1", summary = true),
                    ListPreference("argusos_spoof_video_streams_player_js_variant"),
                    SwitchPreference(
                        "argusos_spoof_video_streams_disable_player_js_update",
                        summary = true,
                        tag = "app.argusos.extension.shared.settings.preference.BulletPointSwitchPreference",
                    ),
                    TextPreference("argusos_spoof_video_streams_player_js_hash_value"),
                    SwitchPreference("argusos_spoof_video_streams_stats_for_nerds"),
                )
            )
        )
    }
)
