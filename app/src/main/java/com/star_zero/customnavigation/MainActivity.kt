package com.star_zero.customnavigation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        navController = findNavController(R.id.custom_nav_host)

        navController.addOnNavigatedListener { controller, destination ->
            // Navigate後

            // ボタンのイベントを設定
            when (destination.id) {
                R.id.firstView -> {
                    findViewById<View>(R.id.button_first).setOnClickListener {
                        controller.navigate(R.id.action_firstView_to_secondView)
                    }
                }
                R.id.secondView -> {
                    findViewById<View>(R.id.button_second).setOnClickListener {
                        controller.navigate(R.id.action_secondView_to_thirdView)
                    }
                }
            }
        }

        // ActionBarとNavControllerを連動させる
        // 左上の ← ボタンの表示制御
        setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }
}
