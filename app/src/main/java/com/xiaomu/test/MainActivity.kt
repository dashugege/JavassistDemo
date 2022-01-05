package com.xiaomu.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var btn : Button?=null
    var btn2 : Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn = findViewById<Button>(R.id.tv1)
        btn2 = findViewById<Button>(R.id.tv2)

        btn?.setOnClickListener {
            startActivity(Intent(this,MainActivity2::class.java))
        }
        btn2?.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
            test1()
            }
        })

    }

    @JTryCatch
    fun test1(){
        val r =  5/ 0
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
