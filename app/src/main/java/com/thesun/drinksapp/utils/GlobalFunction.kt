package com.thesun.drinksapp.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import com.thesun.drinksapp.constant.AboutUsConfig
import com.thesun.drinksapp.prefs.DataStoreManager

object GlobalFunction {

    @JvmStatic
    fun encodeEmailUser(): Int {
        var hashCode = DataStoreManager.user?.email.hashCode()
        if (hashCode < 0) {
            hashCode *= -1
        }
        return hashCode
    }

    @JvmStatic
    fun onClickOpenGmail(activity: Activity) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", AboutUsConfig.GMAIL, null
            )
        )
        activity.startActivity(Intent.createChooser(emailIntent, "Send Email"))
    }

    @JvmStatic
    fun onClickOpenSkype(activity: Activity) {
        try {
            val skypeUri = Uri.parse("skype:${AboutUsConfig.SKYPE_ID}?chat")
            activity.packageManager.getPackageInfo("com.skype.raider", 0)
            val skypeIntent = Intent(Intent.ACTION_VIEW, skypeUri)
            skypeIntent.setComponent(ComponentName("com.skype.raider", "com.skype.raider.Main"))
            activity.startActivity(skypeIntent)
        } catch (e: Exception) {
            openSkypeWebView(activity)
        }
    }

    private fun openSkypeWebView(activity: Activity) {
        try {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("skype:${AboutUsConfig.SKYPE_ID}?chat")
                )
            )
        } catch (exception: Exception) {
            val skypePackageName = "com.skype.raider"
            try {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$skypePackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$skypePackageName")
                    )
                )
            }
        }
    }

    @JvmStatic
    fun onClickOpenFacebook(activity: Activity) {
        var intent: Intent
        try {
            var urlFacebook = AboutUsConfig.PAGE_FACEBOOK
            val packageManager = activity.packageManager
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) {
                urlFacebook = "fb://facewebmodal/f?href=${AboutUsConfig.LINK_FACEBOOK}"
            }
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlFacebook))
        } catch (e: Exception) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_FACEBOOK))
        }
        activity.startActivity(intent)
    }

    @JvmStatic
    fun onClickOpenYoutubeChannel(activity: Activity) {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_YOUTUBE)))
    }

    @JvmStatic
    fun onClickOpenZalo(activity: Activity) {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.ZALO_LINK)))
    }

    @JvmStatic
    fun callPhoneNumber(activity: Activity) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CALL_PHONE
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    101
                )
                return
            }
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:${AboutUsConfig.PHONE_NUMBER}")
            activity.startActivity(callIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
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
}