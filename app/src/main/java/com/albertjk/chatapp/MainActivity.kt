package com.albertjk.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

private val TAG = MainActivity::class.qualifiedName

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}