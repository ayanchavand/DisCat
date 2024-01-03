package com.example.app.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.app.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {


    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: YourPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        // Log message for debugging
        Log.d("LOG", "Hello")

        // Standard onCreate setup
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout: TabLayout = findViewById(R.id.tablayout)
        viewPager2 = findViewById(R.id.viewPager)
        adapter = YourPagerAdapter(this)

        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Home"
                1 -> "Favorites"
                else -> null
            }
            tab.icon = when (position){
                0 -> ContextCompat.getDrawable(this, R.drawable.home)
                1 -> ContextCompat.getDrawable(this, R.drawable.favorite)
                else -> null
            }
        }.attach()

    }
}

class YourPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2 // Number of Fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainFragment() // Replace with your actual HomeFragment
            1 -> FavoritesFragment() // Replace with your actual FavoritesFragment
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}

