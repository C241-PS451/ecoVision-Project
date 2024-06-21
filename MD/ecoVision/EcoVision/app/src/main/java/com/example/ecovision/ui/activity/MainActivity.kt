package com.example.ecovision.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ecovision.R
import com.example.ecovision.databinding.ActivityMainBinding
import com.example.ecovision.ui.fragment.ArticleFragment
import com.example.ecovision.ui.fragment.HistoryFragment
import com.example.ecovision.ui.fragment.HomeFragment
import com.example.ecovision.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()
    private val articleFragment = ArticleFragment()
    private val historyFragment = HistoryFragment()
    private val profileFragment = ProfileFragment()

    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        supportFragmentManager.beginTransaction().apply {
            add(R.id.frameFragment, profileFragment, "Profile").hide(profileFragment)
            add(R.id.frameFragment, historyFragment, "History").hide(historyFragment)
            add(R.id.frameFragment, articleFragment, "Article").hide(articleFragment)
            add(R.id.frameFragment, homeFragment, "Home")
        }.commit()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.miHome -> showFragment(homeFragment)
                R.id.miArticle -> showFragment(articleFragment)
                R.id.miHistory -> showFragment(historyFragment)
                R.id.miProfile -> showFragment(profileFragment)
            }
            true
        }

        binding.fabScan.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressedDispatcher.onBackPressed()
        } else {
            moveTaskToBack(true)
        }
    }
}