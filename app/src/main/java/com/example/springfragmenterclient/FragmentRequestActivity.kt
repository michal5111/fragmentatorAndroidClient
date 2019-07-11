package com.example.springfragmenterclient

import android.content.BroadcastReceiver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.springfragmenterclient.Entities.Movie
import com.star_zero.sse.EventHandler
import com.star_zero.sse.EventSource
import com.star_zero.sse.MessageEvent
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
    private var message: String = ""
    private var percent: Double = 0.0
    private var to: Double = 0.0
    private lateinit var conversionProgressBar: ProgressBar

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
        conversionProgressBar = findViewById(R.id.ConversionprogressBar)
        conversionProgressBar.max = 99
        println(movie.subtitles.filename)
        eventSource = EventSource("http://michal5111.asuscomm.com:8080/rest/requestFragment?fileName=${encodeValue(movie.fileName)}&line=${encodeValue(movie.subtitles.filteredLines[0].textLines)}&timeString=${encodeValue(movie.subtitles.filteredLines[0].timeString)}&path=${encodeValue(movie.path)}&lineNumber=${movie.subtitles.filteredLines[0].number}", object : EventHandler {
            override fun onError(e: java.lang.Exception?) {
                println("error " + e.toString())
                textView.post { textView.text = "error " + e.toString() }
            }

            override fun onOpen() {
                println("opened")
                textView.post {
                    textView.text = "Opened"
                }
            }

            override fun onMessage(messageEvent: MessageEvent) {
                println("Message: "+ messageEvent.data)
                message = message.plus(messageEvent.data).plus("\n")
                if (messageEvent.event.equals("to")) {
                    to = messageEvent.data.toDouble()
                }
                if (messageEvent.event.equals("log")) {
                    if (messageEvent.data.contains("frame=")) {
                        val time = messageEvent.data.substring(messageEvent.data.lastIndexOf("time=")+5,messageEvent.data.lastIndexOf("time=")+16)
                        percent = Fragmentator4000.timeToSeconds(time)*100/to
                        conversionProgressBar.progress = percent.toInt()
                    }
                }
                textView.post {
                    textView.text = message
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
                }
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
