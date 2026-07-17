package app.argusos.patches.youtube.misc.check

import app.argusos.patches.shared.misc.checks.checkEnvironmentPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

internal val checkEnvironmentPatch = checkEnvironmentPatch(
    mainActivityOnCreateFingerprint = YouTubeActivityOnCreateFingerprint,
    extensionPatch = sharedExtensionPatch
)
