/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.argusos.extension.youtube.patches.components;

import static app.argusos.extension.youtube.patches.OpenSystemShareSheetPatch.closeLithoAppShareSheet;

import app.argusos.extension.shared.patches.components.BufferAsciiStrings;
import app.argusos.extension.shared.patches.components.ContextInterface;
import app.argusos.extension.shared.patches.components.Filter;
import app.argusos.extension.shared.patches.components.StringFilterGroup;
import app.argusos.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class SystemShareSheetFilter extends Filter {

    public SystemShareSheetFilter() {
        addPathCallbacks(new StringFilterGroup(
                Settings.OPEN_SYSTEM_SHARE_SHEET,
                "share_sheet_container.e"
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
        closeLithoAppShareSheet();

        return true;
    }
}
