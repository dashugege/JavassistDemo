package com.xiaomu.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var btn : Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn = findViewById<Button>(R.id.tv1)

        btn?.setOnClickListener {
            startActivity(Intent(this,MainActivity2::class.java))
        }
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

    }

    fun test(){
        Toast.makeText(this,btn.toString(),Toast.LENGTH_SHORT).show()
    }

}


// https://juejin.cn/post/6844903891138674696
//https://www.jianshu.com/p/3ecada85ca84