package app.argusos.patches.all.misc.updates

import app.morphe.patcher.Match
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch

// TODO: Delete this after Manager 1.17.0 has been released
internal val checkPatcherUpToDatePatch = bytecodePatch {
    execute {
        try {
            //noinspection CheckResult
            Match.InstructionMatch::class.java.getDeclaredMethod(
                "getMethodCalled",
                BytecodePatchContext::class.java
            )
        } catch (ex: NoSuchMethodException) {
            throw RuntimeException(
                "\n\n#####################################\n\n" +
                        "Your ArgusOS app is outdated." +
                        "\n\nPlease update ArgusOS by downloading from your manager\n\n" +
                        "#####################################\n\n"
                        + ex
            )
        }
    }
}
