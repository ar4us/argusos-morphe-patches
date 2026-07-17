/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches/pull/1948
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to Morphe contributions.
 */

package app.argusos.patches.music.misc.spoof.appversion

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.settings.PreferenceScreen
import app.argusos.patches.music.misc.settings.settingsPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.spoof.appversion.baseSpoofAppVersionPatch

@Suppress("unused")
val spoofAppVersionPatch = baseSpoofAppVersionPatch(
    defaultTargetString = { "7.17.52" },
    preferenceScreen = PreferenceScreen.GENERAL,
    listPreference = {
        ListPreference(
            key = "argusos_spoof_app_version_target",
            entriesKey = "argusos_music_spoof_app_version_target_entries",
            entryValuesKey = "argusos_music_spoof_app_version_target_entry_values"
        )
    },
    block = {
        dependsOn(
            sharedExtensionPatch,
            settingsPatch
        )

        compatibleWith(COMPATIBILITY_YOUTUBE_MUSIC)
    }
)
