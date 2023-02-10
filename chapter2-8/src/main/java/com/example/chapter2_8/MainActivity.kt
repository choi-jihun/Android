package com.example.chapter2_8

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.chapter2_8.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.*
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.play.setOnClickListener { checkPermission() }
        binding.pause.setOnClickListener { mediaPlayerPause() }
        binding.stop.setOnClickListener { mediaPlayerStop() }
    }

    private fun mediaPlayerPlay(){
        val intent = Intent(this,MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_PLAY }
        startService(intent)
    }

    private fun mediaPlayerPause(){
        val intent = Intent(this,MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_PAUSE }
        startService(intent)
    }

    private fun mediaPlayerStop(){
        val intent = Intent(this,MediaPlayerService::class.java).apply {
            action = MEDIA_PLAYER_STOP }
        startService(intent)
    }

    private fun checkPermission(){
        when{
            ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->{
                showPermissionDialog()
            }
            else -> {
                requestNotificationPermission()
            }
        }
        mediaPlayerPlay()
    }

    private fun showPermissionDialog(){
        Builder(this).apply {
            setMessage("Notification 사용을 위한 권한 필요")
            setNegativeButton("취소",null)
            setPositiveButton("동의"){_,_->
                requestNotificationPermission()
            }
        }.show()
    }

    private fun requestNotificationPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),REQUEST_POST_NOTIFICATION)
    }

    companion object {
        const val REQUEST_POST_NOTIFICATION = 100
    }

    override fun onDestroy() {
        stopService(Intent(this,MediaPlayerService::class.java))
        super.onDestroy()
    }

}