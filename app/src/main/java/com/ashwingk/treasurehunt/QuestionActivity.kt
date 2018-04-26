package com.ashwingk.treasurehunt

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.*
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.SecretKeySpec
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.thread

class QuestionActivity : AppCompatActivity() {

    var btn_scan: Button ?= null
    var submit_code: Button ?= null
    var iv_question: ImageView ?= null
    var et_code: EditText ?= null
    var mContext: Context ?= null
    var tv_answered: TextView?=null
    var tv_timer: TextView?=null
    internal var question = ""
    internal var URL = ""
    private var qrScan: IntentIntegrator? = null
    internal var uid = ""
    internal var TAG = "QuestionActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        iv_question = findViewById(R.id.iv_question)
        et_code = findViewById(R.id.et_question_code)
        submit_code = findViewById(R.id.btn_question_submit)
        btn_scan = findViewById(R.id.btn_question_scan)
        tv_timer = findViewById(R.id.tv_question_timer)
        tv_answered = findViewById(R.id.tv_question_answered)

        qrScan = IntentIntegrator(this)
        mContext = this
        uid = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.UID_PREFERENCE, "")

        val helper = QuestionDbHelper(this)
        tv_answered?.setText("Answered: "+helper.answeredQuestions.size)

        btn_scan?.setOnClickListener { qrScan!!.initiateScan() }

        submit_code?.setOnClickListener {
            val answer = et_code?.text.toString().trim { it <= ' ' }
            answerQuestion(answer)
        }
        question = intent.extras!!.getString(Constants.INTENT_QUESTION)
        loadQuestion()
        initTimer()
    }
    fun loadQuestion() {
        val pd: ProgressDialog = ProgressDialog.show(mContext, "", "Loading question...",
                true)
        thread(start = true, isDaemon = false, name = "QuestionThread", block = {
            var cipherIn : CipherInputStream ?= null
            try {
                val encodedKey = getString(R.string.e1_code)
                val cipher = Cipher.getInstance("AES")


                val decodedKey = android.util.Base64.decode(encodedKey, android.util.Base64.DEFAULT)
                val key = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
                cipher.init(Cipher.DECRYPT_MODE, key)

                cipherIn = CipherInputStream(resources.openRawResource(getFile(question)), cipher)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            runOnUiThread {
                if(cipherIn == null) {
                    Toast.makeText(mContext, "Could not load question", Toast.LENGTH_SHORT).show()
                } else {
                    val b = BitmapFactory.decodeStream(cipherIn)
                    iv_question?.setImageBitmap(b)
                }
                pd.dismiss()
            }
        })
    }
    fun initTimer() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val deadline = prefs.getLong(Constants.DEADLINE_PREF, 0)

        val c = Calendar.getInstance()
        var diff = deadline - c.timeInMillis
        diff /= 1000

        var mins = diff/60
        diff %= 60
        var secs = diff
        tv_timer?.setText("Time Remaining: ${mins}:${secs}")


        thread(start = true, isDaemon = false, name = "TimerThread", block= {
            val timer = Timer("schedule", true)
            timer.scheduleAtFixedRate(0, 1000) {
                runOnUiThread {
                    if (mins <= 0 && secs <= 0) {
                        tv_timer?.setText("Time Remaining: 00:00")
                        timer.cancel()
                    } else {
                        secs--
                        if (secs < 0) {
                            secs = 59
                            mins--
                        }
                        tv_timer?.setText("Time Remaining: ${mins}:${secs}")
                    }
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            //if qrcode has nothing in it
            if (result.contents == null) {
                Toast.makeText(this, "QR code could not be scanned", Toast.LENGTH_LONG).show()
            } else {
                //if qr contains data
                val res = result.contents
                et_code?.setText(res)
                submit_code?.callOnClick()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun answerQuestion(answer: String) {
        try {
            val encodedKey = getString(R.string.e1_code)
            val cipher = Cipher.getInstance("AES")
            val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)
            val key = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val encrypted = cipher.doFinal(answer.toByteArray())
            val enc = Base64.encodeToString(encrypted, Base64.DEFAULT)
            if (enc.trim { it <= ' ' } == getCode(question).trim { it <= ' ' }) {
                val helper = QuestionDbHelper(mContext)
                helper.answerQuestion(question, Calendar.getInstance().timeInMillis, answer)
                val i = Intent(this@QuestionActivity, CorrectAnswer::class.java)
                startActivity(i)
                finish()
            } else {
                Log.d(TAG, "answerQuestion: enc code is " + enc + " answer is " + answer + " actual answer is " + getCode(question))
                Toast.makeText(mContext, "Incorrect answer", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    inner class LoadImage : AsyncTask<Void, Void, Void>() {

        var cipherIn: CipherInputStream ?= null
        var pd: ProgressDialog = ProgressDialog.show(mContext, "", "Loading question...",
                true)

        override fun onPreExecute() {
            pd.show()
            super.onPreExecute()
        }

        override fun doInBackground(vararg voids: Void): Void? {
            try {
                val encodedKey = getString(R.string.e1_code)
                val cipher = Cipher.getInstance("AES")


                val decodedKey = android.util.Base64.decode(encodedKey, android.util.Base64.DEFAULT)
                val key = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
                cipher.init(Cipher.DECRYPT_MODE, key)

                cipherIn = CipherInputStream(resources.openRawResource(getFile(question)), cipher)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(aVoid: Void) {
            pd.dismiss()
            val b = BitmapFactory.decodeStream(cipherIn)
            iv_question?.setImageBitmap(b)
            super.onPostExecute(aVoid)
        }
    }

    fun getFile(question: String): Int {
        val resId: Int
        when (question) {
            "e2" -> resId = R.raw.e2_enc
            "e3" -> resId = R.raw.e3_enc
            "e4" -> resId = R.raw.e4_enc
            "e5" -> resId = R.raw.e5_enc
            "e6" -> resId = R.raw.e6_enc
            "e7" -> resId = R.raw.e7_enc
            "e8" -> resId = R.raw.e8_enc
            "m1" -> resId = R.raw.m1_enc
            "m2" -> resId = R.raw.m2_enc
            "m3" -> resId = R.raw.m3_enc
            "m4" -> resId = R.raw.m4_enc
            "m5" -> resId = R.raw.m5_enc
            "m6" -> resId = R.raw.m6_enc
            "m7" -> resId = R.raw.m7_enc
            "m8" -> resId = R.raw.m8_enc
            "h1" -> resId = R.raw.h1_enc
            "h2" -> resId = R.raw.h2_enc
            "h3" -> resId = R.raw.h3_enc
            "h4" -> resId = R.raw.h4_enc
            else -> resId = R.raw.e2_enc
        }
        return resId
    }

    fun getCode(question: String): String {
        val resId: Int
        when (question) {
            "e2" -> resId = R.string.e2_code
            "e3" -> resId = R.string.e3_code
            "e4" -> resId = R.string.e4_code
            "e5" -> resId = R.string.e5_code
            "e6" -> resId = R.string.e6_code
            "e7" -> resId = R.string.e7_code
            "e8" -> resId = R.string.e8_code
            "m1" -> resId = R.string.m1_code
            "m2" -> resId = R.string.m2_code
            "m3" -> resId = R.string.m3_code
            "m4" -> resId = R.string.m4_code
            "m5" -> resId = R.string.m5_code
            "m6" -> resId = R.string.m6_code
            "m7" -> resId = R.string.m7_code
            "m8" -> resId = R.string.m8_code
            "h1" -> resId = R.string.h1_code
            "h2" -> resId = R.string.h2_code
            "h3" -> resId = R.string.h3_code
            "h4" -> resId = R.string.h4_code
            else -> resId = R.string.e2_code
        }
        return getString(resId)
    }
}
