package com.hdd.instaFashion.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.User
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    //Initialize
    private lateinit var et_sign_in_email: EditText
    private lateinit var et_sign_in_password: EditText
    private lateinit var tv_sign_in_forgot_password: TextView
    private lateinit var btnLogin: Button
    private lateinit var pb_sign_in: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        et_sign_in_email = findViewById(R.id.et_sign_in_email)
        et_sign_in_password = findViewById(R.id.et_sign_in_password)
        btnLogin = findViewById(R.id.btnLogin)
        pb_sign_in = findViewById(R.id.pb_sign_in)
        tv_sign_in_forgot_password = findViewById(R.id.tv_sign_in_forgot_password)
        checkRunTimePermission()

        tv_sign_in_forgot_password.setOnClickListener {
            forgotPasswordActivity()
        }

        et_sign_in_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputProvidedByUser()
            }
        })
        et_sign_in_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkInputProvidedByUser()
            }
        })
        btnLogin.setOnClickListener {
            checkForEmailAndPassword()
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun checkForEmailAndPassword() {
        if (Patterns.EMAIL_ADDRESS.matcher(et_sign_in_email.text).matches()) {
            if (et_sign_in_password.length() >= 5) {
                pb_sign_in.isVisible = true
                btnLogin.isVisible = false
                btnLogin.setTextColor(R.color.textDisabledColor)
                logInWithEmailAndPassword()
            } else {
                et_sign_in_password.error = "Password doesn't match"

            }
        } else {
            et_sign_in_email.error = "Please enter a valid email address"
            et_sign_in_email.requestFocus()
            return
        }
    }

    private fun logInWithEmailAndPassword() {

        // login with Api-Retrofit
        withApiRetrofit()
    }

    private fun withApiRetrofit() {
        val email = et_sign_in_email.text.toString()
        val password = et_sign_in_password.text.toString()
        // from retrofit-model
        val user = User(email = email, password = password)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.loginUser(user)

                if (response.success == true) {
                    ServiceBuilder.token = "Bearer ${response.accessToken!!}"
                    ServiceBuilder.uid = response.data!!._id
                    ServiceBuilder.user = response.data
                    withContext(Dispatchers.Main) {
                         addUserDetailInfo()
                        Toast.makeText(this@LoginActivity, "Successfully Login", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, BottomActivity::class.java))
//                        ServiceBuilder.user = response.data
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity,
                            "Invalid username or password",
                            Toast.LENGTH_SHORT).show()
                        pb_sign_in.isVisible = false
                        btnLogin.isVisible = true
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    pb_sign_in.isVisible = false
                    btnLogin.isVisible = true
                    Toast.makeText(this@LoginActivity,
                        "Invalid username or password",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // checking for runtime permission
    private fun checkRunTimePermission() {
        if (!hasPermission()) {
            requestPermission()
        }
    }
    private fun hasPermission(): Boolean {
        var hasPermission = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = false
                break
            }
        }
        return hasPermission
    }

    // requesting permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 1)
    }



    private fun addUserDetailInfo() {
        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", et_sign_in_email.text.toString())
        editor.putString("password", et_sign_in_password.text.toString())
        editor.putString("token", ServiceBuilder.token!!)
        editor.apply()
    }

    // check user details
    @SuppressLint("ResourceAsColor")
    private fun checkInputProvidedByUser() {
        if (!TextUtils.isEmpty(et_sign_in_email.text)) {
            if (et_sign_in_password.length() >= 5) {
                btnLogin.isEnabled = true
                btnLogin.setBackgroundColor(application.resources.getColor(R.color.buttonColor))
                btnLogin.setTextColor(application.resources.getColor(R.color.white))
                pb_sign_in.isEnabled = true
            } else {
                btnLogin.isEnabled = false
                btnLogin.setTextColor(R.color.textDisabledColor)
            }
        } else {
            btnLogin.isEnabled = false
            btnLogin.setTextColor(R.color.textDisabledColor)
        }
    }

    fun signUpActivity(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun forgotPasswordActivity() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    //on back press trigger
    override fun onBackPressed() {
        showAlertDialog()
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Close App")
        builder.setIcon(R.drawable.exit)
        builder.setMessage("Are you sure to close app ?")
//        builder.setIcon(R.id.nav_sign_out)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            super.onBackPressed()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel") { _, _ ->
            Toast.makeText(this, "clicked cancel", Toast.LENGTH_SHORT).show()
        }
        //performing negative action
        builder.setNegativeButton("No") { _, _ ->
            Toast.makeText(this, "clicked No", Toast.LENGTH_SHORT).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

}