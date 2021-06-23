package com.example.yuehaoting.playinterface.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.yuehaoting.R

import com.example.yuehaoting.databinding.PlayActivityBinding

class PlayActivity : AppCompatActivity() {
    private lateinit var binding: PlayActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=  PlayActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}