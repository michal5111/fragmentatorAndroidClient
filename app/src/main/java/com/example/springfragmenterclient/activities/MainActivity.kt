package com.example.springfragmenterclient.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.adapters.SectionsPagerAdapter
import com.example.springfragmenterclient.model.Line
import com.example.springfragmenterclient.model.Movie
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val selectionPagerAdapter =
            SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = selectionPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    fun selectLine(movie: Movie, line: Line) {
        val intent = Intent(this, SelectedLineActivity::class.java).apply {
            putExtra("SELECTED_MOVIE",movie)
            putExtra("SELECTED_LINE",line)
        }
        startActivity(intent)
    }

    fun selectMovie(movie: Movie) {
        val intent = Intent(this, SelectedMovieActivity::class.java).apply {
            putExtra("SELECTED_MOVIE",movie)
        }
        startActivity(intent)
    }
}
