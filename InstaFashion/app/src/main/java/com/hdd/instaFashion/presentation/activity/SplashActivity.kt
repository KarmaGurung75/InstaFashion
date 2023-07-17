package com.hdd.instaFashion.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.User
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    var email: String? = ""
    var password: String? = ""
    var token: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        retrieveUserDetails()
        SystemClock.sleep(2000)
    }

    private fun retrieveUserDetails() {
        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE)
        email = sharedPreferences.getString("email", "")
        password = sharedPreferences.getString("password", "")
        token = sharedPreferences.getString("token", "")
        withApiRetrofit()
    }

    private fun withApiRetrofit() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = UserRepository().loginUser(User(email = email!!, password = password!!))
            if (response.success == true) {
                startActivity(Intent(this@SplashActivity, BottomActivity::class.java))
                ServiceBuilder.token = "$token"
                ServiceBuilder.user = response.data!!
                finish()
            } else {
                val hasAlreadyInstall = getSharedPreferences("userAuth", MODE_PRIVATE).contains("email")
                if (hasAlreadyInstall) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                }
            }
        }
    }

}