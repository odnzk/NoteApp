package com.example.noteapp.ui.util.ext


import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import com.example.noteapp.R
import com.google.android.material.chip.Chip


private const val CATEGORY_NOT_SELECTED_COLOR_ALPHA = 30

fun Chip.setCategoryStyle(color: String, title: String): Chip =
    this.apply {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        text = title
        val selectedCategoryColor = Color.parseColor(color)
        ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checkable)
            ),
            intArrayOf(
                selectedCategoryColor,
                ColorUtils.setAlphaComponent(selectedCategoryColor, CATEGORY_NOT_SELECTED_COLOR_ALPHA)
            )
        ).also { chipBackgroundColor = it }
        ResourcesCompat.getFont(context, R.font.inter_regular).also { typeface = it }
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
//        <item name="android:textSize">12sp</item>
//        <item name="android:fontFamily">@font/inter_light</item>
//        <item name="android:textColor">@color/white</item>
    }
