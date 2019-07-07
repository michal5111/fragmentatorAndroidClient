package com.example.springfragmenterclient

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.springfragmenterclient.Entities.Movie
import com.star_zero.sse.EventHandler
import com.star_zero.sse.EventSource
import com.star_zero.sse.MessageEvent
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets






class FragmentRequestActivity : AppCompatActivity() {

    lateinit var eventSource: EventSource
    lateinit var movie: Movie
    lateinit var textView: TextView
    lateinit var openButton: Button
    lateinit var convertButton: Button
    lateinit var downloadButton: Button
    private lateinit var onDownloadComplete: BroadcastReceiver

    private fun encodeValue(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_request)
        movie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        textView = findViewById(R.id.EventTextView)
        openButton = findViewById(R.id.OpenButton)
        downloadButton = findViewById(R.id.DownloadButton)
        convertButton = findViewById(R.id.ConvertButton)
        eventSource = EventSource("http://michal5111.asuscomm.com:8080/rest/requestFragment?fileName=${encodeValue(movie.fileName)}&line=${encodeValue(movie.subtitles.filteredLines[0].textLines)}&timeString=${encodeValue(movie.subtitles.filteredLines[0].timeString)}&path=${encodeValue(movie.path)}", object : EventHandler {
            override fun onError(e: java.lang.Exception?) {
                println("error " + e.toString())
                textView.post { textView.text = "error " + e.toString() }
                //textView.text = e.toString()
            }

            override fun onOpen() {
                println("opened")
                textView.post {
                    textView.text = "Opened"
                }
                //textView.text = "opened"
                // run on worker thread
                //Log.d(FragmentActivity.TAG, "Open")
            }

            override fun onMessage(messageEvent: MessageEvent) {
                println("Message: "+ messageEvent.data)
                textView.post {
                    textView.text = "Message: "+ messageEvent.data
                }
                if (messageEvent.event.equals("complete")) {
                    eventSource.close()
                    openButton.post {
                        openButton.visibility = View.VISIBLE
                        openButton.setOnClickListener {
                            val openVideo = Intent(Intent.ACTION_VIEW, Uri.parse("http://michal5111.asuscomm.com:8080/fragments/"+messageEvent.data))
                            startActivity(openVideo)
                        }
                    }
                    downloadButton.post {
                        downloadButton.visibility = View.VISIBLE
                        downloadButton.setOnClickListener {
                            val file = File(Environment.DIRECTORY_MOVIES,messageEvent.data)
                            val request =
                                DownloadManager.Request(Uri.parse("http://michal5111.asuscomm.com:8080/fragments/"+messageEvent.data))
                                    .setTitle("Fragment")// Title of the Download Notification
                                    .setDescription("Pobieranie...")// Description of the Download Notification
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                                    .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                                    .setRequiresCharging(false)// Set if charging is required to begin the download
                                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                                    .setAllowedOverRoaming(true)// Set if download is allowed on roaming network
                            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            val downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
                            onDownloadComplete = object : BroadcastReceiver() {
                                override fun onReceive(context: Context, intent: Intent) {
                                    //Fetching the download id received with the broadcast
                                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                                    //Checking if the received broadcast is for our enqueued download by matching download id
                                    if (downloadID == id) {

                                    }
                                }
                            }
                        }
                    }
                }
                //textView.text = "Message: ${messageEvent.data}"
                // run on worker thread
                //Log.d(FragmentActivity.TAG, "Message")
            }
        })
        convertButton.setOnClickListener {
            eventSource.connect()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        eventSource.close()
    }
}
