package com.th.novelpartymember

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.th.novelpartymember.ConstantPool.NAV_HOME
import com.th.novelpartymember.ConstantPool.NAV_MY_PAGE
import com.th.novelpartymember.ConstantPool.NAV_MY_WRITE
import com.th.novelpartymember.databinding.ActivityMainBinding
import com.th.novelpartymember.view.home.HomeFragment
import com.th.novelpartymember.view.my_page.MyPageFragment
import com.th.novelpartymember.view.my_write.MyWriteFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var userInfoViewModel: UserInfoViewModel

    companion object {
        private const val FINISH_INTERVAL_TIME: Long = 2000
        private var backPressTime: Long = 0
    }

    private val mainPagerAdapter: MainPagerAdapter by lazy {
        MainPagerAdapter(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userInfoViewModel = ViewModelProvider(this)[UserInfoViewModel::class.java]

        this.onBackPressedDispatcher.addCallback(this@MainActivity, callback)

        val returnToSecondPage = intent.getBooleanExtra("RETURN_TO_SECOND_PAGE", false)

        init(returnToSecondPage)
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            mainPagerAdapter.run {
                val tempTime = System.currentTimeMillis()
                val intervalTime = tempTime - backPressTime
                if (intervalTime in 0..FINISH_INTERVAL_TIME) {
                    ActivityCompat.finishAffinity(this@MainActivity)
                } else {
                    backPressTime = tempTime
                    Toast.makeText(this@MainActivity, "뒤로가기를 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                }

                if (binding.viewPagerMain.currentItem == 1) {
                    changeScreen(0)
                    return
                }

                if (binding.viewPagerMain.currentItem == 2) {
                    changeScreen(0)
                    return
                }
            }
        }
    }

    private fun init(returnToSecondPage: Boolean) {
        initNavigationBar()

//        getUserInfo()

        binding.viewPagerMain.apply {
            offscreenPageLimit = 1
            adapter = mainPagerAdapter
            isUserInputEnabled = false

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    when (position) {
                        NAV_HOME -> {
                            binding.apply {
                                bottomNavigation.selectedItemId = R.id.action_home
                            }
                        }

                        NAV_MY_WRITE -> {
                            binding.apply {
                                bottomNavigation.selectedItemId = R.id.action_my_write
                            }
                        }

                        NAV_MY_PAGE -> {
                            binding.apply {
                                bottomNavigation.selectedItemId = R.id.action_my_page
                            }
                        }

                        else -> {

                        }
                    }
                }
            })
        }
        if (returnToSecondPage) {
            binding.viewPagerMain.setCurrentItem(NAV_MY_WRITE, false)
        }
    }

    private fun changeScreen(index: Int) {
        binding.viewPagerMain.setCurrentItem(index, false)
    }

    private fun setNavigationItem(position: Int) {
        changeScreen(position)
        binding.viewPagerMain.currentItem = position
    }

    private fun initNavigationBar() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    setNavigationItem(0)
                    item.setIcon(R.drawable.ic_sopamo_select)

                    binding.apply {
                        bottomNavigation.menu.apply {
                            findItem(R.id.action_my_write)
                                ?.setIcon(R.drawable.ic_my_write_not_select)

                            findItem(R.id.action_my_page)
                                ?.setIcon(R.drawable.ic_user_not_select)
                        }
                    }
                }

                R.id.action_my_write -> {
                    setNavigationItem(1)
                    item.setIcon(R.drawable.ic_my_write_select)

                    binding.apply {
                        bottomNavigation.menu.apply {
                            findItem(R.id.action_home)
                                ?.setIcon(R.drawable.ic_sopamo_not_select)

                            findItem(R.id.action_my_page)
                                ?.setIcon(R.drawable.ic_user_not_select)
                        }
                    }
                }

                R.id.action_my_page -> {
                    setNavigationItem(2)

                    item.setIcon(R.drawable.ic_user_select)

                    binding.apply {
                        bottomNavigation.menu.apply {
                            findItem(R.id.action_home)
                                ?.setIcon(R.drawable.ic_sopamo_not_select)

                            findItem(R.id.action_my_write)
                                ?.setIcon(R.drawable.ic_my_write_not_select)
                        }
                    }
                }
            }
            true
        }
        binding.bottomNavigation.selectedItemId = R.id.action_home
    }

    private inner class MainPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        val homeFragment: HomeFragment by lazy {
            HomeFragment()
        }

        val myWriteFragment: MyWriteFragment by lazy {
            MyWriteFragment()
        }

        val myPageFragment: MyPageFragment by lazy {
            MyPageFragment()
        }

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment = when (position) {
            NAV_HOME -> homeFragment
            NAV_MY_WRITE -> myWriteFragment
            NAV_MY_PAGE -> myPageFragment
            else -> throw Exception(Throwable(""))
        }
    }
}