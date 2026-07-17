package app.argusos.patches.music.misc.gms

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.fileprovider.fileProviderPatch
import app.argusos.patches.music.misc.gms.Constants.ARGUSOS_MUSIC_PACKAGE_NAME
import app.argusos.patches.music.misc.gms.Constants.MUSIC_PACKAGE_NAME
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.misc.spoof.spoofVideoStreamsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.music.shared.MusicActivityOnCreateFingerprint
import app.argusos.patches.shared.CastContextFetchFingerprint
import app.argusos.patches.shared.PrimeMethodFingerprint
import app.argusos.patches.shared.misc.gms.gmsCoreSupportPatch

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = MUSIC_PACKAGE_NAME,
    toPackageNameDefault = ARGUSOS_MUSIC_PACKAGE_NAME,
    primeMethodFingerprint = PrimeMethodFingerprint,
    earlyReturnFingerprints = setOf(
        CastContextFetchFingerprint,
    ),
    mainActivityOnCreateFingerprint = MusicActivityOnCreateFingerprint,
    extensionPatch = sharedExtensionPatch,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    dependsOn(spoofVideoStreamsPatch)

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
}

private fun gmsCoreSupportResourcePatch() =
    app.argusos.patches.shared.misc.gms.gmsCoreSupportResourcePatch(
        fromPackageName = MUSIC_PACKAGE_NAME,
        toPackageNameDefault = ARGUSOS_MUSIC_PACKAGE_NAME,
        spoofedPackageSignature = "afb0fed5eeaebdd86f56a97742f4b6b33ef59875",
        screen = PreferenceScreen.MISC,
        block = {
            dependsOn(
                settingsPatch,
                fileProviderPatch(
                    MUSIC_PACKAGE_NAME,
                    ARGUSOS_MUSIC_PACKAGE_NAME
                )
            )
        }
    )
