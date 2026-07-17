package app.argusos.patches.youtube.misc.gms

import app.argusos.patches.shared.CastContextFetchFingerprint
import app.argusos.patches.shared.PrimeMethodFingerprint
import app.argusos.patches.shared.misc.gms.gmsCoreSupportPatch
import app.argusos.patches.youtube.layout.buttons.overlay.hidePlayerOverlayButtonsPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.gms.Constants.ARGUSOS_YOUTUBE_PACKAGE_NAME
import app.argusos.patches.youtube.misc.gms.Constants.YOUTUBE_PACKAGE_NAME
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.misc.spoof.spoofVideoStreamsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.shared.YouTubeActivityOnCreateFingerprint

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = YOUTUBE_PACKAGE_NAME,
    toPackageNameDefault = ARGUSOS_YOUTUBE_PACKAGE_NAME,
    primeMethodFingerprint = PrimeMethodFingerprint,
    earlyReturnFingerprints = setOf(
        CastContextFetchFingerprint,
    ),
    mainActivityOnCreateFingerprint = YouTubeActivityOnCreateFingerprint,
    extensionPatch = sharedExtensionPatch,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    dependsOn(
        sharedExtensionPatch,
        hidePlayerOverlayButtonsPatch, // Hide non-functional cast button.
        spoofVideoStreamsPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)
}

private fun gmsCoreSupportResourcePatch() =
    app.argusos.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
        fromPackageName = YOUTUBE_PACKAGE_NAME,
        toPackageNameDefault = ARGUSOS_YOUTUBE_PACKAGE_NAME,
        spoofedPackageSignature = "24bb24c05e47e0aefa68a58a766179d9b613a600",
        screen = PreferenceScreen.MISC,
        block = {
            dependsOn(
                settingsPatch,
                accountCredentialsInvalidTextPatch
            )
        }
    )
