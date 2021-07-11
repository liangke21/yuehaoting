package com.example.musiccrawler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musiccrawler.databinding.ActivityMainBinding


/**
 * 页面播放
 * 2021.5.8
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}

