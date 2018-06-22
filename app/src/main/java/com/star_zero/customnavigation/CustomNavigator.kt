package com.star_zero.customnavigation

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.core.view.plusAssign
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import java.util.*

@Navigator.Name("custom_view") // navigation.xmlに設定するカスタムタグ
class CustomNavigator(private val container: ViewGroup) : Navigator<CustomNavigator.Destination>() {

    // Destination.id と レイアウトのセット
    data class NavLayout(val id: Int, @LayoutRes val layout: Int)

    private val backStack: ArrayDeque<NavLayout> = ArrayDeque()

    override fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?) {
        // 画面遷移

        val navLayout = NavLayout(destination.id, destination.layout)
        backStack.push(navLayout)
        replaceView(navLayout.layout)

        // 画面遷移のイベント発行
        dispatchOnNavigatorNavigated(navLayout.id, BACK_STACK_DESTINATION_ADDED)
    }

    override fun createDestination() = Destination(this)

    override fun popBackStack(): Boolean {
        // 戻る処理

        return if (backStack.size < 2) {
            false
        } else {
            // 一つ前のidとレイアウトを取得
            backStack.pop()
            val navLayout = backStack.peek()
            replaceView(navLayout.layout)
            dispatchOnNavigatorNavigated(navLayout.id, BACK_STACK_DESTINATION_POPPED)
            true
        }
    }

    private fun replaceView(@LayoutRes layout: Int) {
        // Viewを入れ替え
        container.removeAllViews()
        val view = LayoutInflater.from(container.context).inflate(layout, container, false)
        container += view
    }

    class Destination(navigator: Navigator<out NavDestination>) : NavDestination(navigator) {

        @LayoutRes
        var layout: Int = 0

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)

            // navigation.xmlから遷移に必要な情報を取得
            context.withStyledAttributes(attrs, R.styleable.CustomNavigator, 0, 0, {
                layout = getResourceId(R.styleable.CustomNavigator_layout, 0)
            })
        }
    }

    // 以下、画面回転時の保存と復元に関する処理

    override fun onSaveState(): Bundle? {
        val bundle = Bundle()
        val id = IntArray(backStack.size)
        val layout = IntArray(backStack.size)

        backStack.forEachIndexed({ index, navLayout ->
            id[index] = navLayout.id
            layout[index] = navLayout.layout
        })

        bundle.putIntArray("id", id)
        bundle.putIntArray("layout", layout)

        return bundle
    }

    override fun onRestoreState(savedState: Bundle) {
        val id = savedState.getIntArray("id")
        val layout = savedState.getIntArray("layout")

        backStack.clear()
        id.forEachIndexed { index, _ ->
            backStack.add(NavLayout(id[index], layout[index]))
        }

        // Viewを復元
        replaceView(backStack.peek().layout)
        dispatchOnNavigatorNavigated(backStack.peek().id, BACK_STACK_DESTINATION_ADDED)
    }

}
