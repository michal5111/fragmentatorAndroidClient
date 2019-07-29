package com.example.springfragmenterclient.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.springfragmenterclient.*
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson

class SearchFraze : Fragment() {

    companion object {
        fun newInstance() = SearchFraze()
    }

    private lateinit var viewModel: SearchFrazeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.search_fraze_fragment, container, false)
        val searchButton = root.findViewById<Button>(R.id.SearchButton)
        val frazeInput = root.findViewById<TextInputEditText>(R.id.FrazeInput)
        progressBar = root.findViewById(R.id.progressBar3)
        recyclerView = root.findViewById(R.id.RecyclerView)
        val viewManager = LinearLayoutManager(context)
        recyclerView.layoutManager = viewManager


        searchButton.setOnClickListener {
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            progressBar.visibility = View.VISIBLE
            RequestQueueSingleton.getInstance(context!!)
                .addToRequestQueue(
                        getMoviesByFrazeRequest(Fragmentator4000.encodeValue(frazeInput.text.toString())))
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchFrazeViewModel::class.java)
            .apply { applicationContext = activity?.applicationContext as Fragmentator4000 }
    }

    private fun getMoviesByFrazeRequest(fraze: String): JsonArrayRequest {
        return JsonArrayRequest(
            Request.Method.GET, "${Fragmentator4000.apiUrl}/searchFraze?fraze=$fraze", null,
            Response.Listener { response ->
                val gson = Gson()
                viewModel.movies = gson.fromJson(response.toString(), Fragmentator4000.movieListType)
                recyclerView.adapter = MovieWithLinesRecyclerViewAdapter(viewModel.movies)
                progressBar.visibility = View.INVISIBLE
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }
    }
}