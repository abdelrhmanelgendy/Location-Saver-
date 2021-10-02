package com.example.locationsaver.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.locationsaver.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplahActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splah)
        GlobalScope.launch {
            delay(1200)
            val intent = Intent(this@SplahActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}