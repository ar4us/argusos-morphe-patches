package app.argusos.patches.music.misc.audio

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.playservice.is_8_05_or_greater
import app.argusos.patches.music.misc.playservice.is_9_26_or_greater
import app.argusos.patches.music.misc.playservice.versionCheckPatch
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.music.shared.MusicActivityOnCreateFingerprint
import app.argusos.patches.shared.misc.audio.tracks.forceOriginalAudioPatch

@Suppress("unused")
val forceOriginalAudioPatch = forceOriginalAudioPatch(
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },
    fixUseLocalizedAudioTrackFlag = { is_8_05_or_greater && !is_9_26_or_greater },
    mainActivityOnCreateFingerprint = MusicActivityOnCreateFingerprint,
    subclassExtensionClassDescriptor = "Lapp/argusos/extension/music/patches/ForceOriginalAudioPatch;",
    preferenceScreen = PreferenceScreen.MISC,
)
