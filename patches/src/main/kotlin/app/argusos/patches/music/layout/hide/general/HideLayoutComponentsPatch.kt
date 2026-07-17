/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.argusos.patches.music.layout.hide.general

import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.litho.filter.lithoFilterPatch
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.shared.misc.litho.filter.addLithoFilter
import app.argusos.patches.shared.misc.settings.preference.InputType
import app.argusos.patches.shared.misc.settings.preference.PreferenceCategory
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference.Sorting
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.TextPreference

private const val LAYOUT_COMPONENTS_FILTER =
    "Lapp/argusos/extension/music/patches/components/LayoutComponentsFilter;"
private const val CUSTOM_FILTER =
    "Lapp/argusos/extension/music/patches/components/CustomFilter;"

@Suppress("unused")
val hideLayoutComponentsPatch = bytecodePatch(
    name = "Hide layout components",
    description = "Adds options to hide general layout components."
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        lithoFilterPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)

    execute {
        PreferenceScreen.PLAYER.addPreferences(
            PreferenceCategory(
                titleKey = "argusos_music_hide_lyrics_panel_category_title",
                preferences = setOf(
                    SwitchPreference("argusos_music_hide_lyrics_share_button"),
                    SwitchPreference("argusos_music_hide_lyrics_translate_button")
                )
            )
        )

        PreferenceScreen.GENERAL.addPreferences(
            PreferenceScreenPreference(
                key = "argusos_music_custom_filter_screen",
                titleKey = "argusos_custom_filter_screen_title",
                summaryKey = "argusos_custom_filter_screen_summary",
                sorting = Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference(
                        key = "argusos_music_custom_filter",
                        titleKey = "argusos_custom_filter_title"
                    ),
                    TextPreference(
                        key = "argusos_music_custom_filter_strings",
                        titleKey = "argusos_custom_filter_strings_title",
                        summaryKey = "argusos_custom_filter_strings_summary",
                        inputType = InputType.TEXT_MULTI_LINE
                    )
                )
            )
        )

        addLithoFilter(LAYOUT_COMPONENTS_FILTER)
        addLithoFilter(CUSTOM_FILTER)
    }
}
