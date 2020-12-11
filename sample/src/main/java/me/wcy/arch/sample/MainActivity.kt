package me.wcy.arch.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.wcy.arch.R
import me.wcy.arch.module.Arch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Arch.init(applicationContext)
    }
}