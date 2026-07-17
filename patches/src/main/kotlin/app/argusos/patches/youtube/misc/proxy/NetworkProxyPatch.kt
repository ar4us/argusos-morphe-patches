package app.argusos.patches.youtube.misc.proxy

import app.argusos.patches.shared.misc.proxy.baseNetworkProxyPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playservice.is_20_47_or_greater
import app.argusos.patches.youtube.misc.playservice.is_21_12_or_greater
import app.argusos.patches.youtube.misc.playservice.versionCheckPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE

@Suppress("unused")
val networkProxyPatch = baseNetworkProxyPatch(
    preferenceScreen = PreferenceScreen.MISC,
    targetUsesProxyListInt = {
        is_21_12_or_greater
    },
    patchNotCompatibleMessage = {
        if (is_20_47_or_greater) {
            null
        } else {
            "Network proxy requires YouTube 20.47.62 or newer."
        }
    },
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE)
    }
)
