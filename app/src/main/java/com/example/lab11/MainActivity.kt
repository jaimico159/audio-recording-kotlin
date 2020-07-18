package com.example.lab11

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false

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


        initMediaRecorder()

        button_start_recording.setOnClickListener {
            //aqui se comiuenza a grabar supuestamente
            startRecording()
            button_start_recording.visibility = View.GONE
            button_stop_recording.visibility = View.VISIBLE
            button_pause_recording.visibility = View.VISIBLE
            recView.visibility = View.VISIBLE

        }

        button_stop_recording.setOnClickListener{
            //se tiene que parar
            stopRecording()
            button_start_recording.visibility = View.VISIBLE
            button_stop_recording.visibility = View.GONE
            button_pause_recording.visibility = View.GONE
            recView.visibility = View.INVISIBLE

        }

        button_pause_recording.setOnClickListener {
            //se tien que pausar y mejor si cambiamos el texto del boton
            pauseRecording()
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
    private fun initMediaRecorder(){
        output = Environment.getExternalStorageDirectory().absolutePath + "/grabacion.mp3"
        mediaRecorder = MediaRecorder()

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)
    }

    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            // aún tendriamos que poner algo como una imagen que indique que esta grabando :(
            recView.visibility = View.VISIBLE
            Toast.makeText(this, "La grabación empezó", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
            Toast.makeText(this, "Ya dejaste de grabar", Toast.LENGTH_SHORT).show()
            initMediaRecorder()
        }else{
            //poner algo para saber que no se está grabando
            Toast.makeText(this, "Ya dejaste de grabar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pauseRecording() {
        if(state) {
            if(!recordingStopped){
                Toast.makeText(this,"Parado", Toast.LENGTH_SHORT).show()
                mediaRecorder?.pause()
                recordingStopped = true
                button_pause_recording.text = "RESUME"
            }else{
                //aqui se cambiaria el boton para que diaga pause/resume o algo así...
                resumeRecording()
            }
        }
    }

    private fun resumeRecording() {
        Toast.makeText(this,"Volviendo a grabar!", Toast.LENGTH_SHORT).show()
        mediaRecorder?.resume()
        button_pause_recording.text = "PAUSE"
        recordingStopped = false
        state = true
    }
}
