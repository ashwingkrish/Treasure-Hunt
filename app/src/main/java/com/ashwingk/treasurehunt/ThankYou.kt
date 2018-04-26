package com.ashwingk.treasurehunt

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ThankYou : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)
        val tv : TextView = findViewById(R.id.tv_thank_you)
        val dbhelper = QuestionDbHelper(this)
        val timer_up = true
        var finished = false

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val completed = dbhelper.answeredQuestions.size >= Constants.NUM_QUESTIONS
        if (completed) {
            val t_finish = prefs.getLong(Constants.TIME_FINISHED_PREF, 0)
            val t_deadline = prefs.getLong(Constants.DEADLINE_PREF, 0)
            val diff = t_deadline - t_deadline
            if(t_finish == 0.toLong() || t_deadline == 0.toLong() || diff <= 0.toLong())
                tv.text = "Thank You!\nYou have completed the Hunt!"
            else {
                val seconds_taken = 600 - diff/1000
                val mins: Long = seconds_taken / 60
                var secs: Long = seconds_taken % 60
                tv.text = "Thank You!\nYou have completed the hunt in ${mins}:${secs}\nRun to the pavillion now!"
            }
            finished = true
        }
        val uid = prefs.getString(Constants.UID_PREFERENCE, "")
        val b : Button = findViewById(R.id.btn_answered_questions)
        b.setOnClickListener {
            startActivity(Intent(this@ThankYou, AnswersActivity::class.java))
        }
        if(uid != "") {
            val childObj: HashMap<String, Any> = HashMap()
            childObj["timer_up"] = timer_up
            childObj["finished"] = finished
            FirebaseDatabase.getInstance().getReference("users/"+uid).updateChildren(childObj)
        }
    }
}
