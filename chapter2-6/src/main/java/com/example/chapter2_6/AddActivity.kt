package com.example.chapter2_6

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import com.example.chapter2_6.databinding.ActivityAddBinding
import com.google.android.material.chip.Chip

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var originWord: Word? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        binding.addBtn.setOnClickListener {
            if (originWord == null) add() else edit()
            add()
        }
    }

    private fun initViews() {
        val types = listOf("명사", "동사", "대명사", "형용사", "부사", "감탄사", "전치사", "접속사")
        binding.chipGroup.apply {
            types.forEach {
                addView(creteChip(it))
            }
        }

        binding.textInputEditText.addTextChangedListener {
            it?.let { text->
                binding.textInputLayout.error = when(text.length){
                    0->"값을 입력하세요"
                    1 -> "2자 이상을 입력하세요"
                    else -> null
                }
            }
        }

        binding.meanInputEditText.addTextChangedListener {
            it?.let {
                binding.meanInputLayout.error = when(it.length){
                    0->"뜻을 입력하세요"
                    1-> "2자 이상을 입력하세요"
                    else -> null
                }
            }
        }

        originWord = intent.getParcelableExtra("originWord")
        originWord?.let { word ->
            binding.textInputEditText.setText(word.text)
            binding.meanInputEditText.setText(word.mean)
            val selectedChip =
                binding.chipGroup.children.firstOrNull { (it as Chip).text == word.type } as? Chip
            selectedChip?.isChecked = true
        }
    }

    private fun creteChip(text: String): Chip {
        return Chip(this).apply {
            setText(text)
            isCheckable = true
            isClickable = true
        }
    }

    private fun add() {
        val text = binding.textInputEditText.text.toString()
        val mean = binding.meanInputEditText.text.toString()
        val type = findViewById<Chip>(binding.chipGroup.checkedChipId)?.text.toString()
        val word = Word(text, mean, type)

        when(type){
            null -> runOnUiThread { println(Toast.makeText(this,"품사를 선택하세요",Toast.LENGTH_SHORT)) }
            else -> "알 수 없음"
        }

        Thread {
            AppDatabase.getInstance(this)?.wordDao()?.insert(word)
            runOnUiThread { println(Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT)) }
            val intent = Intent().putExtra("isUpdated", true)
            setResult(RESULT_OK, intent)
            finish()
        }.start()
    }

    private fun edit() {
        val text = binding.textInputEditText.text.toString()
        val mean = binding.meanInputEditText.text.toString()
        val type = findViewById<Chip>(binding.chipGroup.checkedChipId).text.toString()
        val editWord = originWord?.copy(text = text, mean = mean, type = type)

        Thread {
            editWord?.let {
                AppDatabase.getInstance(this)?.wordDao()?.update(it)
                val intent = Intent().putExtra("editWord", editWord)
                setResult(RESULT_OK,intent)
                runOnUiThread { Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show() }
                finish()
            }
        }.start()
    }
}