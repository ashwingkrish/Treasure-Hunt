package com.ashwingk.treasurehunt

import android.content.ClipData
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import kotlinx.android.synthetic.main.row_answered_question.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ashman on 12-10-2017.
 */
class AnswerAdapter(val items: List<Answer>) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_answered_question, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Answer) {
            itemView.tv_question_number.text = "Question: " + item.question
            itemView.tv_answer_time.text = "Time answered- " + SimpleDateFormat("hh:mm aa").format(Date(item.time))
            itemView.tv_question_code.text = "Code- " + item.answer
        }
    }
}