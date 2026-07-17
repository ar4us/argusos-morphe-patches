/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

@file:Suppress("SpellCheckingInspection")

package app.argusos.patches.youtube.misc.litho.context

import app.argusos.patches.shared.misc.litho.context.createConversionContextPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch

val conversionContextPatch = createConversionContextPatch(sharedExtensionPatch)
