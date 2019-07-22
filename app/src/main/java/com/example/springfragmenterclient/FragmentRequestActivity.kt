package com.example.springfragmenterclient

import android.content.BroadcastReceiver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.springfragmenterclient.Entities.Line
import com.example.springfragmenterclient.Entities.Movie
import com.star_zero.sse.EventHandler
import com.star_zero.sse.EventSource
import com.star_zero.sse.MessageEvent
import java.net.URLEncoder
import java.nio.charset.StandardCharsets






class FragmentRequestActivity : AppCompatActivity() {

    private lateinit var eventSource: EventSource
    private lateinit var movie: Movie
    private lateinit var textView: TextView
    private lateinit var openButton: Button
    private lateinit var convertButton: Button
    private lateinit var downloadButton: Button
    private lateinit var startOffsetEditText: EditText
    private lateinit var stopOffsetEditText: EditText
    private lateinit var line: Line
    private lateinit var onDownloadComplete: BroadcastReceiver
    private var message: String = ""
    private var percent: Double = 0.0
    private var to: Double = 0.0
    private lateinit var conversionProgressBar: ProgressBar
    private val apiURL = "http://michal5111.asuscomm.com:8080/rest"
    private lateinit var scrollView: ScrollView

    private fun encodeValue(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    }

    private fun createEventSource() : EventSource {
        return EventSource("$apiURL/requestFragment" +
                "?fileName=${encodeValue(movie.fileName)}" +
                "&line=${encodeValue(line.textLines)}" +
                "&timeString=${encodeValue(line.timeString)}" +
                "&path=${encodeValue(movie.path)}" +
                "&lineNumber=${line.number}" +
                "&startOffset=${line.startOffset}" +
                "&stopOffset=${line.stopOffset}" +
                "&subtitlesFileName=${movie.subtitles.filename}"
            , object : EventHandler {
                override fun onError(e: java.lang.Exception?) {
                    eventSource.close()
                    textView.post { textView.text = "error " + e.toString() }
                }

                override fun onOpen() {
                }

                override fun onMessage(messageEvent: MessageEvent) {
                    if (messageEvent.event.isNullOrBlank() || messageEvent.data.isBlank()) {
                        return
                    }
                    message = message.plus(messageEvent.data).plus("\n")
                    if (messageEvent.event.equals("to")) {
                        to = messageEvent.data.toDouble()
                    }
                    if (messageEvent.event.equals("log")) {
                        if (messageEvent.data.contains("frame=")) {
                            val time = messageEvent.data.substring(messageEvent.data.lastIndexOf("time=")+5,messageEvent.data.lastIndexOf("time=")+16)
                            percent = Fragmentator4000.timeToSeconds(time)*100.0/to
                            conversionProgressBar.progress = percent.toInt()
                        }
                        textView.post {
                            textView.text = message
                        }
                        scrollView.post {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        }
                    }
                    if (messageEvent.event.equals("complete")) {
                        conversionProgressBar.progress = 100
                        eventSource.close()
                        openButton.post {
                            openButton.setOnClickListener {
                                val openVideo = Intent(Intent.ACTION_VIEW, Uri.parse("http://michal5111.asuscomm.com:8080/fragments/"+messageEvent.data))
                                startActivity(openVideo)
                            }
                            convertButton.isEnabled = true
                            openButton.isEnabled = true
                        }
                    }
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_request)
        movie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        line = movie.subtitles.filteredLines[0]
        textView = findViewById(R.id.EventTextView)
        openButton = findViewById(R.id.OpenButton)
        downloadButton = findViewById(R.id.DownloadButton)
        convertButton = findViewById(R.id.ConvertButton)
        conversionProgressBar = findViewById(R.id.ConversionprogressBar)
        scrollView = findViewById(R.id.scrollView2)
        startOffsetEditText = findViewById(R.id.startOffsetEditText)
        stopOffsetEditText = findViewById(R.id.stopOffsetEditText)
        startOffsetEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    line.startOffset = p0.toString().toDouble()
                } catch (e: Exception) {
                    line.startOffset = 0.0
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                openButton.isEnabled = false
            }
        })
        stopOffsetEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    line.stopOffset = p0.toString().toDouble()
                } catch (e: Exception) {
                    line.stopOffset = 0.0
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                openButton.isEnabled = false
            }
        })
        eventSource = createEventSource()
        convertButton.setOnClickListener {
            conversionProgressBar.progress = 0
            convertButton.isEnabled = false
            eventSource = createEventSource()
            eventSource.connect()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        eventSource.close()
    }
}
