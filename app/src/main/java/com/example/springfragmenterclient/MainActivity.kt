package com.example.springfragmenterclient

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Entities.Line
import com.example.springfragmenterclient.Entities.Movie
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val applicationContext = applicationContext as Fragmentator4000
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.SearchButton)
        val frazeInput = findViewById<TextInputEditText>(R.id.FrazeInput)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar3)
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerView)
        val viewManager = LinearLayoutManager(this)
        recyclerView.layoutManager = viewManager


        searchButton.setOnClickListener {
            Fragmentator4000.hideKeyboard(this)
            progressBar.visibility = View.VISIBLE
            RequestQueueSingleton.getInstance(this)
                .addToRequestQueue(
                    applicationContext.getMoviesRequest(frazeInput.text.toString(),recyclerView, progressBar))
        }
    }

    fun selectMovie(movie: Movie, line: Line) {
        val intent = Intent(applicationContext,SelectedLineActivity::class.java).apply {
            putExtra("SELECTED_MOVIE",movie)
            putExtra("SELECTED_LINE",line)
        }
        startActivity(intent)
    }

}
