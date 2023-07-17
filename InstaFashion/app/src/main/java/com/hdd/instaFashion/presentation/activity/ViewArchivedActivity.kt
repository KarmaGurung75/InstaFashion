package com.hdd.instaFashion.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hdd.instaFashion.R
import com.hdd.instaFashion.domain.adapter.ViewPager2Adapter
import com.hdd.instaFashion.presentation.fragments.ArchivedPostFragment
import com.hdd.instaFashion.presentation.fragments.ArchivedRecipeFragment

class ViewArchivedActivity : AppCompatActivity() {
    private lateinit var tabTitleList: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_archived)

        // for profile layout
        viewPager = findViewById(R.id.ava_viewPager)
        tabLayout = findViewById(R.id.ava_tabLayout)

        tabTitleList = arrayListOf<String>("Archived Post", "Archived Product")
        fragmentList = arrayListOf<Fragment>(
            ArchivedPostFragment(),
            ArchivedRecipeFragment()
        )

        // setting up adapter class for view pager2
        val adapter = ViewPager2Adapter(fragmentList, supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) {
                tab, position -> tab.text = tabTitleList[position]
        }.attach()
    }
}