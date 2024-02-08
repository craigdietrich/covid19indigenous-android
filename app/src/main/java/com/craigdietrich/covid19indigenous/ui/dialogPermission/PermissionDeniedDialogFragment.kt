package com.craigdietrich.covid19indigenous.ui.dialogPermission

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment

class PermissionDeniedDialogFragment(val positiveButtonClick: () -> Unit, val negativeButtonClick: () -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Location permission is required. Please grant it in app settings.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                positiveButtonClick.invoke()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                negativeButtonClick.invoke()
            }
            .create()
    }
}