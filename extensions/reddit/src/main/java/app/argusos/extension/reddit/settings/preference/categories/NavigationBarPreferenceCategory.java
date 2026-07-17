/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */
package app.argusos.extension.reddit.settings.preference.categories;

import static app.morphe.extension.shared.StringRef.str;

import android.content.Context;
import android.preference.PreferenceScreen;

import app.argusos.extension.reddit.patches.HideNavigationButtonsPatch;
import app.argusos.extension.reddit.settings.Settings;
import app.argusos.extension.reddit.settings.preference.BooleanSettingPreference;

@SuppressWarnings("deprecation")
public class NavigationBarPreferenceCategory extends ConditionalPreferenceCategory {
    public NavigationBarPreferenceCategory(Context context, PreferenceScreen screen) {
        super(context, screen);
        setTitle(str("morphe_screen_navigation_bar_title"));
    }

    @Override
    public boolean getSettingsStatus() {
        return HideNavigationButtonsPatch.isPatchIncluded();
    }

    @Override
    public void addPreferences(Context context) {
        addPreference(new BooleanSettingPreference(
                context,
                Settings.HIDE_ANSWERS_BUTTON
        ));
        addPreference(new BooleanSettingPreference(
                context,
                Settings.HIDE_CHAT_BUTTON
        ));
        addPreference(new BooleanSettingPreference(
                context,
                Settings.HIDE_CREATE_BUTTON
        ));
        addPreference(new BooleanSettingPreference(
                context,
                Settings.HIDE_DISCOVER_BUTTON
        ));
        addPreference(new BooleanSettingPreference(
                context,
                Settings.HIDE_GAMES_BUTTON
        ));
    }
}
