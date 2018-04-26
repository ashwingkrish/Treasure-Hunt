package com.ashwingk.treasurehunt

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button

import kotlinx.android.synthetic.main.content_rules.*

class RulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_rules)
        val btn : Button = findViewById(R.id.btn_rules_understood)
        btn.setOnClickListener {
            startActivity(Intent(this@RulesActivity, StartActivity::class.java))
            finish()
        }
    }

}
