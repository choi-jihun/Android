package com.example.chapter2_3

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.chapter2_3.databinding.ActivityEditBinding

class EditActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ABOSpinner.adapter = ArrayAdapter.createFromResource(
            this,R.array.ABO_types,android.R.layout.simple_list_item_1
        )

        binding.birthLayer.setOnClickListener {
            val listener = OnDateSetListener{ _,year,month,dayOfMonth->
                binding.birthValue.text = "$year-${month+1}-$dayOfMonth"
            }
            DatePickerDialog(
                this,listener,2023,2,6
            ).show()
        }

        binding.check.setOnCheckedChangeListener { _, isChecked ->
            binding.warningEdit.isVisible = isChecked

        }
        binding.warningEdit.isVisible = binding.check.isChecked

        binding.save.setOnClickListener {
            saveData()
            finish()
        }
    }

    private fun saveData(){
        with(getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit()){
            putString(NAME,binding.nameEditText.text.toString())
            putString(ABO,getABOtype())
            putString(EMERGENCY,binding.emergencyEdit.text.toString())
            putString(BIRTHDATE,binding.birthValue.text.toString())
            putString(WARNING,getWarning())
            apply()
        }

        Toast.makeText(this,"데이터 저장 완료!",Toast.LENGTH_SHORT).show()
    }

    private fun getABOtype() : String{
        val blood = binding.ABOSpinner.selectedItem.toString()
        val bloodSign = if(binding.Rhplus.isChecked) "Rh+" else "RH-"
        return "$bloodSign$blood"
    }

    private fun getWarning() : String{
        return if(binding.check.isChecked) binding.warningEdit.text.toString() else ""
    }
}