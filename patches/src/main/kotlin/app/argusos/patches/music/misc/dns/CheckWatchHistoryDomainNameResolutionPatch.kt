package app.argusos.patches.music.misc.dns

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.music.shared.MusicActivityOnCreateFingerprint
import app.argusos.patches.shared.misc.dns.checkWatchHistoryDomainNameResolutionPatch

val checkWatchHistoryDomainNameResolutionPatch = checkWatchHistoryDomainNameResolutionPatch(
    block = {
        dependsOn(
            sharedExtensionPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    },

    mainActivityFingerprint = MusicActivityOnCreateFingerprint
)
