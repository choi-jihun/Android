package com.example.chapter2_5

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.example.chapter2_5.databinding.ActivityMainBinding
import com.example.chapter2_5.databinding.DialogCountdownSettingBinding
import kotlinx.coroutines.NonCancellable.start
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var countDown = 5
    private var currentCountdown = countDown * 10
    private var currentSecond = 0
    private var timer : Timer ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countText.setOnClickListener {
            showCountdown()
        }

        binding.start.setOnClickListener {
            start()
            binding.start.isVisible = false
            binding.stop.isVisible = false
            binding.pause.isVisible = true
            binding.lap.isVisible = true

        }

        binding.stop.setOnClickListener {
            showAlert()
        }

        binding.pause.setOnClickListener {
            pause()
            binding.start.isVisible = true
            binding.stop.isVisible = true
            binding.pause.isVisible = false
            binding.lap.isVisible = false
        }

        binding.lap.setOnClickListener {
            lap()
        }

        initCountView()
    }

    private fun initCountView(){
        binding.countText.text = String.format("%02d",countDown)
        binding.countPro.progress = 100
    }

    private fun start() {
        timer = timer(initialDelay = 0, period = 100){// 딜레이가 0 100ms 0.1초마다
            if (currentCountdown == 0){
                currentSecond += 1
                val min = currentSecond.div(10)/60
                val second = currentSecond.div(10)%60
                val deciSecond = currentSecond%10

                runOnUiThread{
                    // runOnUiThread를 쓰지않으면 메인스레드에서 실행하지 않은 정보를 메인 스레드에서 접근하려하기 때문에 오류발생
                    binding.timeText.text = String.format("%02d:%02d",min,second)
                    binding.tickText.text = deciSecond.toString()
                    binding.countGroup.isVisible = false
                }
            }
            else{
                currentCountdown -= 1
                val second = currentCountdown/10
                val progress = (currentCountdown / (countDown * 10f))*100

                binding.root.post {
                    binding.countText.text = String.format("%02d",second)
                    binding.countPro.progress = progress.toInt()
                }
            }
            if(currentSecond == 0 && currentCountdown < 31 && currentCountdown % 10 == 0){
                val Tone = if (currentCountdown == 0) ToneGenerator.TONE_CDMA_ABBR_ALERT else ToneGenerator.TONE_CDMA_ANSWER
                ToneGenerator(AudioManager.STREAM_ALARM,ToneGenerator.MAX_VOLUME).startTone(Tone,100)
            }
        }
    }

    private fun pause() {
        timer?.cancel()
        timer = null
    }

    private fun stop() {
        binding.start.isVisible = true
        binding.stop.isVisible = true
        binding.pause.isVisible = false
        binding.lap.isVisible = false

        currentSecond = 0
        binding.timeText.text = "00:00"
        binding.tickText.text = "0"

        binding.countGroup.isVisible = true
        initCountView()
        binding.lapLinear.removeAllViews()
    }

    private fun lap() {
        if (currentSecond == 0)
            return
        val container = binding.lapLinear
        TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
            val min = currentSecond.div(10) / 60
            val sec = currentSecond.div(10)%60
            val dec = currentSecond % 10
            text = "${container.childCount.inc().toString()}. " + String.format("%02d:%02d %01d",min,sec,dec)
            setPadding(30)
        }.let { lapTextView->
            container.addView(lapTextView,0)
        }
        //val lapTextView = TextView 이렇게 받고 container.addView(lapTextView,0) let대신 이렇게 해도 됨
    }

    private fun showCountdown(){
        AlertDialog.Builder(this).apply {
            val dialogBinding = DialogCountdownSettingBinding.inflate(layoutInflater)
            with(dialogBinding.countdownSecondPicker){
                maxValue = 20
                minValue = 0
                value = countDown
            }
            setTitle("카운트다운 설정")
            setView(dialogBinding.root)
            setPositiveButton("확인"){_,_->
                countDown = dialogBinding.countdownSecondPicker.value
                currentCountdown = countDown * 10
                binding.countText.text = String.format("%02d",countDown) // 03초 이런식으로 표시되게
            }
            setNegativeButton("취소",null)
        }.show()
    }

    private fun showAlert() {
        AlertDialog.Builder(this).apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("네") { _, _ ->
                stop()
            }
            setNegativeButton("아니요", null)
        }.show()
    }
}