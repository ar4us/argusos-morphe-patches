package app.argusos.extension.youtube.patches;

import app.argusos.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class TapToSeekPatch {
    public static boolean tapToSeekEnabled() {
        return Settings.TAP_TO_SEEK.get();
    }
}
