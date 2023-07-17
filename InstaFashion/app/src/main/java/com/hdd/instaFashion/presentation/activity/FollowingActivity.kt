package com.hdd.instaFashion.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.domain.adapter.SetFragmentAdapter
import com.hdd.instaFashion.presentation.fragments.FollowersFragment
import com.hdd.instaFashion.presentation.fragments.FollowingFragment

class FollowingActivity : AppCompatActivity() {
    private lateinit var tabTitleList: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)

        // for Following layout
        viewPager = findViewById(R.id.af_viewPager)
        tabLayout = findViewById(R.id.af_tabLayout)

        tabTitleList = arrayListOf<String>("Followers", "Following")
        fragmentList = arrayListOf<Fragment>(
            FollowersFragment(ServiceBuilder.user!!._id),
            FollowingFragment(ServiceBuilder.user!!._id),
        )

        // setting up adapter class for view pager2 in PDL
        val adapter = SetFragmentAdapter(fragmentList, supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) {
                tab, position -> tab.text = tabTitleList[position]
        }.attach()

    }
}