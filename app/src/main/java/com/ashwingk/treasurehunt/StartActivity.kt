package com.ashwingk.treasurehunt

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*

class StartActivity : AppCompatActivity() {

    var uid: String?=null
    var mContext: Context?=null
    var et_code: EditText?=null
    var start: Button?=null
    internal var TAG = "StartActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        mContext = this
        uid = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.UID_PREFERENCE, "")
        if (uid == "") {
            Toast.makeText(mContext, "Please register.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@StartActivity, SignUpActivity::class.java))
            finish()
        }
        Log.d(TAG, "Hello")
        start = findViewById(R.id.btn_start)
        et_code = findViewById(R.id.et_start_code)
        start?.setOnClickListener {
            val k = et_code?.text.toString().trim { it <= ' ' }
            if (k == "TH2K17") {
                Toast.makeText(mContext, "Correct code, get ready!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(mContext, LoaderActivity::class.java))
                finish()
            }
            else
                Toast.makeText(mContext, "Invalid code.", Toast.LENGTH_SHORT).show()
        }
    }



}
