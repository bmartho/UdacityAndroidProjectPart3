package com.udacity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileNameValue = findViewById<TextView>(R.id.file_name_value)
        val statusValue = findViewById<TextView>(R.id.status_value)
        val button = findViewById<Button>(R.id.button_ok)

        fileNameValue.text = intent.getStringExtra(FILE_NAME_PARAMETER)
        statusValue.text = intent.getStringExtra(STATUS_PARAMETER)

        if (statusValue.text == getString(R.string.status_failed)) {
            statusValue.setTextColor(getColor(R.color.failStatus))
        }

        button.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            )

            finish()
        }
    }

    companion object {
        private const val FILE_NAME_PARAMETER = "file_name"
        private const val STATUS_PARAMETER = "status"

        fun createIntent(context: Context, fileName: String, status: String): Intent {
            return Intent(context, DetailActivity::class.java)
                .putExtra(FILE_NAME_PARAMETER, fileName)
                .putExtra(STATUS_PARAMETER, status)
        }
    }
}
