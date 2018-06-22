package com.star_zero.customnavigation

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.plusAssign

class CustomNavHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), NavHost {

    private val navController = NavController(context)

    private var graphId = 0

    init {
        Navigation.setViewNavController(this, navController)

        // カスタムのNavigatorを追加
        navController.navigatorProvider += CustomNavigator(this)

        // XML属性からNavigationのGraphを取得
        context.withStyledAttributes(attrs, R.styleable.CustomNavHost, 0, 0, {
            graphId = getResourceId(R.styleable.CustomNavHost_navGraph, 0)
        })
    }

    override fun getNavController() = navController

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // onRestoreInstanceState後に処理したいので、onAttachedToWindowでGraph設定
        // 復元後の場合は何もしない

        if (navController.graph == null) {
            navController.setGraph(graphId)
        }
    }

    // 以下、画面回転時の保存と復元に関する処理

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.navControllerState = navController.saveState()
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            navController.restoreState(state.navControllerState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    class SavedState: BaseSavedState {
        var navControllerState: Bundle? = null

        constructor(superState: Parcelable): super(superState)

        constructor(source: Parcel): super(source) {
            navControllerState = source.readBundle(javaClass.classLoader)
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeBundle(navControllerState)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
