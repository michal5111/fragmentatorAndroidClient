package com.example.springfragmenterclient.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.springfragmenterclient.R

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val TAB_TITLES = arrayOf(
        R.string.tab1Name,
        R.string.tab2Name
    )

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return SearchFraze.newInstance()
            1 -> return SearchMovie.newInstance()
        }
        throw IllegalArgumentException("Invalid position")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }
}