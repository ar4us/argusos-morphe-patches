package app.argusos.extension.music.patches.spoof;

import static app.argusos.extension.music.settings.Settings.SPOOF_VIDEO_STREAMS_CLIENT_TYPE;
import static app.argusos.extension.shared.spoof.ClientType.ANDROID_REEL_NO_AUTH;
import static app.argusos.extension.shared.spoof.ClientType.ANDROID_VR_1_64;
import static app.argusos.extension.shared.spoof.ClientType.TV;
import static app.argusos.extension.shared.spoof.ClientType.VISIONOS;

import java.util.List;

import app.argusos.extension.shared.spoof.ClientType;

@SuppressWarnings("unused")
public class SpoofVideoStreamsPatch {

    /**
     * Injection point.
     */
    public static void setClientOrderToUse() {
        List<ClientType> availableClients = List.of(
                TV,
                ANDROID_VR_1_64,
                VISIONOS,
                ANDROID_REEL_NO_AUTH
        );

        app.argusos.extension.shared.spoof.SpoofVideoStreamsPatch.setClientsToUse(
                availableClients, SPOOF_VIDEO_STREAMS_CLIENT_TYPE.get());
    }
}
