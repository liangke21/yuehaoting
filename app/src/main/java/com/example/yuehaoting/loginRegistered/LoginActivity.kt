package com.example.yuehaoting.loginRegistered

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.yuehaoting.base.DataUri
import com.example.yuehaoting.R
import com.example.yuehaoting.loginRegistered.base.MyAppCompatActivity
import com.example.yuehaoting.loginRegistered.entity.MNumber
import com.example.yuehaoting.loginRegistered.retrofit.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern


class LoginActivity : MyAppCompatActivity(), View.OnClickListener {
    private lateinit var etNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var registered: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        initView()
    }


    private fun initView() {
        etNumber = findViewById(R.id.et_number)
        etPassword = findViewById(R.id.et_password)
        findViewById<ImageButton>(R.id.bnt_login).setOnClickListener(this)
        registered = findViewById(R.id.tv_registered)
        registered.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        registered.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bnt_login -> {
                accountPasswordVerification(etNumber.text.toString(), etPassword.text.toString())

            }
            R.id.tv_registered -> {
                val intent = Intent(this, RegisteredActivity::class.java)
                startActivity(intent)
            }
        }

    }


    private fun accountPasswordVerification(number: String, password: String) {

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码账号不能为空", Toast.LENGTH_SHORT).show()
        } else {
            val patten = Pattern.compile("[0-9]*")

            if (!patten.matcher(number).matches() || !patten.matcher(password).matches()) {
                Toast.makeText(this, "账号密码必须为整数", Toast.LENGTH_SHORT).show()
            } else {
                if ((number.length in 6..11) && (password.length in 6..11)) {
                    isItPureUser(number, password)
                } else {
                    Toast.makeText(this, "账号密码长度必须为 6..11", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    /**
     * 判断账户是否纯在
     */
    private fun isItPureUser(number: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("${DataUri.apiUri}/songs/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mNumber = retrofit.create(LoginService::class.java)
        mNumber.inquire(number).enqueue(object : Callback<MNumber> {
            override fun onResponse(call: Call<MNumber>, response: Response<MNumber>) {
                val sNumber = response.body()
                if ((sNumber!!.number == number && !TextUtils.isEmpty(sNumber!!.number)) && (sNumber!!.password == password && !TextUtils.isEmpty(
                        sNumber!!.password
                    ))
                ) {
                    Log.e("跳转成功", "--------------")
                } else {
                    Toast.makeText(applicationContext, "账户或密码错误", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<MNumber>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


}