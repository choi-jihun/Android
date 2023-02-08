package com.example.chapter2_6

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chapter2_6.databinding.ActivityAddBinding
import com.example.chapter2_6.databinding.ActivityMainBinding
import java.nio.file.Files.delete

class MainActivity : AppCompatActivity(), WordAdapter.ItemClickLister {
    private lateinit var binding: ActivityMainBinding
    private lateinit var wordAdapter: WordAdapter
    private var selectedWord: Word? = null
    private val updateAddWord =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val isUpdated = result.data?.getBooleanExtra("isUpdated", false) ?: false
            if (result.resultCode == RESULT_OK && isUpdated) {
                updateAddWord()
            }
        }

    private val updateEditWord =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            val editWord = result.data?.getParcelableExtra<Word>("editWord")
            if (result.resultCode == RESULT_OK && editWord != null) {
                updateEditWord(editWord)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        binding.add.setOnClickListener {

            Intent(this, AddActivity::class.java).let {
                updateAddWord.launch(it)
            }
        }

        binding.delete.setOnClickListener {
            delete()
        }

        binding.edit.setOnClickListener {
            edit()
        }
    }

    private fun initRecyclerView() {
        wordAdapter = WordAdapter(mutableListOf(), this)
        binding.recycle.apply {
            adapter = wordAdapter
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration =
                DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }

        Thread {
            val list = AppDatabase.getInstance(this)?.wordDao()?.getAll() ?: emptyList()
            wordAdapter.list.addAll(list)
            runOnUiThread { wordAdapter.notifyDataSetChanged() } // 이걸 넣어줘야 화면에 데이터가 변경된것을 확인하고 변경

        }.start()

    }

    private fun updateAddWord() {
        Thread {
            AppDatabase.getInstance(this)?.wordDao()?.getLatestWord()?.let { word ->
                wordAdapter.list.add(0, word)
                runOnUiThread { wordAdapter.notifyDataSetChanged() }
            }

        }.start()
    }

    private fun updateEditWord(word: Word) {
        val index = wordAdapter.list.indexOfFirst { it.id == word.id }
        wordAdapter.list[index] = word
        runOnUiThread {
            selectedWord = word
            wordAdapter.notifyItemChanged(index)
            binding.textTxtView.text = word.text
            binding.meanText.text = word.mean
        }
    }

    override fun onClick(word: Word) {
        selectedWord = word
        binding.textTxtView.text = word.text
        binding.meanText.text = word.mean
    }

    private fun delete() {
        if (selectedWord == null)
            return
        Thread {
            selectedWord?.let {
                AppDatabase.getInstance(this)?.wordDao()?.delete(it)
                runOnUiThread {
                    wordAdapter.list.remove(it)
                    wordAdapter.notifyDataSetChanged()
                    binding.textTxtView.text = ""
                    binding.meanText.text = ""
                    Toast.makeText(this, "삭제완료", Toast.LENGTH_SHORT).show()
                }

            }
        }.start()
    }

    private fun edit() {
        if (selectedWord == null)
            return
        val intent = Intent(this, AddActivity::class.java).putExtra("originWord", selectedWord)
        updateEditWord.launch(intent)
    }
}