package com.dicoding.submission.storyapp.ui.intro

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
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

        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logoMenu1, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleMenu1, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLoginIntro, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegisterIntro, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                btnLogin,
                btnRegister)
            start()
        }
    }
}
