/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.argusos.patches.youtube.layout.hide.shelves

import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.shared.misc.litho.filter.addLithoFilter
import app.argusos.patches.youtube.misc.engagement.engagementPanelHookPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.litho.filter.lithoFilterPatch
import app.argusos.patches.youtube.misc.litho.observer.layoutReloadObserverPatch
import app.argusos.patches.youtube.misc.navigation.navigationBarHookPatch
import app.argusos.patches.youtube.misc.playertype.playerTypeHookPatch

private const val EXTENSION_FILTER =
    "Lapp/argusos/extension/youtube/patches/components/HorizontalShelvesFilter;"

internal val hideHorizontalShelvesPatch = bytecodePatch {
    dependsOn(
        sharedExtensionPatch,
        lithoFilterPatch,
        playerTypeHookPatch,
        navigationBarHookPatch,
        engagementPanelHookPatch,
        layoutReloadObserverPatch
    )

    execute {
        addLithoFilter(EXTENSION_FILTER)
    }
}
