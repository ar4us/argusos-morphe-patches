package app.argusos.patches.youtube.layout.sponsorblock

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.argusos.patches.all.misc.resources.addAppResources
import app.argusos.patches.all.misc.resources.addResourcesPatch
import app.morphe.patches.all.misc.resources.resourceMappingPatch
import app.argusos.patches.shared.misc.settings.preference.BasePreference
import app.argusos.patches.shared.misc.settings.preference.InputType
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.settings.preference.NonInteractivePreference
import app.argusos.patches.shared.misc.settings.preference.PreferenceCategory
import app.argusos.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.TextPreference
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playercontrols.addTopControl
import app.argusos.patches.youtube.misc.playercontrols.initializeTopControl
import app.argusos.patches.youtube.misc.playercontrols.injectVisibilityCheckCall
import app.argusos.patches.youtube.misc.playercontrols.legacyPlayerControlsPatch
import app.argusos.patches.youtube.misc.playertype.playerTypeHookPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.shared.SeekbarOnDrawFingerprint
import app.argusos.patches.youtube.video.information.onCreateHook
import app.argusos.patches.youtube.video.information.videoInformationPatch
import app.argusos.patches.youtube.video.information.videoTimeHook
import app.argusos.patches.youtube.video.videoid.hookBackgroundPlayVideoId
import app.argusos.patches.youtube.video.videoid.videoIdPatch
import app.morphe.util.ResourceGroup
import app.morphe.util.addInstructionsAtControlFlowLabel
import app.morphe.util.copyResources
import app.morphe.util.getReference
import app.morphe.util.indexOfFirstInstructionReversedOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val SB_PREFERENCES_PACKAGE = "app.argusos.extension.youtube.sponsorblock.preferences"
private const val SEGMENT_CATEGORY_PREFERENCE_TAG =
    "app.argusos.extension.shared.sponsorblock.objects.SegmentCategoryPreference"

public fun categoryPreference(settingKey: String): BasePreference =
    object : BasePreference(settingKey, null, null, null, null, null, SEGMENT_CATEGORY_PREFERENCE_TAG) {}

private val sponsorBlockResourcePatch = resourcePatch {
    dependsOn(
        settingsPatch,
        resourceMappingPatch,
        legacyPlayerControlsPatch,
        addResourcesPatch
    )

    execute {
        addAppResources("sponsorblock")

        PreferenceScreen.SPONSORBLOCK.addPreferences(
            SwitchPreference("argusos_sb_enabled", summary = true),
            PreferenceCategory(
                key = "argusos_sb_appearance_category",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("argusos_sb_voting_button", summary = true),
                    SwitchPreference("argusos_sb_compact_skip_button", summary = true),
                    SwitchPreference("argusos_sb_auto_hide_skip_button", summary = true),
                    ListPreference(key = "argusos_sb_auto_hide_skip_button_duration"),
                    SwitchPreference("argusos_sb_toast_on_skip", summary = true),
                    ListPreference(key = "argusos_sb_toast_on_skip_duration"),
                    SwitchPreference("argusos_sb_video_length_without_segments", summary = true),
                    SwitchPreference("argusos_sb_square_layout", summary = true)
                )
            ),
            PreferenceCategory(
                key = "argusos_sb_diff_segments",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    categoryPreference("argusos_sb_sponsor_color"),
                    categoryPreference("argusos_sb_selfpromo_color"),
                    categoryPreference("argusos_sb_interaction_color"),
                    categoryPreference("argusos_sb_highlight_color"),
                    categoryPreference("argusos_sb_intro_color"),
                    categoryPreference("argusos_sb_outro_color"),
                    categoryPreference("argusos_sb_preview_color"),
                    categoryPreference("argusos_sb_hook_color"),
                    categoryPreference("argusos_sb_filler_color"),
                    categoryPreference("argusos_sb_music_offtopic_color")
                )
            ),
            PreferenceCategory(
                key = "argusos_sb_create_segment_category",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference(
                        key = "argusos_sb_create_new_segment",
                        summary = true,
                        tag = "$SB_PREFERENCES_PACKAGE.SponsorBlockCreateSegmentSwitchPreference"
                    ),
                    TextPreference(
                        key = "argusos_sb_create_new_segment_step",
                        tag = "$SB_PREFERENCES_PACKAGE.SponsorBlockSegmentStepPreference",
                        inputType = InputType.NUMBER
                    ),
                    NonInteractivePreference(
                        key = "argusos_sb_guidelines",
                        tag = "$SB_PREFERENCES_PACKAGE.SponsorBlockGuidelinesPreference",
                        selectable = true
                    )
                )
            ),
            PreferenceCategory(
                key = "argusos_sb_general",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("argusos_sb_toast_on_connection_error", summary = true),
                    SwitchPreference("argusos_sb_track_skip_count", summary = true),
                    TextPreference(
                        key = "argusos_sb_min_segment_duration",
                        inputType = InputType.NUMBER_DECIMAL
                    ),
                    TextPreference(
                        key = "argusos_sb_private_user_id_Do_Not_Share",
                        tag = "$SB_PREFERENCES_PACKAGE.SponsorBlockPrivateUserIdPreference"
                    ),
                    NonInteractivePreference(
                        key = "argusos_sb_api_url",
                        tag = "$SB_PREFERENCES_PACKAGE.SponsorBlockApiUrlPreference",
                        selectable = true
                    ),
                    NonInteractivePreference(
                        key = "argusos_sb_channel_whitelist",
                        tag = "$SB_PREFERENCES_PACKAGE.SponsorBlockChannelWhitelistPreference",
                        selectable = true
                    ),
                    SwitchPreference("argusos_sb_toast_on_whitelisted_channel", summary = true),
                    TextPreference(
                        key = null,
                        titleKey = "argusos_sb_settings_ie_title",
                        summaryKey = "argusos_sb_settings_ie_summary",
                        tag = "$SB_PREFERENCES_PACKAGE.SponsorBlockImportExportPreference"
                    )
                )
            ),
            PreferenceCategory(
                key = "argusos_sb_stats",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = emptySet(), // Preferences are added by custom class at runtime.
                tag = "app.argusos.extension.youtube.sponsorblock.ui.SponsorBlockStatsPreferenceCategory"
            ),
            PreferenceCategory(
                key = "argusos_sb_about",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = setOf(
                    NonInteractivePreference(
                        key = "argusos_sb_about_api",
                        tag = "app.argusos.extension.shared.sponsorblock.ui.SponsorBlockAboutPreference",
                        selectable = true
                    )
                )
            )
        )

        arrayOf(
            ResourceGroup(
                "layout",
                "argusos_sb_inline_sponsor_overlay.xml",
                "argusos_sb_new_segment.xml",
                "argusos_sb_skip_sponsor_button.xml"
            ),
            ResourceGroup(
                "drawable",
                "argusos_sb_adjust.xml",
                "argusos_sb_adjust_bold.xml",
                "argusos_sb_backward.xml",
                "argusos_sb_backward_bold.xml",
                "argusos_sb_compare.xml",
                "argusos_sb_compare_bold.xml",
                "argusos_sb_edit.xml",
                "argusos_sb_edit_bold.xml",
                "argusos_sb_forward.xml",
                "argusos_sb_forward_bold.xml",
                "argusos_sb_logo.xml",
                "argusos_sb_logo_bold.xml",
                "argusos_sb_publish.xml",
                "argusos_sb_publish_bold.xml",
                "argusos_sb_voting.xml",
                "argusos_sb_voting_bold.xml"
            )
        ).forEach { resourceGroup ->
            copyResources("sponsorblock", resourceGroup)
        }

        addTopControl(
            "sponsorblock",
            "@+id/argusos_sb_voting_button",
            "@+id/argusos_sb_create_segment_button"
        )
    }
}

internal const val EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS =
    "Lapp/argusos/extension/youtube/sponsorblock/YouTubeSponsorBlockConfig;"
private const val EXTENSION_CREATE_SEGMENT_BUTTON_CONTROLLER_CLASS =
    "Lapp/argusos/extension/youtube/sponsorblock/ui/CreateSegmentButton;"
private const val EXTENSION_VOTING_BUTTON_CONTROLLER_CLASS =
    "Lapp/argusos/extension/youtube/sponsorblock/ui/VotingButton;"
private const val EXTENSION_SPONSORBLOCK_VIEW_CONTROLLER_CLASS =
    "Lapp/argusos/extension/youtube/sponsorblock/ui/SponsorBlockViewController;"

@Suppress("unused")
val sponsorBlockPatch = bytecodePatch(
    name = "SponsorBlock",
    description = "Adds options to enable and configure SponsorBlock, which can skip undesired video segments such as sponsored content."
) {
    dependsOn(
        sharedExtensionPatch,
        resourceMappingPatch,
        videoIdPatch,
        videoInformationPatch,
        playerTypeHookPatch,
        legacyPlayerControlsPatch,
        sponsorBlockResourcePatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        // Hook the video time methods.
        videoTimeHook(
            EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS,
            "setVideoTime"
        )

        hookBackgroundPlayVideoId(
            EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS +
                "->setCurrentVideoId(Ljava/lang/String;)V"
        )

        // Set seekbar draw rectangle.
        val rectangleFieldName: FieldReference
        RectangleFieldInvalidatorFingerprint.let {
            it.method.apply {
                val rectangleIndex = indexOfFirstInstructionReversedOrThrow(
                    it.instructionMatches.first().index
                ) {
                    getReference<FieldReference>()?.type == "Landroid/graphics/Rect;"
                }
                rectangleFieldName = getInstruction<ReferenceInstruction>(rectangleIndex).reference as FieldReference
            }
        }

        // Seekbar drawing.

        // Shared fingerprint and indexes may have changed.
        SeekbarOnDrawFingerprint.clearMatch()
        // Cannot match using original immutable class because
        // class may have been modified by other patches
        SeekbarOnDrawFingerprint.let {
            it.method.apply {
                // Set seekbar thickness.
                val thicknessIndex = it.instructionMatches.last().index
                val thicknessRegister = getInstruction<OneRegisterInstruction>(thicknessIndex).registerA
                addInstruction(
                    thicknessIndex + 1,
                    "invoke-static { v$thicknessRegister }, " +
                            "$EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS->setSeekbarThickness(I)V"
                )

                // Find the drawCircle call and draw the segment before it.
                val drawCircleIndex = indexOfFirstInstructionReversedOrThrow {
                    getReference<MethodReference>()?.name == "drawCircle"
                }
                val drawCircleInstruction = getInstruction<FiveRegisterInstruction>(drawCircleIndex)
                val canvasInstanceRegister = drawCircleInstruction.registerC
                val centerYRegister = drawCircleInstruction.registerE

                addInstruction(
                    drawCircleIndex,
                    "invoke-static { v$canvasInstanceRegister, v$centerYRegister }, " +
                            "$EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS->" +
                            "drawSegmentTimeBars(Landroid/graphics/Canvas;F)V"
                )

                // Set seekbar bounds.
                addInstructions(
                    0,
                    """
                        move-object/from16 v0, p0
                        iget-object v0, v0, $rectangleFieldName
                        invoke-static { v0 }, $EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS->setSeekbarRectangle(Landroid/graphics/Rect;)V
                    """
                )
            }
        }

        // Change visibility of the buttons.
        initializeTopControl(EXTENSION_CREATE_SEGMENT_BUTTON_CONTROLLER_CLASS)
        injectVisibilityCheckCall(EXTENSION_CREATE_SEGMENT_BUTTON_CONTROLLER_CLASS)

        initializeTopControl(EXTENSION_VOTING_BUTTON_CONTROLLER_CLASS)
        injectVisibilityCheckCall(EXTENSION_VOTING_BUTTON_CONTROLLER_CLASS)

        // Show skip button when player overlay is active.
        injectVisibilityCheckCall(EXTENSION_SPONSORBLOCK_VIEW_CONTROLLER_CLASS)

        // Append the new time to the player layout.
        AppendTimeFingerprint.let {
            it.method.apply {
                val index = it.instructionMatches.last().index
                val register = getInstruction<OneRegisterInstruction>(index).registerA

                addInstructions(
                    index + 1,
                    """
                        invoke-static { v$register }, $EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS->appendTimeWithoutSegments(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$register
                    """
                )
            }
        }

        // Initialize the player controller.
        onCreateHook(EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS, "initialize")

        // Initialize the SponsorBlock view.
        ControlsOverlayFingerprint.let {
            it.method.apply {
                val checkCastIndex = it.instructionMatches.last().index
                val frameLayoutRegister = getInstruction<OneRegisterInstruction>(checkCastIndex).registerA
                addInstruction(
                    checkCastIndex + 1,
                    "invoke-static {v$frameLayoutRegister}, $EXTENSION_SPONSORBLOCK_VIEW_CONTROLLER_CLASS->initialize(Landroid/view/ViewGroup;)V"
                )
            }
        }

        AdProgressTextViewVisibilityFingerprint.method.apply {
            val index = indexOfAdProgressTextViewVisibilityInstruction(this)
            val register = getInstruction<FiveRegisterInstruction>(index).registerD

            addInstructionsAtControlFlowLabel(
                index,
                "invoke-static { v$register }, $EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS->setAdProgressTextVisibility(I)V"
            )
        }
    }
}
