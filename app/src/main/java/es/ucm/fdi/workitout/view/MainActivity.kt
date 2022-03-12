package es.ucm.fdi.workitout.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.ucm.fdi.workitout.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)

        //setContentView(R.layout.activity_main)

    }
}