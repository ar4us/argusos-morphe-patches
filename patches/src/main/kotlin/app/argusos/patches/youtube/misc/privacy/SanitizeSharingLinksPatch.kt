package app.argusos.patches.youtube.misc.privacy

import app.argusos.patches.shared.misc.privacy.sanitizeSharingLinksPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE

@Suppress("unused")
val sanitizeSharingLinksPatch = sanitizeSharingLinksPatch(
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
        )

        compatibleWith(COMPATIBILITY_YOUTUBE)
    },
    preferenceScreen = PreferenceScreen.MISC,
    replaceLinksWithShortener = true
)
