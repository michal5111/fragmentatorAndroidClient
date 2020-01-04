package com.example.springfragmenterclient.activities

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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProviders
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.entities.FragmentRequest
import com.example.springfragmenterclient.entities.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import java.io.File


class FragmentRequestActivity : AppCompatActivity() {

    private lateinit var viewModel: FragmentRequestViewModel
    private lateinit var movie: Movie
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
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var mediaController: MediaController
    private val compositeDisposable = CompositeDisposable()

    private object RequestCodes {
        const val DOWNLOAD_PERMISSION_REQUEST = 0
        const val SHARE_PERMISSION_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FragmentRequestViewModel::class.java)
        setContentView(R.layout.activity_fragment_request)
        movie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        viewModel.fragmentRequest =
            intent.getSerializableExtra("FRAGMENT_REQUEST") as FragmentRequest
        textView = findViewById(R.id.event_text)
        openButton = findViewById(R.id.open_button)
        downloadButton = findViewById(R.id.download_button)
        convertButton = findViewById(R.id.convert_button)
        conversionProgressBar = findViewById(R.id.conversion_progress_bar)
        scrollView = findViewById(R.id.log_scroll_view)
        startOffsetEditText = findViewById(R.id.startOffsetEditText)
        stopOffsetEditText = findViewById(R.id.stopOffsetEditText)
        shareButton = findViewById(R.id.share_button)
        videoView = findViewById(R.id.video_view)
        mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        videoView.setOnPreparedListener {
            mediaController.setAnchorView(videoView)
            progressBar.visibility = View.INVISIBLE
            videoView.start()
        }
        progressBar = findViewById(R.id.progressBar)
        startOffsetEditText.addTextChangedListener(startOffsetTextWatcher)
        stopOffsetEditText.addTextChangedListener(stopOffsetTextWatcher)
        convertButton.setOnClickListener {
            compositeDisposable += viewModel.saveFragmentRequest(viewModel.fragmentRequest)
                .toObservable().flatMap { afterPostObservable(it) }
                .doOnSubscribe {
                    conversionProgressBar.progress = 0
                    convertButton.isEnabled = false
                }
                .subscribeBy(
                    onError = {
                        textView.post {
                            (application as Fragmentator4000).errorHandler(it)
                        }
                    }
                )

        }
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun shareFile(uri: Uri) {
        val dir = File(getExternalFilesDir(null), "cache")
        val videoFile = File(dir, uri.lastPathSegment!!).apply { deleteOnExit() }
        val shareFileUri =
            FileProvider.getUriForFile(
                this,
                "com.example.springfragmenterclient.fileprovider",
                videoFile
            )
        val shareVideoIntent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(shareFileUri, contentResolver.getType(shareFileUri))
            putExtra(Intent.EXTRA_STREAM, shareFileUri)
        }
        startActivity(
            Intent.createChooser(
                shareVideoIntent,
                resources.getString(R.string.shareFragment)
            )
        )
    }

    private val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Toast.makeText(p0, resources.getString(R.string.downloadComplete), Toast.LENGTH_SHORT)
                .show()
            if (viewModel.lastShare != -1L) {
                val c: Cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(viewModel.lastShare))
                if (c.moveToFirst()) {
                    val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val movieURI =
                            Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                        viewModel.lastShare = -1L
                        shareFile(movieURI)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
        compositeDisposable.clear()
    }

    private fun downloadManagerEnqueueForSharing(fileName: String) = downloadManager.enqueue(
        DownloadManager.Request(("${Fragmentator4000.fragmentsUrl}/$fileName").toUri())
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle("Fragment: " + movie.fileName + viewModel.fragmentRequest.startLineId)
            .setDescription(movie.fileName)
            .setDestinationInExternalFilesDir(
                this@FragmentRequestActivity,
                "cache",
                fileName
            )
    )

    private fun downloadManagerEnqueueForDownload(fileName: String) = downloadManager.enqueue(
        DownloadManager.Request(("${Fragmentator4000.fragmentsUrl}/$fileName").toUri())
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle("Fragment: " + movie.fileName + viewModel.fragmentRequest.startLineId)
            .setDescription(movie.fileName)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
    )

    private fun openButtonOnClickListener(fileName: String) {
        val openVideo = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("${Fragmentator4000.fragmentsUrl}/$fileName")
        )
        startActivity(openVideo)
    }

    private fun downloadButtonOnClickListener(fileName: String) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@FragmentRequestActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RequestCodes.DOWNLOAD_PERMISSION_REQUEST
            )
        } else {
            viewModel.lastDownload = downloadManagerEnqueueForDownload(fileName)
        }
    }

    private fun shareButtonOnClickListener(fileName: String) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@FragmentRequestActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RequestCodes.SHARE_PERMISSION_REQUEST
            )
        } else {
            viewModel.lastShare = downloadManagerEnqueueForSharing(fileName)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestCodes.DOWNLOAD_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadManagerEnqueueForDownload(viewModel.fileName)
                }
            }
            RequestCodes.SHARE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadManagerEnqueueForSharing(viewModel.fileName)
                }
            }
        }
    }

    private val stopOffsetTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            try {
                viewModel.fragmentRequest.stopOffset = p0.toString().toDouble()
            } catch (e: Exception) {
                viewModel.fragmentRequest.stopOffset = 0.0
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            openButton.isEnabled = false
            downloadButton.isEnabled = false
            shareButton.isEnabled = false
        }
    }

    private val startOffsetTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            try {
                viewModel.fragmentRequest.startOffset = p0.toString().toDouble()
            } catch (e: Exception) {
                viewModel.fragmentRequest.startOffset = 0.0
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            openButton.isEnabled = false
            downloadButton.isEnabled = false
            shareButton.isEnabled = false
        }
    }

    private fun afterPostObservable(fragmentRequest: FragmentRequest) =
        viewModel.requestFragment(fragmentRequest.id!!)
            .doOnNext { conversionStatus ->
                if (conversionStatus.eventType == "log") {
                    if (conversionStatus.logLine!!.contains("frame=")) {
                        conversionProgressBar.progress = viewModel.percent.toInt()
                    }
                    textView.post { textView.text = viewModel.message }
                    scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                } else if (conversionStatus.eventType == "complete") {
                    conversionProgressBar.progress = 100
                    openButton.post {
                        openButton.setOnClickListener { openButtonOnClickListener(viewModel.fileName) }
                        downloadButton.setOnClickListener { downloadButtonOnClickListener(viewModel.fileName) }
                        shareButton.setOnClickListener { shareButtonOnClickListener(viewModel.fileName) }
                        convertButton.isEnabled = true
                        openButton.isEnabled = true
                        downloadButton.isEnabled = true
                        shareButton.isEnabled = true
                        videoView.setVideoURI(("${Fragmentator4000.fragmentsUrl}/${viewModel.fileName}").toUri())
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
            .doOnSubscribe {
                viewModel.message = ""
                textView.post { textView.text = viewModel.message }
            }
}
