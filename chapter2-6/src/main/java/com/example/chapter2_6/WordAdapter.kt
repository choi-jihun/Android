package com.example.chapter2_6

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chapter2_6.databinding.ItemWordBinding

class WordAdapter(val list: MutableList<Word>, private val itemClickLister: ItemClickLister?=null) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
        val binding = ItemWordBinding.inflate(inflater,parent,false)
        return WordViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
//        holder.binding.apply {
//            val word = list[position]
//            textTextView.text = word.text
//            meanTextView.text = word.mean
//            typeChip.text = word.type
//        } WordViewHolder의 bind함수와 같은 기능
        val word = list[position]
        holder.bind(list[position])
        holder.itemView.setOnClickListener { itemClickLister?.onClick(word) }
    }

    class WordViewHolder(val binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(word: Word){
            binding.apply {
                textTextView.text = word.text
                meanTextView.text = word.mean
                typeChip.text = word.type
            }
        }
    }

    interface ItemClickLister{
        fun onClick(word: Word)
    }
}