/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.argusos.patches.youtube.layout.sharesheet

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.shared.misc.litho.filter.addLithoFilter
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.litho.filter.lithoFilterPatch
import app.argusos.patches.youtube.misc.recyclerviewtree.addRecyclerViewTreeHook
import app.argusos.patches.youtube.misc.recyclerviewtree.recyclerViewTreeHookPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE

private const val EXTENSION_CLASS =
    "Lapp/argusos/extension/youtube/patches/OpenSystemShareSheetPatch;"
private const val EXTENSION_FILTER =
    "Lapp/argusos/extension/youtube/patches/components/SystemShareSheetFilter;"


@Suppress("unused")
internal fun openSystemShareSheetPatch(
) = bytecodePatch(
    name = "Open system share sheet",
    description = "Adds an option to always open the system share sheet instead of the in-app share sheet."
) {

    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        lithoFilterPatch,
        recyclerViewTreeHookPatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        PreferenceScreen.GENERAL.addPreferences(
            SwitchPreference("argusos_open_system_share_sheet", summary = true)
        )

        ShareSheetPanelContentInitializationFingerprint.method.addInstruction(
            0,
            "invoke-static { }, $EXTENSION_CLASS->openSystemShareSheet()V"
        )

        addRecyclerViewTreeHook(EXTENSION_CLASS)

        addLithoFilter(EXTENSION_FILTER)
    }
}
