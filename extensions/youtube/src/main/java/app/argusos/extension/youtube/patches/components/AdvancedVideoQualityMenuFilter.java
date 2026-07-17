package app.argusos.extension.youtube.patches.components;

import app.argusos.extension.shared.patches.components.BufferAsciiStrings;
import app.argusos.extension.shared.patches.components.ContextInterface;
import app.argusos.extension.shared.patches.components.Filter;
import app.argusos.extension.shared.patches.components.StringFilterGroup;
import app.argusos.extension.youtube.patches.playback.quality.AdvancedVideoQualityMenuPatch;
import app.argusos.extension.youtube.settings.Settings;

/**
 * LithoFilter for {@link AdvancedVideoQualityMenuPatch}.
 */
public final class AdvancedVideoQualityMenuFilter extends Filter {
    // Must be volatile or synchronized, as litho filtering runs off main thread
    // and this field is then access from the main thread.
    public static volatile boolean isVideoQualityMenuVisible;

    public AdvancedVideoQualityMenuFilter() {
        addPathCallbacks(new StringFilterGroup(
                Settings.ADVANCED_VIDEO_QUALITY_MENU,
                "quick_quality_sheet_content.e"
        ));
    }

    @Override
    public boolean isFiltered(ContextInterface contextInterface,
                              String identifier,
                              String accessibility,
                              String path,
                              byte[] buffer,
                              BufferAsciiStrings asciiStrings,
                              StringFilterGroup matchedGroup,
                              FilterContentType contentType,
                              int contentIndex) {
        isVideoQualityMenuVisible = true;

        return false;
    }
}
