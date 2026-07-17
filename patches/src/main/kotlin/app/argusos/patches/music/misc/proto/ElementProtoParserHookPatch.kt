/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.argusos.patches.music.misc.proto

import app.argusos.patches.music.misc.extension.sharedExtensionPatch
import app.argusos.patches.shared.misc.proto.createElementProtoParserHookPatch

val elementProtoParserHookPatch = createElementProtoParserHookPatch(sharedExtensionPatch)
