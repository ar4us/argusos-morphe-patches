package app.argusos.patches.music.misc.spoof

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.playservice.is_7_16_or_greater
import app.argusos.patches.music.misc.playservice.is_7_33_or_greater
import app.argusos.patches.music.misc.playservice.is_8_11_or_greater
import app.argusos.patches.music.misc.playservice.is_8_15_or_greater
import app.argusos.patches.music.misc.playservice.is_8_40_or_greater
import app.argusos.patches.music.misc.playservice.is_9_19_or_greater
import app.argusos.patches.music.misc.playservice.is_9_24_or_greater
import app.argusos.patches.music.misc.playservice.versionCheckPatch
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.music.shared.MusicActivityOnCreateFingerprint
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.settings.preference.NonInteractivePreference
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.TextPreference
import app.argusos.patches.shared.misc.spoof.spoofVideoStreamsPatch

val spoofVideoStreamsPatch = spoofVideoStreamsPatch(
    extensionClass = "Lapp/argusos/extension/music/patches/spoof/SpoofVideoStreamsPatch;",
    mainActivityOnCreateFingerprint = MusicActivityOnCreateFingerprint,
    fixMediaFetchHotConfig = { is_7_16_or_greater },
    fixMediaFetchHotConfigAlternative = { is_8_11_or_greater && !is_8_15_or_greater },
    fixParsePlaybackResponseFeatureFlag = { is_7_33_or_greater && !is_9_24_or_greater },
    fixMediaSessionFeatureFlag = { is_8_40_or_greater },
    fixReelItemWatchResponseFeatureFlag = { false },
    hookAccountIdentity = { false },
    useNewRequestBuilderFingerprint = { is_9_19_or_greater },

    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch,
            userAgentClientSpoofPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
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
                        key = "argusos_spoof_video_streams_sign_in_android_vr_about",
                        tag = "app.argusos.extension.music.settings.preference.SpoofVideoStreamsSignInPreference",
                        selectable = true,
                    ),
                    SwitchPreference(
                        "argusos_spoof_video_streams_disable_player_js_update",
                        summary = true,
                        tag = "app.argusos.extension.shared.settings.preference.BulletPointSwitchPreference",
                    ),
                    TextPreference("argusos_spoof_video_streams_player_js_hash_value"),
                )
            )
        )
    }
)
