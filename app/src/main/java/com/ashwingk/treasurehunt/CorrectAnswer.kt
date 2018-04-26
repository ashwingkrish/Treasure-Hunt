package com.ashwingk.treasurehunt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button

import java.util.ArrayList
import java.util.Random

class CorrectAnswer : AppCompatActivity() {

    var btn_continue: Button ?=null
    internal var mContext: Context ?= null
    internal var TAG = "CorrectAnswer"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correct_answer)
        mContext = this
        val uid = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.UID_PREFERENCE, "")

        if (uid == "") {
            val i = Intent(this@CorrectAnswer, SignUpActivity::class.java)
            startActivity(i)
            finish()
        }
        btn_continue = findViewById<View>(R.id.btn_correct_continue) as Button
        btn_continue?.setOnClickListener { checkProgress() }
    }

    fun checkProgress() {
        val dbhelper = QuestionDbHelper(mContext)
        val lastQuestion = dbhelper.lastEntry
        Log.d(TAG, "checkProgress: " + lastQuestion[0] + " and " + lastQuestion[1])
        if (lastQuestion.size < 2 || lastQuestion[1] == "1") {
            //calculate new question and insert
            val questions = dbhelper.answeredQuestions
            var k = ""
            if (questions.contains("e2") && questions.contains("e3") && questions.contains("e4") &&
                    questions.contains("e5") && questions.contains("e6") && questions.contains("e7") &&
                    questions.contains("e8")) {
                //now in level m
                if (questions.contains("m2") && questions.contains("m3") && questions.contains("m4") &&
                        questions.contains("m5") && questions.contains("m6") && questions.contains("m7") &&
                        questions.contains("m8") && questions.contains("m1")) {
                    //now in level h
                    if (questions.contains("h4"))
                        k = "completed"
                    else if (questions.contains("h3"))
                        k = "h4"
                    else if (questions.contains("h2"))
                        k = "h3"
                    else if (questions.contains("h1"))
                        k = "h2"
                    else
                        k = "h1"
                } else {
                    val arr = ArrayList<Int>()
                    arr.add(1)
                    arr.add(2)
                    arr.add(3)
                    arr.add(4)
                    arr.add(5)
                    arr.add(6)
                    arr.add(7)
                    arr.add(8)
                    while (arr.size > 0) {
                        val generator = Random()
                        val i = generator.nextInt(arr.size)
                        val s = "m" + arr[i]
                        if (questions.contains(s))
                            arr.removeAt(i)
                        else {
                            k = s
                            break
                        }
                    }
                }
            } else {
                val arr = ArrayList<Int>()
                arr.add(2)
                arr.add(3)
                arr.add(4)
                arr.add(5)
                arr.add(6)
                arr.add(7)
                arr.add(8)
                while (arr.size > 0) {
                    val generator = Random()
                    val i = generator.nextInt(arr.size)
                    val s = "e" + arr[i]
                    if (questions.contains(s))
                        arr.removeAt(i)
                    else {
                        k = s
                        break
                    }
                }
            }
            Log.d(TAG, "checkProgress: selected question is " + k)
            if (k == "completed") {
                val i = Intent(mContext, ThankYou::class.java)
                startActivity(i)
                finish()
            } else {
                dbhelper.addQuestion(k)
                val i = Intent(mContext, QuestionActivity::class.java)
                i.putExtra(Constants.INTENT_QUESTION, k)
                startActivity(i)
                finish()
            }
        } else {
            val i = Intent(mContext, QuestionActivity::class.java)
            i.putExtra(Constants.INTENT_QUESTION, lastQuestion[0])
            startActivity(i)
            finish()
        }
    }
}
