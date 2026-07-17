package app.argusos.patches.music.misc.debugging

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.playservice.is_8_40_or_greater
import app.argusos.patches.music.misc.playservice.is_8_41_or_greater
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.shared.misc.debugging.enableDebuggingPatch
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference

@Suppress("unused")
val enableDebuggingPatch = enableDebuggingPatch(
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },
    // String feature flag does not appear to be present with YT Music.
    hookStringFeatureFlag = { false },
    // 8.40 has changes not worth supporting.
    hookLongFeatureFlag = { !is_8_40_or_greater || is_8_41_or_greater },
    hookDoubleFeatureFlag = { !is_8_40_or_greater || is_8_41_or_greater },
    preferenceScreen = PreferenceScreen.MISC,
    additionalDebugPreferences = listOf(SwitchPreference("argusos_debug_protobuffer", summary = true))
)
