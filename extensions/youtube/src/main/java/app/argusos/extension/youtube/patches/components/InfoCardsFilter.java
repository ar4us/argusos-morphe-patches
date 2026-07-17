package app.argusos.extension.youtube.patches.components;

import app.argusos.extension.shared.patches.components.Filter;
import app.argusos.extension.shared.patches.components.StringFilterGroup;

import app.argusos.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public final class InfoCardsFilter extends Filter {

    public InfoCardsFilter() {
        addIdentifierCallbacks(
                new StringFilterGroup(
                        Settings.HIDE_INFO_CARDS,
                        "info_card_teaser_overlay.e"
                )
        );
    }
}
