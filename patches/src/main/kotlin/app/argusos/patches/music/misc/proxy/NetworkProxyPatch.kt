package app.argusos.patches.music.misc.proxy

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.playservice.is_8_51_or_greater
import app.argusos.patches.music.misc.playservice.is_9_20_or_greater
import app.argusos.patches.music.misc.playservice.versionCheckPatch
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.shared.misc.proxy.baseNetworkProxyPatch

@Suppress("unused")
val networkProxyPatch = baseNetworkProxyPatch(
    preferenceScreen = PreferenceScreen.MISC,
    targetUsesProxyListInt = {
        is_9_20_or_greater
    },
    patchNotCompatibleMessage = {
        if (is_8_51_or_greater) {
            null
        } else {
            "Network proxy requires YouTube Music 8.51.51 or newer."
        }
    },
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch,
            versionCheckPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    }
)
