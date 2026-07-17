package app.argusos.patches.youtube.layout.thumbnails

import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.imageurlhook.addImageURLHook
import app.argusos.patches.youtube.misc.imageurlhook.cronetImageURLHookPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE

private const val EXTENSION_CLASS =
    "Lapp/argusos/extension/youtube/patches/BypassImageRegionRestrictionsPatch;"

val bypassImageRegionRestrictionsPatch = bytecodePatch(
    name = "Bypass image region restrictions",
    description = "Adds an option to use a different host for user avatar and channel images " +
        "and can fix missing images that are blocked in some countries.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        cronetImageURLHookPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.MISC.addPreferences(
            SwitchPreference("argusos_bypass_image_region_restrictions", summary = true),
        )

        // A priority hook is not needed, as the image URLs of interest are not modified
        // by AlternativeThumbnails or any other patch in this repo.
        addImageURLHook(EXTENSION_CLASS)
    }
}
