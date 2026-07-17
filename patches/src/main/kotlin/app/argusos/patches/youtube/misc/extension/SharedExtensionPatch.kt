package app.argusos.patches.youtube.misc.extension

import app.morphe.patches.all.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.extension.hooks.applicationInitHook
import app.argusos.patches.youtube.misc.extension.hooks.applicationInitOnCrateHook

val sharedExtensionPatch = sharedExtensionPatch(
    listOf("youtube", "shared-youtube"),
    applicationInitHook,
    applicationInitOnCrateHook
)
