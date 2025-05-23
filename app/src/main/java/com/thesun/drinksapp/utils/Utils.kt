package com.thesun.drinksapp.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import com.thesun.drinksapp.BuildConfig
import java.text.Normalizer
import java.util.regex.Pattern

object Utils {
    const val PUBLIC_KEY = BuildConfig.STRIPE_PUBLIC_KEY
    const val SECRET_KEY = BuildConfig.STRIPE_SECRET_KEY
    @JvmStatic
    fun hideSoftKeyboard(activity: Activity) {
        try {
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    @JvmStatic
    fun getTextSearch(input: String?): String {
        val nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("")
    }
}