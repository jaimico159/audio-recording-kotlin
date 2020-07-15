package com.example.lab11

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!AudioRecordPermissionManager.hasRecordingPermission(this)) {
            AudioRecordPermissionManager.requestRecordingPermission(this)
            return
        }

        if (!WritePermissionManager.hasWritePermission(this)) {
            WritePermissionManager.requestWritePermission(this)
            return
        }

        
    }

    /** Helper to ask recording permission.  */
    object AudioRecordPermissionManager {
        private const val AUDIO_RECORD_PERMISSION_CODE = 0
        private const val AUDIO_RECORD_PERMISSION = Manifest.permission.RECORD_AUDIO

        /** Check to see we have the necessary permissions for this app.  */
        fun hasRecordingPermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(activity, AUDIO_RECORD_PERMISSION) == PackageManager.PERMISSION_GRANTED
        }

        /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
        fun requestRecordingPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(AUDIO_RECORD_PERMISSION), AUDIO_RECORD_PERMISSION_CODE)
        }

        /** Check to see if we need to show the rationale for this permission.  */
        fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, AUDIO_RECORD_PERMISSION)
        }

        /** Launch Application Setting to grant permission.  */
        fun launchPermissionSettings(activity: Activity) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(intent)
        }
    }

    object WritePermissionManager {
        private const val WRITE_PERMISSION_CODE = 1
        private const val WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

        /** Check to see we have the necessary permissions for this app.  */
        fun hasWritePermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(activity, WRITE_PERMISSION) == PackageManager.PERMISSION_GRANTED
        }

        /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
        fun requestWritePermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(WRITE_PERMISSION), WRITE_PERMISSION_CODE)
        }

        /** Check to see if we need to show the rationale for this permission.  */
        fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_PERMISSION)
        }

        /** Launch Application Setting to grant permission.  */
        fun launchPermissionSettings(activity: Activity) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!AudioRecordPermissionManager.hasRecordingPermission(this)) {
            Toast.makeText(this, "Se necesita permiso para usar la grabadora de audio", Toast.LENGTH_LONG)
                .show()
            if (!AudioRecordPermissionManager.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                AudioRecordPermissionManager.launchPermissionSettings(this)
            }
            finish()
        }

        if (!WritePermissionManager.hasWritePermission(this)) {
            Toast.makeText(this, "Se necesita permiso para usar la grabadora de audio", Toast.LENGTH_LONG)
                .show()
            if (!WritePermissionManager.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                WritePermissionManager.launchPermissionSettings(this)
            }
            finish()
        }

        recreate()
    }
}
