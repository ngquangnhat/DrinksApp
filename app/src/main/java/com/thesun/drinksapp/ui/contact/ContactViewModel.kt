package com.thesun.drinksapp.ui.contact

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.thesun.drinksapp.R
import com.thesun.drinksapp.constant.AboutUsConfig
import com.thesun.drinksapp.data.model.Contact
import com.thesun.drinksapp.utils.GlobalFunction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor() : ViewModel() {

    private val _aboutUsState = MutableStateFlow(
        AboutUsState(
            title = AboutUsConfig.ABOUT_US_TITLE,
            content = AboutUsConfig.ABOUT_US_CONTENT,
            websiteTitle = AboutUsConfig.ABOUT_US_WEBSITE_TITLE
        )
    )
    val aboutUsState: StateFlow<AboutUsState> = _aboutUsState.asStateFlow()

    private val _contacts = MutableStateFlow(getListContact())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private var pendingCallPhone = false

    fun openWebsite(activity: Activity) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.WEBSITE))
        activity.startActivity(intent)
    }

    fun requestCallPhone(activity: Activity, permissionLauncher: ActivityResultLauncher<String>) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.CALL_PHONE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            callPhoneNumber(activity)
        } else {
            pendingCallPhone = true
            permissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
        }
    }

    fun callPhoneNumber(activity: Activity) {
        GlobalFunction.callPhoneNumber(activity)
        pendingCallPhone = false
    }

    fun handleContactClick(contact: Contact, activity: Activity) {
        when (contact.id) {
            Contact.FACEBOOK -> GlobalFunction.onClickOpenFacebook(activity)
            Contact.GMAIL -> GlobalFunction.onClickOpenGmail(activity)
            Contact.SKYPE -> GlobalFunction.onClickOpenSkype(activity)
            Contact.YOUTUBE -> GlobalFunction.onClickOpenYoutubeChannel(activity)
            Contact.ZALO -> GlobalFunction.onClickOpenZalo(activity)
        }
    }

    private fun getListContact(): List<Contact> {
        return listOf(
            Contact(Contact.FACEBOOK, R.drawable.ic_facebook),
            Contact(Contact.HOTLINE, R.drawable.ic_hotline),
            Contact(Contact.GMAIL, R.drawable.ic_gmail),
            Contact(Contact.SKYPE, R.drawable.ic_skype),
            Contact(Contact.YOUTUBE, R.drawable.ic_youtube),
            Contact(Contact.ZALO, R.drawable.ic_zalo)
        )
    }
}

data class AboutUsState(
    val title: String,
    val content: String,
    val websiteTitle: String
)