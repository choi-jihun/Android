package com.example.chapter2_4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.chapter2_4.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    val firstNum = StringBuilder("")
    val secondNum = StringBuilder("")
    val operator = StringBuilder("")
    val decimal = DecimalFormat("#,###") //3자리마다 ,찍기 && 첫자리에 0오면 0삭제

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun numberClicked(view : View){
        val numString = (view as? Button)?.text?.toString() ?: ""
        val numText = if(operator.isEmpty()) firstNum else secondNum
        numText.append(numString)
        updateCalText()
    }

    fun clearClicked(view : View){
        firstNum.clear()
        secondNum.clear()
        operator.clear()
        updateCalText()
        binding.resultText.text = ""
    }

    fun equalClicked(view : View){
        if(firstNum.isEmpty() || secondNum.isEmpty() || operator.isEmpty()){
            Toast.makeText(this,"올바르지 않은 수식 입니다.",Toast.LENGTH_SHORT).show()
            return
        }
        val firstNumber = firstNum.toString().toBigDecimal() // 큰 수도 입력 및 계산 가능
        val secondNumber = secondNum.toString().toBigDecimal()

        val result = when(operator.toString()){
            "+" -> decimal.format(firstNumber+secondNumber)
            "-" -> decimal.format(firstNumber-secondNumber)
            else ->
                "잘못된 수식 입니다."
        }
        binding.resultText.text = result

    }

    fun operatorClicked(view : View){
        val opString = (view as? Button)?.text?.toString() ?: ""
        if(firstNum.isEmpty()){
            Toast.makeText(this,"숫자를 먼저 입력하세요.",Toast.LENGTH_SHORT).show()
            return
        }
        if(secondNum.isNotEmpty()){
            Toast.makeText(this,"1개의 연산자에 대해서만 연산이 가능.",Toast.LENGTH_SHORT).show()
            return
        }

        operator.append(opString)
        updateCalText()
    }

    private  fun updateCalText(){
        val firstFormattedNumber = if(firstNum.isNotEmpty()) decimal.format(firstNum.toString().toBigDecimal()) else ""
        val secondFormattedNumber = if(secondNum.isNotEmpty()) decimal.format(secondNum.toString().toBigDecimal()) else ""

        binding.calText.text="$firstFormattedNumber $operator $secondFormattedNumber"
    }
}