package app.argusos.patches.youtube.misc.dns

import app.argusos.patches.shared.misc.dns.checkWatchHistoryDomainNameResolutionPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

val checkWatchHistoryDomainNameResolutionPatch = checkWatchHistoryDomainNameResolutionPatch(
    block = {
        dependsOn(sharedExtensionPatch)

        compatibleWith(COMPATIBILITY_YOUTUBE)
    },
    mainActivityFingerprint = YouTubeActivityOnCreateFingerprint
)
