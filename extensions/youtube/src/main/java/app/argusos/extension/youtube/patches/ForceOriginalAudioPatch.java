package app.argusos.extension.youtube.patches;

import app.argusos.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class ForceOriginalAudioPatch {

    /**
     * Injection point.
     */
    public static void setEnabled() {
        app.argusos.extension.shared.patches.ForceOriginalAudioPatch.setEnabled(
                Settings.FORCE_ORIGINAL_AUDIO.get(),
                Settings.SPOOF_VIDEO_STREAMS_CLIENT_TYPE.get()
        );
    }
}
