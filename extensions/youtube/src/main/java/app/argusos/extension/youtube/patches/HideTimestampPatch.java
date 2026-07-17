package app.argusos.extension.youtube.patches;

import app.argusos.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class HideTimestampPatch {
    public static boolean hideTimestamp() {
        return Settings.HIDE_TIMESTAMP.get();
    }
}
