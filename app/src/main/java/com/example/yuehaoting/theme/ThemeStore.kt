package com.example.yuehaoting.theme

import android.content.res.Configuration
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import com.example.yuehaoting.App
import com.example.yuehaoting.R


object ThemeStore {
  private const val KEY_NAME = "aplayer-theme"
  private const val LIGHT = "light"
  private const val DARK = "dark"
  private const val BLACK = "black"
  const val ALWAYS_OFF = "always_off"
  const val ALWAYS_ON = "always_on"
  const val FOLLOW_SYSTEM = "follow_system"
  private const val KEY_PRIMARY_COLOR = "primary_color"
  private const val KEY_ACCENT_COLOR = "accent_color"
  private const val KEY_FLOAT_LYRIC_TEXT_COLOR = "float_lyric_text_color"
  const val STATUS_BAR_ALPHA = 150

  @JvmField
  var sColoredNavigation: Boolean = false

  @JvmField
  var sImmersiveMode: Boolean = false

  val theme: String
    get() {
      val darkTheme = SPUtil.getValue(
        App.context,
        SPUtil.SETTING_KEY.NAME,
        SPUtil.SETTING_KEY.DARK_THEME,
        FOLLOW_SYSTEM
      )
      return if (darkTheme == ALWAYS_ON || (darkTheme == FOLLOW_SYSTEM && (App.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)) {
        if (SPUtil.getValue(
            App.context,
            SPUtil.SETTING_KEY.NAME,
            SPUtil.SETTING_KEY.BLACK_THEME,
            false
          )
        ) {
          BLACK
        } else {
          DARK
        }
      } else {
        LIGHT
      }
    }




  @JvmStatic
  @get:ColorInt
  var materialPrimaryColor: Int
    get() = SPUtil.getValue(
      App.context, KEY_NAME, KEY_PRIMARY_COLOR, Color.parseColor("#698cf6")
    )
    set(@ColorInt value) {
      SPUtil.putValue(App.context, KEY_NAME, KEY_PRIMARY_COLOR, value)
    }

  @get:ColorInt
  val materialPrimaryDarkColor: Int
    get() = ColorUtil.darkenColor(materialPrimaryColor)

  @JvmStatic
  @get:ColorInt
  var accentColor: Int

    get() {
      var accentColor = SPUtil.getValue(
        // 默认颜色
        App.context, KEY_NAME, KEY_ACCENT_COLOR, Color.parseColor("#FFFFFF")
      )
      if (ColorUtil.isColorCloseToWhite(accentColor)) {
        accentColor = ColorUtil.getColor(R.color.accent_gray_color)
      }
      return accentColor
    }
    set(value) {
      SPUtil.putValue(App.context, KEY_NAME, KEY_ACCENT_COLOR, value)
    }

  @JvmStatic
  @get:ColorInt
  val navigationBarColor: Int
    get() = materialPrimaryColor

  @JvmStatic
  @get:ColorInt
  val statusBarColor: Int
    get() = if (sImmersiveMode) {
      materialPrimaryColor
    } else {
      materialPrimaryDarkColor
    }



  @JvmStatic
  val isMDColorLight: Boolean
    get() = ColorUtil.isColorLight(materialPrimaryColor)


  @JvmStatic
  @get:StyleRes
  val themeRes: Int
    get() = when (theme) {
      LIGHT -> R.style.PlayTheme
      BLACK -> R.style.Theme_yuehaoting_Black
      DARK -> R.style.Theme_yuehaoting_Base_Diablo
      else -> R.style.PlayTheme
    }

  @JvmStatic
  val isLightTheme: Boolean
    get() = themeRes == R.style.PlayTheme

  @get:ColorInt
  val playerBtnColor: Int
    get() = Color.parseColor(
      if (isLightTheme) {
        "#DEDEDE"
      } else {
        "#6b6b6b"
      }
    )


}