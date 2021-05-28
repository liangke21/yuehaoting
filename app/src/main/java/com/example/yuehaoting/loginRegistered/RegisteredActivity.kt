package com.example.yuehaoting.loginRegistered

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.yuehaoting.Data
import com.example.yuehaoting.R
import com.example.yuehaoting.loginRegistered.base.MyAppCompatActivity
import com.example.yuehaoting.loginRegistered.entity.MNumber
import com.example.yuehaoting.loginRegistered.entity.Success
import com.example.yuehaoting.loginRegistered.entity.User
import com.example.yuehaoting.loginRegistered.retrofit.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern
import kotlin.concurrent.thread


class RegisteredActivity : MyAppCompatActivity(), View.OnClickListener {
    private lateinit var number: EditText
    private lateinit var password: EditText
    private lateinit var mPassword: EditText
    private var code: Success? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registered)
        supportActionBar?.hide()
        initView()
    }

    private fun initView() {
        number = findViewById(R.id.et_registered_number)
        password = findViewById(R.id.et_registered_password)
        mPassword = findViewById(R.id.et_registered_password_q)
        findViewById<ImageButton>(R.id.bnt_registered).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bnt_registered -> {
                accountPasswordVerification(number.text.toString(), password.text.toString(), mPassword.text.toString())

            }
        }
    }

    private fun accountPasswordVerification(number: String, password: String, mPassword: String) {

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(password) || TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, "密码账号不能为空", Toast.LENGTH_SHORT).show()
        } else {
            val patten = Pattern.compile("[0-9]*")

            if (!patten.matcher(number).matches() || !patten.matcher(password).matches() || !patten.matcher(mPassword)
                    .matches()
            ) {
                Toast.makeText(this, "账号密码必须为整数", Toast.LENGTH_SHORT).show()
            } else {
                if ((number.length in 6..11) && (password.length in 6..11) && (mPassword.length in 6..11)) {
                    if (password == mPassword) {
                        isItPureUser(number, mPassword)


                    } else {
                        Toast.makeText(this, "密码和确认密码不一致", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "账号密码长度必须为 6..11", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    /**
     * 判断账户是否纯在
     */
    private fun isItPureUser(number: String, mPassword: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("${Data.apiUri}/songs/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mNumber = retrofit.create(LoginService::class.java)
        mNumber.inquire(number).enqueue(object : Callback<MNumber> {
            override fun onResponse(call: Call<MNumber>, response: Response<MNumber>) {
                val sNumber = response.body()
                if (sNumber!!.number == number && !TextUtils.isEmpty(sNumber!!.number)) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "账户已存在", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    loginRetrofit(number, mPassword)
                    Thread.sleep(3000);//休眠3秒
                    finish()
                }

            }

            override fun onFailure(call: Call<MNumber>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * 账户密码添加进数据库
     */
    private fun loginRetrofit(number: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("${Data.apiUri}/songs/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val login = retrofit.create(LoginService::class.java)
        login.addData(User(number, password)).enqueue(object : Callback<Success> {
            override fun onResponse(call: Call<Success>, response: Response<Success>) {
                code = response.body()
                runOnUiThread {
                    Toast.makeText(applicationContext, code!!.code, Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<Success>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}