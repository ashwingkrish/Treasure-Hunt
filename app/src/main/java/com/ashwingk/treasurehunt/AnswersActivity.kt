package com.ashwingk.treasurehunt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout

class AnswersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answers)
        val answers = QuestionDbHelper(this).answers

        val adapter = AnswerAdapter(answers)
        val recyclerView : RecyclerView = findViewById(R.id.rv_answered_questions)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = adapter
    }
}
