package com.example.springfragmenterclient

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.android.volley.toolbox.NetworkImageView
import com.example.springfragmenterclient.Entities.Line
import com.example.springfragmenterclient.Entities.Movie
import com.example.springfragmenterclient.Entities.Subtitles
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject

class SelectedLineActivity : AppCompatActivity() {

    private lateinit var imageView: NetworkImageView
    private lateinit var movieTitleTextView: TextView
    private lateinit var movieTimeTextView: TextView
    private lateinit var textView: TextView
    private lateinit var selectedMovie: Movie
    private lateinit var selectedLine: Line
    private lateinit var progressBar: ProgressBar
    private lateinit var downloadButton: Button
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_line)
        imageView = findViewById(R.id.SelectedLineImageView)
        movieTimeTextView = findViewById(R.id.SelectedLineTimeTextView)
        movieTitleTextView = findViewById(R.id.SelectedLineTitleTextView)
        textView = findViewById(R.id.SelectedLineTextView)
        progressBar = findViewById(R.id.SelectedLineProgressBar)
        selectedMovie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        selectedLine = intent.getSerializableExtra("SELECTED_LINE") as Line
        movieTitleTextView.text = selectedMovie.fileName
        movieTimeTextView.text = selectedLine.timeString
        textView.text = HtmlCompat.fromHtml(selectedLine.textLines, Html.FROM_HTML_MODE_LEGACY)
        downloadButton = findViewById(R.id.SelectedLineDownloadButton)
        downloadButton.setOnClickListener {
            val intent = Intent(applicationContext,FragmentRequestActivity::class.java).apply {
                putExtra("SELECTED_MOVIE",movie)
            }
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        movie = Movie()
        movie.fileName = selectedMovie.fileName
        movie.path = selectedMovie.path
        movie.subtitles = Subtitles()
        movie.subtitles.filename = selectedMovie.subtitles.filename
        movie.subtitles.filteredLines.add(selectedLine)
        val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        val movieJsonString = gson.toJson(movie)
        val jsonObject = JSONObject(movieJsonString)
        val snapshotRequest = (this.applicationContext as Fragmentator4000).getSnapshotRequest(
            jsonObject,
            imageView,
            RequestQueueSingleton.getInstance(this.applicationContext).imageLoader,
            progressBar
        )
        RequestQueueSingleton.getInstance(this.applicationContext).addToRequestQueue(snapshotRequest)
    }


}
