package com.example.yuehaoting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.yuehaoting.callback.MusicEvenCallback
import com.example.yuehaoting.databinding.ActivityMainBinding
import com.example.yuehaoting.databinding.MianAaaBinding


/**
 * 页面播放
 * 2021.5.8
 */
class MainActivity : AppCompatActivity()  {
    private lateinit var binding: MianAaaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MianAaaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e("MainActivity111", " onDestroy")

    }

}