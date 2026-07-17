package app.argusos.patches.music.misc.settings

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patches.all.misc.fix.openurllinks.removeLinkVerification
import app.argusos.patches.all.misc.packagename.setOrGetFallbackPackageName
import app.argusos.patches.all.misc.resources.addAppResources
import app.argusos.patches.all.misc.resources.addResourcesPatch
import app.argusos.patches.all.misc.resources.localesYouTube
import app.morphe.patches.all.misc.resources.resourceMappingPatch
import app.argusos.patches.all.misc.resources.setAddResourceLocale
import app.argusos.patches.all.misc.updates.checkPatcherUpToDatePatch
import app.argusos.patches.music.misc.extension.hooks.youTubeMusicApplicationInitOnCreateHook
import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.music.misc.gms.Constants.MUSIC_PACKAGE_NAME
import app.argusos.patches.music.misc.playservice.is_8_40_or_greater
import app.argusos.patches.music.misc.playservice.versionCheckPatch
import app.argusos.patches.music.shared.Constants.COMPATIBILITY_YOUTUBE_MUSIC
import app.argusos.patches.shared.BoldIconsFeatureFlagFingerprint
import app.argusos.patches.shared.GoogleApiActivityOnCreateFingerprint
import app.argusos.patches.shared.misc.checks.experimentalAppNoticePatch
import app.argusos.patches.shared.misc.initialization.initializationPatch
import app.argusos.patches.shared.misc.settings.ARGUSOS_SETTINGS_INTENT
import app.argusos.patches.shared.misc.settings.preference.BasePreference
import app.argusos.patches.shared.misc.settings.preference.BasePreferenceScreen
import app.argusos.patches.shared.misc.settings.preference.InputType
import app.argusos.patches.shared.misc.settings.preference.IntentPreference
import app.argusos.patches.shared.misc.settings.preference.NonInteractivePreference
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference.Sorting
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.settings.preference.TextPreference
import app.argusos.patches.shared.misc.settings.settingsPatch
import app.argusos.patches.youtube.misc.settings.modifyActivityForSettingsInjection
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources
import app.morphe.util.copyXmlNode
import app.morphe.util.inputStreamFromBundledResource
import app.morphe.util.insertLiteralOverride

private const val MUSIC_ACTIVITY_HOOK_CLASS = "Lapp/argusos/extension/music/settings/MusicActivityHook;"

private val preferences = mutableSetOf<BasePreference>()

private val settingsResourcePatch = resourcePatch {
    dependsOn(
        resourceMappingPatch,
        settingsPatch(
            rootPreferences = listOf(
                IntentPreference(
                    titleKey = "argusos_settings_title",
                    summaryKey = null,
                    intent = newIntent(ARGUSOS_SETTINGS_INTENT),
                ) to "settings_headers"
            ),
            preferences = preferences
        )
    )

    execute {
        copyResources(
            "settings",
            ResourceGroup("drawable",
                "argusos_settings_screen_00_about.xml",
                "argusos_settings_screen_00_about_bold.xml",
                "argusos_settings_screen_01_ads.xml",
                "argusos_settings_screen_01_ads_bold.xml",
                "argusos_settings_screen_04_general.xml",
                "argusos_settings_screen_04_general_bold.xml",
                "argusos_settings_screen_05_player.xml",
                "argusos_settings_screen_05_player_bold.xml",
                "argusos_settings_screen_10_sponsorblock.xml",
                "argusos_settings_screen_10_sponsorblock_bold.xml",
                "argusos_settings_screen_11_misc.xml",
                "argusos_settings_screen_11_misc_bold.xml",
                "argusos_settings_screen_13_scrobbling.xml",
                "argusos_settings_screen_13_scrobbling_bold.xml"
            ),
            ResourceGroup("layout",
                "argusos_preference_with_icon.xml",
                "argusos_color_dot_widget.xml"
            )
        )

        // Set the style for the Morphe settings to follow the style of the music settings,
        // namely: action bar height, menu item padding and remove horizontal dividers.
        val targetResource = "values/styles.xml"
        inputStreamFromBundledResource(
            "settings/music",
            targetResource
        )!!.let { inputStream ->
            "resources".copyXmlNode(
                document(inputStream),
                document("res/$targetResource")
            ).close()
        }

        // Remove horizontal dividers from the music settings.
        val styleFile = get("res/values/styles.xml")
        styleFile.writeText(
            styleFile.readText()
                .replace(
                    "allowDividerAbove\">true",
                    "allowDividerAbove\">false"
                ).replace(
                    "allowDividerBelow\">true",
                    "allowDividerBelow\">false"
                )
        )
    }
}

val settingsPatch = bytecodePatch(
    description = "Adds settings for Morphe to YouTube Music."
) {
    dependsOn(
        checkPatcherUpToDatePatch,
        sharedExtensionPatch,
        settingsResourcePatch,
        addResourcesPatch,
        versionCheckPatch,
        removeLinkVerification,
        experimentalAppNoticePatch(
            mainActivityFingerprint = youTubeMusicApplicationInitOnCreateHook.fingerprint,
            recommendedAppVersion = COMPATIBILITY_YOUTUBE_MUSIC.targets.first { !it.isExperimental }.version!!
        ),
        initializationPatch(
            extensionPatch = sharedExtensionPatch
        )
    )

    execute {
        setAddResourceLocale(localesYouTube)
        addAppResources("shared-youtube")
        addAppResources("music")

        // Add an "About" preference to the top.
        preferences += NonInteractivePreference(
            key = "argusos_settings_music_screen_0_about",
            summaryKey = null,
            icon = "@drawable/argusos_settings_screen_00_about",
            iconBold = "@drawable/argusos_settings_screen_00_about_bold",
            layout = "@layout/argusos_preference_with_icon",
            tag = "app.argusos.extension.shared.settings.preference.about.MorpheAboutPreference",
            selectable = true
        )

        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("argusos_settings_search_history"),
            SwitchPreference("argusos_show_menu_icons")
        )

        PreferenceScreen.MISC.addPreferences(
            TextPreference(
                key = null,
                titleKey = "argusos_pref_import_export_title",
                summaryKey = "argusos_pref_import_export_summary",
                inputType = InputType.TEXT_MULTI_LINE,
                tag = "app.argusos.extension.shared.settings.preference.ImportExportPreference"
            ),
            ListPreference(
                key = "argusos_language",
                tag = "app.argusos.extension.shared.settings.preference.SortedListPreference"
            )
        )

        modifyActivityForSettingsInjection(
            GoogleApiActivityOnCreateFingerprint,
            MUSIC_ACTIVITY_HOOK_CLASS,
            true
        )

        // TODO: Implement a 'Spoof app version' patch for YouTube Music.
        if (is_8_40_or_greater) {
            BoldIconsFeatureFlagFingerprint.let {
                it.method.insertLiteralOverride(
                    it.instructionMatches.first().index,
                    "$MUSIC_ACTIVITY_HOOK_CLASS->useBoldIcons(Z)Z"
                )
            }
        }
    }

    finalize {
        PreferenceScreen.close()
    }
}

/**
 * Creates an intent to open Morphe settings.
 */
fun newIntent(settingsName: String) = IntentPreference.Intent(
    data = settingsName,
    targetClass = "com.google.android.gms.common.api.GoogleApiActivity"
) {
    // The package name change has to be reflected in the intent.
    setOrGetFallbackPackageName(MUSIC_PACKAGE_NAME)
}

object PreferenceScreen : BasePreferenceScreen() {
    val ADS = Screen(
        key = "argusos_settings_music_screen_1_ads",
        summaryKey = null,
        icon = "@drawable/argusos_settings_screen_01_ads",
        iconBold = "@drawable/argusos_settings_screen_01_ads_bold",
        layout = "@layout/argusos_preference_with_icon"
    )
    val GENERAL = Screen(
        key = "argusos_settings_music_screen_2_general",
        summaryKey = null,
        icon = "@drawable/argusos_settings_screen_04_general",
        iconBold = "@drawable/argusos_settings_screen_04_general_bold",
        layout = "@layout/argusos_preference_with_icon"
    )
    val PLAYER = Screen(
        key = "argusos_settings_music_screen_3_player",
        summaryKey = null,
        icon = "@drawable/argusos_settings_screen_05_player",
        iconBold = "@drawable/argusos_settings_screen_05_player_bold",
        layout = "@layout/argusos_preference_with_icon"
    )
    val SCROBBLING = Screen(
        key = "argusos_settings_music_screen_4_scrobbling",
        summaryKey = null,
        icon = "@drawable/argusos_settings_screen_13_scrobbling",
        iconBold = "@drawable/argusos_settings_screen_13_scrobbling_bold",
        layout = "@layout/argusos_preference_with_icon",
        sorting = Sorting.UNSORTED
    )
    val SPONSORBLOCK = Screen(
        key = "argusos_settings_music_screen_5_sponsorblock",
        summaryKey = null,
        icon = "@drawable/argusos_settings_screen_10_sponsorblock",
        iconBold = "@drawable/argusos_settings_screen_10_sponsorblock_bold",
        layout = "@layout/argusos_preference_with_icon",
        sorting = Sorting.UNSORTED
    )
    val MISC = Screen(
        key = "argusos_settings_music_screen_6_misc",
        summaryKey = null,
        icon = "@drawable/argusos_settings_screen_11_misc",
        iconBold = "@drawable/argusos_settings_screen_11_misc_bold",
        layout = "@layout/argusos_preference_with_icon"
    )

    override fun commit(screen: PreferenceScreenPreference) {
        preferences += screen
    }
}
