/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */
package app.argusos.patches.reddit.layout.ask

import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.reddit.misc.flag.featureFlagHookPatch
import app.argusos.patches.reddit.misc.flag.hookFeatureFlag
import app.argusos.patches.reddit.misc.settings.settingsPatch
import app.argusos.patches.reddit.misc.version.versionCheckPatch
import app.argusos.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import app.morphe.util.setExtensionIsPatchIncluded

private const val EXTENSION_CLASS =
    "Lapp/argusos/extension/reddit/patches/HideAskButtonPatch;"

@Suppress("unused")
val hideAskButtonPatch = bytecodePatch(
    name = "Hide Ask button",
    description = "Adds an option to hide Ask button in the search bar."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(
        settingsPatch,
        featureFlagHookPatch,
        versionCheckPatch
    )

    execute {

        hookFeatureFlag("$EXTENSION_CLASS->hideAskButton")

        AskButtonComposableFingerprint.method.addInstructionsWithLabels(
            0,
            """
                invoke-static { }, $EXTENSION_CLASS->shouldHideAskButton()Z
                move-result v0
                if-eqz v0, :ignore
                return-void
                :ignore
                nop
            """
        )

        setExtensionIsPatchIncluded(EXTENSION_CLASS)
    }
}
