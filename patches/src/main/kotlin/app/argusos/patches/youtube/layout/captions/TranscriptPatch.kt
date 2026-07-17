package app.argusos.patches.youtube.layout.captions

import app.morphe.patcher.patch.bytecodePatch
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.youtube.misc.contexthook.Endpoint
import app.argusos.patches.youtube.misc.contexthook.addClientVersionHook
import app.argusos.patches.youtube.misc.contexthook.clientContextHookPatch
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playservice.versionCheckPatch
import app.argusos.patches.youtube.misc.settings.settingsPatch

private const val EXTENSION_CLASS =
    "Lapp/argusos/extension/youtube/patches/TranscriptPatch;"

internal val transcriptPatch = bytecodePatch(
    description = "Add an option to fix an issue where transcript is unavailable due to a precondition check failure.",
) {
    dependsOn(
        sharedExtensionPatch,
        settingsPatch,
        versionCheckPatch,
        clientContextHookPatch,
    )

    execute {
        settingsMenuCaptionGroup.add(
            SwitchPreference("argusos_fix_transcript", summary = true)
        )

        addClientVersionHook(
            Endpoint.TRANSCRIPT,
            "$EXTENSION_CLASS->getTranscriptAppVersionOverride(Ljava/lang/String;)Ljava/lang/String;",
        )
    }
}
