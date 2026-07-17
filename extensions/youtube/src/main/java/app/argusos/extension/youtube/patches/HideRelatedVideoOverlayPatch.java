package app.argusos.extension.youtube.patches;

import app.argusos.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class HideRelatedVideoOverlayPatch {
    /**
     * Injection point.
     */
    public static boolean hideRelatedVideoOverlay() {
        return Settings.HIDE_PLAYER_RELATED_VIDEOS_OVERLAY.get();
    }
}
