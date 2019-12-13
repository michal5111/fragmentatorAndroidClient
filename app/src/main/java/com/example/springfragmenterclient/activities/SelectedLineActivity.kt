package com.example.springfragmenterclient.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NetworkImageView
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.adapters.LineEditViewAdapter
import com.example.springfragmenterclient.entities.*
import com.example.springfragmenterclient.utils.RequestQueueSingleton
import com.google.gson.Gson
import java.util.*

class SelectedLineActivity : AppCompatActivity() {

    private lateinit var imageView: NetworkImageView
    private lateinit var movieTitleTextView: TextView
    private lateinit var movieTimeTextView: TextView
    private lateinit var textView: TextView
    private lateinit var selectedMovie: Movie
    private lateinit var selectedLine: Line
    private lateinit var progressBar: ProgressBar
    private lateinit var downloadButton: Button
    private lateinit var lineEditRecyclerView: RecyclerView
    private var fragmentRequest: FragmentRequest = FragmentRequest()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_line)
        imageView = findViewById(R.id.SelectedLineImageView)
        movieTimeTextView = findViewById(R.id.SelectedLineTimeTextView)
        movieTitleTextView = findViewById(R.id.SelectedLineTitleTextView)
        textView = findViewById(R.id.SelectedLineTextView)
        progressBar = findViewById(R.id.SelectedLineProgressBar)
        lineEditRecyclerView = findViewById(R.id.LineEditRecyclerView)
        selectedMovie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        selectedLine = intent.getSerializableExtra("SELECTED_LINE") as Line
        fragmentRequest.apply {
            movieId = selectedMovie.id
            startLineId = selectedLine.id
            stopLineId = selectedLine.id
        }
        movieTitleTextView.text = selectedMovie.fileName
        movieTimeTextView.text = selectedLine.timeString
        textView.text = HtmlCompat.fromHtml(selectedLine.textLines, Html.FROM_HTML_MODE_LEGACY)
        downloadButton = findViewById(R.id.SelectedLineDownloadButton)
        downloadButton.setOnClickListener {
            val intent = Intent(applicationContext, FragmentRequestActivity::class.java).apply {
                putExtra("SELECTED_MOVIE", selectedMovie)
                putExtra("FRAGMENT_REQUEST", fragmentRequest)
                putExtra("EDITS_LIST", getLineEdits())
            }
            startActivity(intent)
        }
        lineEditRecyclerView.layoutManager = LinearLayoutManager(this)
        lineEditRecyclerView.adapter = LineEditViewAdapter(listOf(selectedLine))
        val dialogButton: Button = findViewById(R.id.Dialog)
        dialogButton.setOnClickListener {
            val intent = Intent(applicationContext, SelectedMovieActivity::class.java).apply {
                putExtra("SELECTED_MOVIE", selectedMovie)
                putExtra("POSITION", selectedLine.number - 1)
            }
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val snapshotRequest = getSnapshotRequest(
            RequestQueueSingleton.getInstance(this).imageLoader
        )
        RequestQueueSingleton.getInstance(this).addToRequestQueue(snapshotRequest)
    }

    private fun getSnapshotRequest(
        imageLoader: ImageLoader
    ) = JsonObjectRequest(
        Request.Method.GET, "${Fragmentator4000.apiUrl}/lineSnapshot?lineId=${selectedLine.id}", null,
        com.android.volley.Response.Listener { response ->
            val gson = Gson()
            val json =
                gson.fromJson(response.toString(), Response::class.java)
            imageView.setImageUrl(json.url, imageLoader)
            progressBar.visibility = View.INVISIBLE
        },
        com.android.volley.Response.ErrorListener { error ->
            progressBar.visibility = View.INVISIBLE
            Toast.makeText(applicationContext, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    ).apply {
        retryPolicy = DefaultRetryPolicy(
            1000,
            20,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    private fun getLineEdits(): ArrayList<LineEdit> {
        val editsList = ArrayList<LineEdit>()
        for (i in 0 until lineEditRecyclerView.childCount) {
            val holder: LineEditViewAdapter.ViewHolder = lineEditRecyclerView
                .getChildViewHolder(lineEditRecyclerView.getChildAt(i)) as LineEditViewAdapter.ViewHolder
            if (holder.edited) {
                editsList.add(
                    LineEdit(
                        null,
                        fragmentRequest.id,
                        selectedLine.id,
                        holder.lineTextEdit.text.toString()
                    )
                )
            }
        }
        return editsList
    }
}
