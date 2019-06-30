package com.example.springfragmenterclient

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.NetworkImageView
import com.example.springfragmenterclient.Entities.Line
import com.example.springfragmenterclient.Entities.Movie
import com.example.springfragmenterclient.Entities.SubtitlesFile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject

class SelectedLineActivity : AppCompatActivity() {

    lateinit var imageView: NetworkImageView
    lateinit var selectedMovieTitleTextView: TextView
    lateinit var selectedMovieTimeTextView: TextView
    lateinit var selectedLineTextView: TextView
    lateinit var selectedMovie: Movie
    lateinit var selectedLine: Line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_line)
        imageView = findViewById(R.id.SelectedLineImageView)
        selectedMovieTimeTextView = findViewById(R.id.SelectedLineTimeTextView)
        selectedMovieTitleTextView = findViewById(R.id.SelectedLineTitleTextView)
        selectedLineTextView = findViewById(R.id.SelectedLineTextView)
        selectedMovie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        selectedLine = intent.getSerializableExtra("SELECTED_LINE") as Line
        selectedMovieTitleTextView.text = selectedMovie.fileName
        selectedMovieTimeTextView.text = selectedLine.timeString
        selectedLineTextView.text = selectedLine.textLines
    }

    override fun onStart() {
        super.onStart()
//        RequestQueueSingleton.getInstance(this.applicationContext).imageLoader
//            .get("$url/${selectedMovie.fileName}${selectedLine.number}",
//                ImageLoader.getImageListener(imageView,R.drawable.ic_launcher_background,
//                    R.drawable.ic_launcher_foreground))
        val movie: Movie = Movie()
        movie.fileName = selectedMovie.fileName
        movie.path = selectedMovie.path
        movie.subtitles = SubtitlesFile()
        movie.subtitles.filteredLines.add(selectedLine)
        val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        val movieJsonString = gson.toJson(movie)
        val jsonObject = JSONObject(movieJsonString)
        var snapshotRequest = (this.applicationContext as Fragmentator4000).getSnapshotRequest(
            jsonObject,
            imageView,
            RequestQueueSingleton.getInstance(this.applicationContext).imageLoader
        )
        RequestQueueSingleton.getInstance(this.applicationContext).addToRequestQueue(snapshotRequest)
    }


}
