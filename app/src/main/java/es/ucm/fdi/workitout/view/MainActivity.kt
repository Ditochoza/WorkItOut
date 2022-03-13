package es.ucm.fdi.workitout.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()

        //setContentView(R.layout.activity_main)
    }
}