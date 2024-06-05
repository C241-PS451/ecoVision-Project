package com.example.ecovision

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ecovision.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        val homeFragment = HomeFragment()
        val articleFragment = ArticleFragment()
        val historyFragment = HistoryFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.miHome -> setCurrentFragment(homeFragment)
                R.id.miArticle -> setCurrentFragment(articleFragment)
                R.id.miHistory -> setCurrentFragment(historyFragment)
                R.id.miProfile -> setCurrentFragment(profileFragment)
            }
            true
        }

        binding.fabScan.setOnClickListener {
            // Handle the click event for the floating action button
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameFragment, fragment)
            commit()
        }
}