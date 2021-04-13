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
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.model.FragmentRequest
import com.example.springfragmenterclient.model.Movie
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import javax.inject.Inject

class FragmentRequestActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: FragmentRequestViewModel
    private lateinit var textView: TextView
    private lateinit var openButton: Button
    private lateinit var convertButton: Button
    private lateinit var downloadButton: Button
    private lateinit var shareButton: Button
    private lateinit var startOffsetEditText: EditText
    private lateinit var stopOffsetEditText: EditText
    private lateinit var conversionProgressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var mediaController: MediaController
    private val messageObserver = Observer<String> {
        textView.text = it
        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
    }
    private val percentageObserver = Observer<Int> {
        conversionProgressBar.progress = it
    }

    private object RequestCodes {
        const val DOWNLOAD_PERMISSION_REQUEST = 0
        const val SHARE_PERMISSION_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[FragmentRequestViewModel::class.java]
        setContentView(R.layout.activity_fragment_request)
        viewModel.movie =
            intent.getSerializableExtra("com.example.springfragmenterclient.SELECTED_MOVIE") as Movie
        viewModel.fragmentRequest =
            intent.getSerializableExtra("com.example.springfragmenterclient.FRAGMENT_REQUEST") as FragmentRequest
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
        }
        progressBar = findViewById(R.id.progressBar)
        startOffsetEditText.addTextChangedListener(startOffsetTextWatcher)
        stopOffsetEditText.addTextChangedListener(stopOffsetTextWatcher)
        convertButton.setOnClickListener {
            viewModel.compositeDisposable += viewModel.saveFragmentRequest(viewModel.fragmentRequest)
                .toObservable()
                .flatMap(this::afterPostObservable)
                .doOnSubscribe {
                    conversionProgressBar.progress = 0
                    convertButton.isEnabled = false
                }
                .doOnError {
                    convertButton.isEnabled = true
                }
                .subscribeBy(
                    onError = (application as Fragmentator4000)::errorHandler
                )

        }
        viewModel.downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        viewModel.messageLiveData.observe(this, messageObserver)
        viewModel.percentLiveData.observe(this, percentageObserver)
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
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(
                context,
                resources.getString(R.string.downloadComplete),
                Toast.LENGTH_SHORT
            ).show()
            if (viewModel.lastShare == -1L) return
            val c: Cursor = viewModel
                .downloadManager
                .query(DownloadManager.Query().setFilterById(viewModel.lastShare))
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

    override fun onDestroy() {
        unregisterReceiver(onComplete)
        super.onDestroy()
    }

    private fun openButtonOnClick(fileName: String) {
        val openVideo = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("${Fragmentator4000.fragmentsUrl}/$fileName")
        )
        startActivity(openVideo)
    }

    private fun downloadButtonOnClick(fileName: String) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RequestCodes.DOWNLOAD_PERMISSION_REQUEST
            )
        } else {
            viewModel.lastDownload = viewModel.downloadManagerEnqueueForDownload(fileName)
        }
    }

    private fun shareButtonOnClick(fileName: String) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@FragmentRequestActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RequestCodes.SHARE_PERMISSION_REQUEST
            )
        } else {
            viewModel.lastShare = viewModel.downloadManagerEnqueueForSharing(fileName, this)
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
                    viewModel.downloadManagerEnqueueForDownload(viewModel.fileName)
                }
            }
            RequestCodes.SHARE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.downloadManagerEnqueueForSharing(viewModel.fileName, this)
                }
            }
        }
    }

    private val stopOffsetTextWatcher = object : TextWatcher {
        override fun afterTextChanged(ed: Editable?) {
            try {
                viewModel.fragmentRequest.stopOffset = ed.toString().toDouble()
            } catch (e: Exception) {
                viewModel.fragmentRequest.stopOffset = 0.0
            }
        }

        override fun beforeTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
            openButton.isEnabled = false
            downloadButton.isEnabled = false
            shareButton.isEnabled = false
            convertButton.isEnabled = true
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
            convertButton.isEnabled = true
        }
    }

    private fun afterPostObservable(fragmentRequest: FragmentRequest) =
        viewModel.requestFragment(fragmentRequest.id!!)
            .doOnComplete {
                openButton.setOnClickListener { openButtonOnClick(viewModel.fileName) }
                downloadButton.setOnClickListener { downloadButtonOnClick(viewModel.fileName) }
                shareButton.setOnClickListener { shareButtonOnClick(viewModel.fileName) }
                openButton.isEnabled = true
                downloadButton.isEnabled = true
                shareButton.isEnabled = true
                progressBar.visibility = View.VISIBLE
                videoView.setVideoURI(("${Fragmentator4000.fragmentsUrl}/${viewModel.fileName}").toUri())
                videoView.start()
                mediaController.hide()
            }
}
