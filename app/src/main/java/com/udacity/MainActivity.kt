package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var fileName: String = ""
    private lateinit var notificationManager: NotificationManager
    private lateinit var download_radio_group: RadioGroup

    private lateinit var downloadManager: DownloadManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel()

        download_radio_group = findViewById(R.id.download_radio_group)
        custom_button.setOnClickListener {
            val url = when (download_radio_group.checkedRadioButtonId) {
                R.id.download_glide -> URL_GLIDE
                R.id.download_loadapp -> URL_LOADAPP
                R.id.download_retrofit -> URL_RETROFIT
                else -> ""
            }

            if (url.isEmpty()) {
                Toast.makeText(this, R.string.no_radio_button_selected, Toast.LENGTH_SHORT).show()
            } else {
                val radioButton =
                    findViewById<RadioButton>(download_radio_group.checkedRadioButtonId)
                fileName = radioButton.text.toString()

                custom_button.setState(ButtonState.Loading)
                download(url)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            custom_button.complete()

            val id = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val query = DownloadManager.Query()
                .setFilterById(id)
            val cursor = downloadManager.query(query)

            var status = getString(R.string.status_failed)
            if (cursor.moveToFirst()) {
                val statusCode = cursor.getInt(
                    cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                )

                if (statusCode == DownloadManager.STATUS_SUCCESSFUL) {
                    status = getString(R.string.status_success)
                }
            }

            val action = buildNotificationAction(status)
            val builder = NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_description))
                .addAction(action)

            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.main_channel),
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun buildNotificationAction(status: String): NotificationCompat.Action {
        val intent = DetailActivity.createIntent(applicationContext, fileName, status)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Action(0, getString(R.string.notification_button), pendingIntent)
    }

    companion object {
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_LOADAPP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"

        private const val CHANNEL_ID = "channelId"
        private const val NOTIFICATION_ID = 0
    }
}
