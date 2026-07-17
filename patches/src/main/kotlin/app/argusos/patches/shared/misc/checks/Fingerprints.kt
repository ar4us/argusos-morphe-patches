package app.argusos.patches.shared.misc.checks

import app.morphe.patcher.Fingerprint

internal object PatchInfoFingerprint : Fingerprint(
    definingClass = "Lapp/argusos/extension/shared/checks/PatchInfo;"
)

internal object PatchInfoBuildFingerprint : Fingerprint(
    definingClass = $$"Lapp/argusos/extension/shared/checks/PatchInfo$Build;"
)
