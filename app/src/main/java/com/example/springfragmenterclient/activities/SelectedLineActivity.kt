package com.example.springfragmenterclient.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.adapters.LineEditViewAdapter
import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.entities.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class SelectedLineActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var movieTitleTextView: TextView
    private lateinit var movieTimeTextView: TextView
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var downloadButton: Button
    private lateinit var lineEditRecyclerView: RecyclerView
    private lateinit var viewModel: SelectedLineViewModel
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_line)
        viewModel = ViewModelProviders.of(this)[SelectedLineViewModel::class.java]
        imageView = findViewById(R.id.SelectedLineImageView)
        movieTimeTextView = findViewById(R.id.SelectedLineTimeTextView)
        movieTitleTextView = findViewById(R.id.SelectedLineTitleTextView)
        textView = findViewById(R.id.SelectedLineTextView)
        progressBar = findViewById(R.id.SelectedLineProgressBar)
        lineEditRecyclerView = findViewById(R.id.LineEditRecyclerView)
        viewModel.selectedMovie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        viewModel.selectedLine = intent.getSerializableExtra("SELECTED_LINE") as Line
        viewModel.fragmentRequest.apply {
            movieId = viewModel.selectedMovie.id
            startLineId = viewModel.selectedLine.id
            stopLineId = viewModel.selectedLine.id
        }
        movieTitleTextView.text = viewModel.selectedMovie.fileName
        movieTimeTextView.text = viewModel.selectedLine.timeString
        textView.text = HtmlCompat.fromHtml(viewModel.selectedLine.textLines, Html.FROM_HTML_MODE_LEGACY)
        downloadButton = findViewById(R.id.SelectedLineDownloadButton)
        downloadButton.setOnClickListener {
            viewModel.setLineEdits(lineEditRecyclerView)
            val intent = Intent(applicationContext, FragmentRequestActivity::class.java).apply {
                putExtra("SELECTED_MOVIE", viewModel.selectedMovie)
                putExtra("FRAGMENT_REQUEST", viewModel.fragmentRequest)
            }
            startActivity(intent)
        }
        lineEditRecyclerView.layoutManager = LinearLayoutManager(this)
        lineEditRecyclerView.adapter = LineEditViewAdapter(listOf(viewModel.selectedLine))
        val dialogButton: Button = findViewById(R.id.Dialog)
        dialogButton.setOnClickListener {
            val intent = Intent(applicationContext, SelectedMovieActivity::class.java).apply {
                putExtra("SELECTED_MOVIE", viewModel.selectedMovie)
                putExtra("POSITION", viewModel.selectedLine.number - 1)
            }
            startActivity(intent)
        }
    }

    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()
        compositeDisposable.add(
            viewModel.getLineSnapshot(viewModel.selectedLine.id!!)
                .doOnSubscribe { progressBar.visibility = View.VISIBLE }
                .doFinally { progressBar.visibility = View.INVISIBLE }
                .subscribeBy(
                    onSuccess = {
                        imageView.load(it)
                    },
                    onError = {
                        Toast.makeText(applicationContext, "error " + it.message, Toast.LENGTH_LONG).show()
                    }
                )
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
