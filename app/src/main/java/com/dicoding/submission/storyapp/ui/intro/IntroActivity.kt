package com.dicoding.submission.storyapp.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.submission.storyapp.data.pref.DataStoreHelper
import com.dicoding.submission.storyapp.databinding.ActivityIntroBinding
import com.dicoding.submission.storyapp.ui.login.LoginActivity
import com.dicoding.submission.storyapp.ui.register.RegisterActivity
import com.dicoding.submission.storyapp.ui.story.StoryActivity

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah pengguna sudah login
        lifecycleScope.launchWhenCreated {
            DataStoreHelper.isLoggedIn(applicationContext).collect { isLoggedIn ->
                if (isLoggedIn) {
                    // Jika sudah login, langsung menuju ke MainActivity tanpa melalui IntroActivity
                    val intent = Intent(this@IntroActivity, StoryActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup IntroActivity agar pengguna tidak bisa kembali
                    return@collect
                }
            }
        }

        binding.btnLoginIntro.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegisterIntro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
