package app.argusos.patches.youtube.interaction.swipecontrols

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.argusos.patches.shared.misc.settings.preference.InputType
import app.argusos.patches.shared.misc.settings.preference.ListPreference
import app.argusos.patches.shared.misc.settings.preference.NonInteractivePreference
import app.argusos.patches.shared.misc.settings.preference.SwitchPreference
import app.argusos.patches.shared.misc.settings.preference.TextPreference
import app.argusos.patches.youtube.misc.extension.sharedExtensionPatch
import app.argusos.patches.youtube.misc.playertype.playerTypeHookPatch
import app.argusos.patches.youtube.misc.playservice.is_20_34_or_greater
import app.argusos.patches.youtube.misc.playservice.versionCheckPatch
import app.argusos.patches.youtube.misc.settings.PreferenceScreen
import app.argusos.patches.youtube.misc.settings.settingsPatch
import app.argusos.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE
import app.argusos.patches.youtube.shared.YouTubeMainActivityConstructorFingerprint
import app.morphe.util.ResourceGroup
import app.morphe.util.copyResources
import app.morphe.util.insertLiteralOverride
import app.morphe.util.transformMethods
import app.morphe.util.traverseClassHierarchy
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod

internal const val EXTENSION_CLASS = "Lapp/argusos/extension/youtube/swipecontrols/SwipeControlsHostActivity;"

private val swipeControlsResourcePatch = resourcePatch {
    dependsOn(
        settingsPatch,
        versionCheckPatch
    )

    execute {
        // If fullscreen swipe is enabled in newer versions the app can crash.
        // It likely is caused by conflicting experimental flags that are never enabled together.
        // Flag was completely removed in 20.34+
        if (!is_20_34_or_greater) {
            PreferenceScreen.SWIPE_CONTROLS.addPreferences(
                SwitchPreference("argusos_swipe_change_video", summary = true)
            )
        }

        PreferenceScreen.SWIPE_CONTROLS.addPreferences(
            SwitchPreference("argusos_swipe_brightness", summary = true),
            SwitchPreference("argusos_swipe_volume", summary = true),
            SwitchPreference("argusos_swipe_speed", summary = true),
            NonInteractivePreference(
                key = "argusos_swipe_zone_width",
                tag = "app.argusos.extension.shared.settings.preference.SeekBarPreference"
            ),
            NonInteractivePreference(
                key = "argusos_swipe_speed_zone_height",
                tag = "app.argusos.extension.shared.settings.preference.SeekBarPreference"
            ),
            NonInteractivePreference(
                key = "argusos_swipe_zone_preview",
                summaryKey = null,
                tag = "app.argusos.extension.youtube.settings.preference.SwipeZonePreference"
            ),
            NonInteractivePreference(
                key = "argusos_swipe_brightness_sensitivity",
                tag = "app.argusos.extension.shared.settings.preference.SeekBarPreference"
            ),
            NonInteractivePreference(
                key = "argusos_swipe_volume_sensitivity",
                tag = "app.argusos.extension.shared.settings.preference.SeekBarPreference"
            ),
            NonInteractivePreference(
                key = "argusos_swipe_speed_sensitivity",
                tag = "app.argusos.extension.shared.settings.preference.SeekBarPreference"
            ),
            ListPreference("argusos_swipe_speed_step"),
            SwitchPreference("argusos_swipe_press_to_engage", summary = true),
            SwitchPreference("argusos_swipe_haptic_feedback"),
            SwitchPreference("argusos_swipe_save_and_restore_brightness", summary = true),
            SwitchPreference("argusos_swipe_lowest_value_enable_auto_brightness", summary = true),
            ListPreference("argusos_swipe_overlay_style"),
            NonInteractivePreference(
                key = "argusos_swipe_overlay_background_opacity",
                tag = "app.argusos.extension.shared.settings.preference.SeekBarPreference"
            ),
            TextPreference("argusos_swipe_overlay_progress_brightness_color",
                tag = "app.argusos.extension.shared.settings.preference.ColorPickerWithOpacitySliderPreference",
                inputType = InputType.TEXT_CAP_CHARACTERS
            ),
            TextPreference("argusos_swipe_overlay_progress_volume_color",
                tag = "app.argusos.extension.shared.settings.preference.ColorPickerWithOpacitySliderPreference",
                inputType = InputType.TEXT_CAP_CHARACTERS
            ),
            TextPreference("argusos_swipe_overlay_progress_speed_color",
                tag = "app.argusos.extension.shared.settings.preference.ColorPickerWithOpacitySliderPreference",
                inputType = InputType.TEXT_CAP_CHARACTERS
            ),
            NonInteractivePreference(
                key = "argusos_swipe_text_overlay_size",
                tag = "app.argusos.extension.shared.settings.preference.SeekBarPreference"
            ),
            TextPreference("argusos_swipe_overlay_timeout", inputType = InputType.NUMBER),
            TextPreference("argusos_swipe_threshold", inputType = InputType.NUMBER)
        )

        copyResources(
            "swipecontrols",
            ResourceGroup(
                "drawable",
                "argusos_ic_sc_brightness_auto.xml",
                "argusos_ic_sc_brightness_full.xml",
                "argusos_ic_sc_brightness_high.xml",
                "argusos_ic_sc_brightness_low.xml",
                "argusos_ic_sc_brightness_medium.xml",
                "argusos_ic_sc_volume_high.xml",
                "argusos_ic_sc_volume_low.xml",
                "argusos_ic_sc_volume_mute.xml",
                "argusos_ic_sc_volume_normal.xml",
                "argusos_ic_sc_speed.xml"
            )
        )
    }
}

@Suppress("unused")
val swipeControlsPatch = bytecodePatch(
    name = "Swipe controls",
    description = "Adds options to enable and configure volume and brightness swipe controls."
) {
    dependsOn(
        sharedExtensionPatch,
        playerTypeHookPatch,
        swipeControlsResourcePatch
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)

    execute {
        val wrapperClass = SwipeControlsHostActivityFingerprint.classDef
        val targetClass = YouTubeMainActivityConstructorFingerprint.classDef

        // Inject the wrapper class from the extension into the class hierarchy of MainActivity.
        wrapperClass.setSuperClass(targetClass.superclass)
        targetClass.setSuperClass(wrapperClass.type)

        // Ensure all classes and methods in the hierarchy are non-final, so we can override them in the extension.
        traverseClassHierarchy(targetClass) {
            accessFlags = accessFlags and AccessFlags.FINAL.value.inv()
            transformMethods {
                ImmutableMethod(
                    definingClass,
                    name,
                    parameters,
                    returnType,
                    accessFlags and AccessFlags.FINAL.value.inv(),
                    annotations,
                    hiddenApiRestrictions,
                    implementation,
                ).toMutable()
            }
        }

        if (!is_20_34_or_greater) {
            SwipeChangeVideoFingerprint.let {
                it.method.insertLiteralOverride(
                    it.instructionMatches.last().index,
                    "$EXTENSION_CLASS->allowSwipeChangeVideo(Z)Z"
                )
            }
        }
    }
}
