package com.ashwingk.treasurehunt

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import java.util.*

class LoaderActivity : Activity() {

    var mContext: Context ?= null
    internal val TAG = "LoaderActivivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)
        mContext = this
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        val uid = prefs.getString(Constants.UID_PREFERENCE, "")
        val timer_started = prefs.getBoolean(Constants.TIMER_STARTED_PREF, false)
        val timer_up = prefs.getBoolean(Constants.TIME_UP_PREF, false)
        Log.d(TAG, "uid, timerstarted and timerup is ${uid} ${timer_started} and ${timer_up}")
        if(timer_up) {
            startActivity(Intent(mContext, ThankYou::class.java))
            finish()
        }
        else {
            if(!timer_started) {
                startTimer()
                val ed = prefs.edit()
                ed.putBoolean(Constants.TIMER_STARTED_PREF, true)
                ed.apply()
            }
            if (uid == "") {
                val i = Intent(this@LoaderActivity, SignUpActivity::class.java)
                startActivity(i)
                finish()
            } else {
                checkProgress()
            }
        }
    }

    fun startTimer() {
        val myInt = Intent(mContext, TimerReceiver::class.java)
        myInt.type = Constants.TIME_UP
        val myIntent = PendingIntent.getBroadcast(mContext,
                0, myInt, 0)
        val alrm_mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val c = Calendar.getInstance()
        c.add(Calendar.MINUTE, 100)
        alrm_mgr.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, myIntent)
        val remIntent = Intent(mContext, TimerReceiver::class.java)
        remIntent.type = Constants.IS_REMINDER
        val reminderIntent = PendingIntent.getBroadcast(mContext,
                0, remIntent, 0)
        c.add(Calendar.MINUTE, -10)

        alrm_mgr.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, reminderIntent)
        c.add(Calendar.MINUTE, 10)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(Constants.DEADLINE_PREF, c.timeInMillis).apply()
    }

    fun checkProgress() {
        val dbhelper = QuestionDbHelper(mContext)
        val lastQuestion = dbhelper.lastEntry
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
                    if (questions.contains("h4")) {
                        k = "completed"
                        val m = PreferenceManager.getDefaultSharedPreferences(this).getLong(Constants.TIME_FINISHED_PREF, 0)
                        if (m == 0.toLong()) {
                            PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(Constants.TIME_FINISHED_PREF, Calendar.getInstance().timeInMillis).apply()
                        }
                    }
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
