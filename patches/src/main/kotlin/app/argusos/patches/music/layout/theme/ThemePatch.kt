package app.argusos.patches.music.layout.theme

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.shared.layout.theme.THEME_DEFAULT_DARK_COLOR_NAMES
import app.argusos.patches.shared.layout.theme.baseThemePatch
import app.argusos.patches.shared.layout.theme.baseThemeResourcePatch

private const val EXTENSION_CLASS = "Lapp/argusos/extension/music/patches/theme/ThemePatch;"

@Suppress("unused")
val themePatch = baseThemePatch(
    extensionClassDescriptor = EXTENSION_CLASS,
    block = {
        dependsOn(
            sharedExtensionPatch,
            baseThemeResourcePatch(
                darkColorNames = {
                    THEME_DEFAULT_DARK_COLOR_NAMES + setOf(
                        "yt_black_pure",
                        "yt_black_pure_opacity80",
                        "ytm_color_grey_12",
                        "material_grey_800"
                    )
                }
            )
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    }
)