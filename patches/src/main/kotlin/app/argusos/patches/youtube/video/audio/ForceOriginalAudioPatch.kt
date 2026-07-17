package app.argusos.patches.youtube.video.audio

import app.argusos.patches.shared.misc.audio.tracks.forceOriginalAudioPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playservice.is_21_26_or_greater
import app.argusos.patches.youtube.misc.playservice.versionCheckPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

@Suppress("unused")
val forceOriginalAudioPatch = forceOriginalAudioPatch(
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE)
    },
    // Localized audio track flag was removed in 21.26+ but might be replaced with 45673827L
    fixUseLocalizedAudioTrackFlag = { !is_21_26_or_greater },
    mainActivityOnCreateFingerprint = YouTubeActivityOnCreateFingerprint,
    subclassExtensionClassDescriptor = "Lapp/argusos/extension/youtube/patches/ForceOriginalAudioPatch;",
    preferenceScreen = PreferenceScreen.VIDEO,
)
