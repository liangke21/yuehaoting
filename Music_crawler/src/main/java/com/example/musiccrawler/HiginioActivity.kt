package com.example.musiccrawler

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.example.musiccrawler.hifini.HiginioService

class HiginioActivity : AppCompatActivity() {


    private var mIMyAidlInterface: AidlInterfaceMy? = null
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mIMyAidlInterface = AidlInterfaceMy.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        bindService(
            Intent(this, HiginioService::class.java),
            mServiceConnection,
            Context.BIND_AUTO_CREATE

        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hifinio)
    }
}