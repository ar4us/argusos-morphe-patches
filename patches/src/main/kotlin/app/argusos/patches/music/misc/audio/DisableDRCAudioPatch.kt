package app.argusos.patches.music.misc.audio

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.playservice.is_9_19_or_greater
import app.argusos.patches.music.misc.playservice.is_9_20_or_greater
import app.argusos.patches.music.misc.playservice.versionCheckPatch
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.shared.misc.audio.drc.disableDRCAudioPatch

@Suppress("unused")
val disableDRCAudioPatch = disableDRCAudioPatch(
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },
    preferenceScreen = PreferenceScreen.MISC,
    useLegacyNormalizationFlag = { !is_9_19_or_greater },
    // Ignore 9.19 because it's missing a flag and requires version specific changes to support.
    useNormalizationFlag = { is_9_20_or_greater }
)
