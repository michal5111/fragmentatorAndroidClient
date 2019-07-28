package com.example.springfragmenterclient

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.springfragmenterclient.Entities.Movie
import com.example.springfragmenterclient.Fragmentator4000.Companion.encodeValue
import com.star_zero.sse.EventHandler
import com.star_zero.sse.EventSource
import com.star_zero.sse.MessageEvent
import java.io.File


class FragmentRequestActivity : AppCompatActivity() {

    private lateinit var movie: Movie
    //private lateinit var line: Line
    private lateinit var eventSource: EventSource
    private lateinit var textView: TextView
    private lateinit var openButton: Button
    private lateinit var convertButton: Button
    private lateinit var downloadButton: Button
    private lateinit var shareButton: Button
    private lateinit var startOffsetEditText: EditText
    private lateinit var stopOffsetEditText: EditText
    private lateinit var conversionProgressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var downloadManager: DownloadManager
    private var message: String = ""
    private var percent: Double = 0.0
    private var to: Double = 0.0
    private var lastDownload: Long = -1L
    private var lastShare: Long = -1L
    private lateinit var fileName: String
    private lateinit var endpoint: String

    private fun createEventSource(): EventSource {
        var adress: String = "$endpoint?fileName=${encodeValue(movie.fileName)}&path=${encodeValue(movie.path)}"
        for (line in movie.subtitles.filteredLines) {
            adress = adress.plus(
                "&line=${encodeValue(line.textLines)}" +
                        "&timeString=${encodeValue(line.timeString)}" +
                        "&lineNumber=${line.number}"
            )
        }
        adress = adress.plus( "&startOffset=${movie.startOffset}" +
                "&stopOffset=${movie.stopOffset}" +
                "&subtitlesFileName=${encodeValue(movie.subtitles.filename)}")
        println(adress)
        return EventSource(
            adress
            , object : EventHandler {
                override fun onError(e: java.lang.Exception?) {
                    eventSource.close()
                    textView.post { textView.text = "error " + e.toString() }
                }

                override fun onOpen() {
                    message = ""
                    textView.post {
                        textView.text = message
                    }
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
                            val offset = messageEvent.data.lastIndexOf("time=")
                            val time = messageEvent.data.substring(offset + 5, offset + 16)
                            percent = Fragmentator4000.timeToSeconds(time) * 100.0 / to
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
                        fileName = messageEvent.data
                        eventSource.close()
                        conversionProgressBar.progress = 100
                        openButton.post {
                            openButton.setOnClickListener {
                                val openVideo = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("${Fragmentator4000.fragmentsUrl}/$fileName")
                                )
                                startActivity(openVideo)
                            }
                            downloadButton.setOnClickListener {
                                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(
                                        this@FragmentRequestActivity,
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        1
                                    )
                                } else {
                                    lastDownload = downloadManager.enqueue(
                                        DownloadManager.Request(("${Fragmentator4000.fragmentsUrl}/$fileName").toUri())
                                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                                            .setAllowedOverRoaming(false)
                                            .setTitle("Fragment: " + movie.subtitles.filteredLines[0].textLines)
                                            .setDescription(movie.fileName)
                                            .setDestinationInExternalPublicDir(
                                                Environment.DIRECTORY_DOWNLOADS,
                                                fileName
                                            )
                                    )
                                }
                            }
                            shareButton.setOnClickListener {
                                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(
                                        this@FragmentRequestActivity,
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        1
                                    )
                                } else {
                                    lastShare = downloadManager.enqueue(
                                        DownloadManager.Request(("${Fragmentator4000.fragmentsUrl}/$fileName").toUri())
                                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                                            .setAllowedOverRoaming(false)
                                            .setTitle("Fragment: " + movie.subtitles.filteredLines[0].textLines)
                                            .setDescription(movie.fileName)
                                            .setDestinationInExternalFilesDir(
                                                this@FragmentRequestActivity,
                                                "cache",
                                                fileName
                                            )
                                    )
                                }
                            }
                            convertButton.isEnabled = true
                            openButton.isEnabled = true
                            downloadButton.isEnabled = true
                            shareButton.isEnabled = true
                        }
                    }
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_request)
        movie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        endpoint = intent.getSerializableExtra("ENDPOINT") as String
        //line = movie.subtitles.filteredLines[0]
        textView = findViewById(R.id.EventTextView)
        openButton = findViewById(R.id.OpenButton)
        downloadButton = findViewById(R.id.DownloadButton)
        convertButton = findViewById(R.id.ConvertButton)
        conversionProgressBar = findViewById(R.id.ConversionprogressBar)
        scrollView = findViewById(R.id.scrollView2)
        startOffsetEditText = findViewById(R.id.startOffsetEditText)
        stopOffsetEditText = findViewById(R.id.stopOffsetEditText)
        shareButton = findViewById(R.id.ShareButton)
        startOffsetEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    movie.startOffset = p0.toString().toDouble()
                } catch (e: Exception) {
                    movie.startOffset = 0.0
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                openButton.isEnabled = false
                downloadButton.isEnabled = false
                shareButton.isEnabled = false
            }
        })
        stopOffsetEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    movie.stopOffset = p0.toString().toDouble()
                } catch (e: Exception) {
                    movie.stopOffset = 0.0
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                openButton.isEnabled = false
                downloadButton.isEnabled = false
                shareButton.isEnabled = false
            }
        })
        eventSource = createEventSource()
        convertButton.setOnClickListener {
            conversionProgressBar.progress = 0
            convertButton.isEnabled = false
            eventSource = createEventSource()
            eventSource.connect()
        }
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun shareFile(uri: Uri) {
        val dir = File(getExternalFilesDir(null), "cache")
        val videoFile = File(dir, uri.lastPathSegment).apply { deleteOnExit() }
        val shareFileUri =
            FileProvider.getUriForFile(this, "com.example.springfragmenterclient.fileprovider", videoFile)
        val shareVideoIntent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(shareFileUri, contentResolver.getType(shareFileUri))
            putExtra(Intent.EXTRA_STREAM, shareFileUri)
        }
        startActivity(Intent.createChooser(shareVideoIntent, resources.getString(R.string.shareFragment)))
    }

    private val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Toast.makeText(p0, resources.getString(R.string.downloadComplete), Toast.LENGTH_SHORT).show()
            if (lastShare != -1L) {
                val c: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(lastShare))
                if (c.moveToFirst()) {
                    val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val movieURI = Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                        lastShare = -1L
                        shareFile(movieURI)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
        eventSource.close()
    }
}
