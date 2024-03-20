package com.affixstudio.whatsapptool.activity.contactselector

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.affixstudio.whatsapptool.R
import com.affixstudio.whatsapptool.activity.BaseActivity
import com.affixstudio.whatsapptool.databinding.ActivityContactSelectorBinding
import com.affixstudio.whatsapptool.fragment.ContactSelectorFragment
import com.affixstudio.whatsapptool.model.utils.ContactsHelper
import com.affixstudio.whatsapptool.viewmodel.SwipeToKillAppDetectViewModel

class ContactSelectorActivity : BaseActivity() {
    private lateinit var contactSelectorFragment: ContactSelectorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityContactSelectorBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        contactSelectorFragment = supportFragmentManager.findFragmentById(R.id.contact_selector_layout)
                as ContactSelectorFragment

        title = getString(R.string.contact_selector)

        ViewModelProvider(this).get(SwipeToKillAppDetectViewModel::class.java)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ContactsHelper.CONTACT_PERMISSION_REQUEST_CODE && this::contactSelectorFragment.isInitialized) {
            contactSelectorFragment.loadContactList()
        }
    }
}