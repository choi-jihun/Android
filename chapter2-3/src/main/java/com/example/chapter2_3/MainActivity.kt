package com.example.chapter2_3

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.chapter2_3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goInputActivity.setOnClickListener {
            val intent = Intent(this,EditActivity::class.java)
            startActivity(intent)
        }

        binding.delete.setOnClickListener {
            deleteData()
        }

        binding.callLayer.setOnClickListener {
            with(Intent(Intent.ACTION_VIEW)){
                val phoneNumber = binding.emergencyValue.text.toString()
                    .replace("-","")
                data = Uri.parse("tel:$phoneNumber")
                startActivity(this)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        getDataAndUpdate()
    }

    private fun getDataAndUpdate(){
        with(getSharedPreferences(USER_INFO,Context.MODE_PRIVATE)){
            binding.nameValue.text = getString(NAME,"미정")
            binding.ABOValue.text = getString(ABO,"미정")
            binding.birthValue.text = getString(BIRTHDATE,"미정")
            binding.emergencyValue.text = getString(EMERGENCY,"미정")
            val warning = getString(WARNING,"")

            binding.warning.isVisible = false
            binding.warningValue.isVisible = false

            if(!warning.isNullOrEmpty()){
                binding.warning.isVisible = true
                binding.warningValue.isVisible= true
                binding.warningValue.text = warning
            }

        }
    }

    private fun deleteData(){
        with(getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit()) {
            clear()
            apply()
            getDataAndUpdate()
        }
        Toast.makeText(this,"초기화 완료",Toast.LENGTH_SHORT).show()
    }
}