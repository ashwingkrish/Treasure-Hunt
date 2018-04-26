package com.ashwingk.treasurehunt

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

import java.util.HashMap

class SignUpActivity : AppCompatActivity() {

    var etnumber: EditText?=null
    var etparticipant1: EditText?=null
    var etparticipant2: EditText?=null
    var engineering: RadioButton?=null
    var nonengineering: RadioButton?=null
    var signup: Button?=null
    var login: Button?=null
    var mContext: Context?=null
    internal var URL = ""
    internal var TAG = "SignUpActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mContext = this
        etnumber = findViewById<EditText>(R.id.et_sign_up_phone)
        etparticipant1 = findViewById<EditText>(R.id.et_sign_up_participant1)
        etparticipant2 = findViewById<EditText>(R.id.et_sign_up_participant2)
        engineering = findViewById<RadioButton>(R.id.rb_sign_up_engineering)
        nonengineering = findViewById<View>(R.id.rb_sign_up_non_engineering) as RadioButton
        signup = findViewById<View>(R.id.btn_sign_up) as Button
        login = findViewById<View>(R.id.tv_sign_up_login) as Button
        signup?.setOnClickListener { signUp() }
        login?.setOnClickListener { login() }
    }

    fun signUp() {
        Log.d(TAG, "signUp: inside signup")

        val pd = ProgressDialog.show(mContext, "Sign Up", "Registering...",
                true)
        val participant1 = etparticipant1?.text.toString().trim { it <= ' ' }
        val participant2 = etparticipant2?.text.toString().trim { it <= ' ' }
        var number1 = etnumber?.text.toString().trim { it <= ' ' }.replace("[a-zA-Z]".toRegex(), "")
        if (number1.length > 3 && number1[0] == '+')
            number1 = number1.substring(3)
        val number = number1
        val isEngineering = engineering?.isChecked
        if (participant1 == "" || participant2 == "" || number.length != 10) {
            Toast.makeText(mContext, "Please enter valid details.", Toast.LENGTH_SHORT).show()
            pd.dismiss()
            return
        }
        val dbref = FirebaseDatabase.getInstance().getReference("users")
        dbref.orderByChild("phone_number").equalTo(number).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.childrenCount.toInt() != 0) {
                            Toast.makeText(mContext, "Phone number already registered. Login please.", Toast.LENGTH_SHORT).show()
                            pd.dismiss()
                        } else {
                            val params = HashMap<String, Any>()
                            params.put("participant1", participant1)
                            params.put("participant2", participant2)
                            params.put("engg", isEngineering.toString() + "")
                            params.put("phone_number", number)
                            params.put("token", FirebaseInstanceId.getInstance().token.toString())
                            val uid = dbref.push().key
                            dbref.updateChildren(params) { databaseError, databaseReference ->
                                if (databaseError != null) {
                                    pd.dismiss()
                                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                                } else {
                                    val ed = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                    ed.putString(Constants.UID_PREFERENCE, uid)
                                    ed.apply()
                                    val i = Intent(mContext, RulesActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(mContext, "Something went wrong.", Toast.LENGTH_SHORT).show()
                        pd.dismiss()
                    }
                }
        )
    }

    fun login() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val et_phone = EditText(this)
        et_phone.hint = "Registered Phone Number"
        et_phone.inputType = InputType.TYPE_CLASS_PHONE
        alertDialogBuilder.setView(et_phone)
        alertDialogBuilder.setTitle("Enter phone number")
        alertDialogBuilder.setPositiveButton("Login") { dialog, which ->
            var l = et_phone.text.toString().trim { it <= ' ' }.replace("[a-zA-Z]".toRegex(), "")
            if (l[0] == '+')
                l = l.substring(3)
            val k = l
            if (k.length == 10) {
                val pd = ProgressDialog.show(mContext, "", "Logging in...",
                        true)
                FirebaseDatabase.getInstance().getReference("users").orderByChild("phone_number").equalTo(k).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                pd.dismiss()
                                if (dataSnapshot.childrenCount.toInt() == 0) {
                                    Toast.makeText(mContext, "Could not find number.", Toast.LENGTH_SHORT).show()
                                } else {
                                    var uid = ""
                                    for (d in dataSnapshot.children) {
                                        uid = d.key
                                    }
                                    val ed = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                    ed.putString(Constants.UID_PREFERENCE, uid)
                                    ed.apply()
                                    val i = Intent(mContext, RulesActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                pd.dismiss()
                                Toast.makeText(mContext, "Something went wrong.", Toast.LENGTH_SHORT).show()
                            }
                        }
                )
            } else {
                Toast.makeText(mContext, "Please enter a valid number.", Toast.LENGTH_SHORT).show()
            }
        }
        alertDialogBuilder.show()
    }
}
