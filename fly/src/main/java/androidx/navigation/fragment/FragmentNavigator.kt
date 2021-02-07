package androidx.navigation.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.ClassType
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigator.Destination
import com.tiamosu.fly.R
import java.util.*

/**
 * Navigator that navigates through [fragment transactions][FragmentTransaction]. Every
 * destination using this Navigator must set a valid Fragment class name with
 * `android:name` or [Destination.setClassName].
 *
 * The current Fragment from FragmentNavigator's perspective can be retrieved by calling
 * [FragmentManager.getPrimaryNavigationFragment] with the FragmentManager
 * passed to this FragmentNavigator.
 *
 * Note that the default implementation does Fragment transactions
 * asynchronously, so the current Fragment will not be available immediately
 * (i.e., in callbacks to [androidx.navigation.NavController.OnDestinationChangedListener]).
 */
@Navigator.Name("fragment")
class FragmentNavigator internal constructor(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) : Navigator<Destination>() {

    companion object {
        private const val TAG = "FragmentNavigator"
        private const val KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds"
    }

    private val backStack = ArrayDeque<Int>()
    private val resumeHandler by lazy { Handler(Looper.getMainLooper()) }

    /**
     * {@inheritDoc}
     *
     * This method must call
     * [FragmentTransaction.setPrimaryNavigationFragment]
     * if the pop succeeded so that the newly visible Fragment can be retrieved with
     * [FragmentManager.getPrimaryNavigationFragment].
     *
     * Note that the default implementation pops the Fragment
     * asynchronously, so the newly visible Fragment from the back stack
     * is not instantly available after this call completes.
     */
    override fun popBackStack(): Boolean {
        if (backStack.isEmpty()) {
            return false
        }
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state")
            return false
        }
        try {
            fragmentManager.popBackStack(
                generateBackStackName(backStack.size, backStack.peekLast()),
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            //Increase fault tolerance to deal with incorrectly-timed jumps in the case of nested sub-fragments
            var removeIndex = backStack.size - 1
            if (removeIndex >= fragmentManager.fragments.size) {
                removeIndex = fragmentManager.fragments.lastIndex
            }
            fragmentManager.fragments.removeAt(removeIndex)
            backStack.removeLast()

            var preFragmentIndex = removeIndex - 1
            if (preFragmentIndex >= fragmentManager.fragments.size) {
                preFragmentIndex = fragmentManager.fragments.lastIndex
            }
            if (preFragmentIndex >= 0 && preFragmentIndex < fragmentManager.fragments.size) {
                val preFragment = fragmentManager.fragments[preFragmentIndex]
                resumeHandler.postDelayed({
                    if (!preFragment.isStateSaved && preFragment.isAdded) {
                        fragmentManager.beginTransaction().apply {
                            setMaxLifecycle(preFragment, Lifecycle.State.RESUMED)
                            commit()
                        }
                    }
                }, 50)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun createDestination(): Destination {
        return Destination(this)
    }

    /**
     * Instantiates the Fragment via the FragmentManager's
     * [androidx.fragment.app.FragmentFactory].
     *
     * Note that this method is **not** responsible for calling
     * [Fragment.setArguments] on the returned Fragment instance.
     *
     * @param context         Context providing the correct [ClassLoader]
     * @param fragmentManager FragmentManager the Fragment will be added to
     * @param className       The Fragment to instantiate
     * @param args            The Fragment's arguments, if any
     * @return A new fragment instance.
     */
    @Deprecated(
        """Set a custom {@link FragmentFactory} via
      {@link FragmentManager#setFragmentFactory(FragmentFactory)} to control
      instantiation of Fragments.""",
        ReplaceWith("fragmentManager.fragmentFactory.instantiate(context.classLoader, className)")
    )
    fun instantiateFragment(
        context: Context,
        fragmentManager: FragmentManager,
        className: String,
        args: Bundle?
    ): Fragment {
        return fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
    }

    /**
     * {@inheritDoc}
     *
     * This method should always call
     * [FragmentTransaction.setPrimaryNavigationFragment]
     * so that the Fragment associated with the new destination can be retrieved with
     * [FragmentManager.getPrimaryNavigationFragment].
     *
     * Note that the default implementation commits the new Fragment
     * asynchronously, so the new Fragment is not instantly available
     * after this call completes.
     */
    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return null
        }
        //是否包含 popUpTo 设定页面一起退出栈
        val isPopUpToInclusive = navOptions?.isPopUpToInclusive ?: false
        if (backStack.isNotEmpty() && isPopUpToInclusive) {
            //防止出现栈中的上个页面先显示再隐藏的一个闪烁问题。
            resumeHandler.removeCallbacksAndMessages(null)
        }

        try {
            var className = destination.className
            if (className[0] == '.') {
                className = context.packageName + className
            }
            val toFragment =
                fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
            toFragment.arguments = args

            val ft = fragmentManager.beginTransaction()
            val enterAnim = navOptions?.enterAnim ?: 0
            val exitAnim = navOptions?.exitAnim ?: 0
            val popEnterAnim = navOptions?.popEnterAnim ?: 0
            val popExitAnim = navOptions?.popExitAnim ?: 0
            if (enterAnim != 0 || exitAnim != 0 || popEnterAnim != 0 || popExitAnim != 0) {
                ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
            }

            //Solve the unexpected display of popUpToInclusive under the add hide solution
            //Increase fault tolerance to deal with incorrectly-timed jumps in the case of nested sub-fragments
            if (backStack.size > 0 && fragmentManager.fragments.size > 0) {
                if (fragmentManager.fragments.size > backStack.size - 1) {
                    val hideFragment = fragmentManager.fragments[backStack.size - 1]
                    ft.hide(hideFragment)
                    ft.setMaxLifecycle(hideFragment, Lifecycle.State.STARTED)
                }
                ft.add(containerId, toFragment)
                ft.setMaxLifecycle(toFragment, Lifecycle.State.RESUMED)
            } else {
                ft.replace(containerId, toFragment)
                ft.setMaxLifecycle(toFragment, Lifecycle.State.RESUMED)
            }
            ft.setPrimaryNavigationFragment(toFragment)

            @IdRes val destId = destination.id
            val initialNavigation = backStack.isEmpty()
            // TODO Build first class singleTop behavior for fragments
            val isSingleTopReplacement =
                (!initialNavigation && navOptions?.shouldLaunchSingleTop() == true && backStack.peekLast() == destId)

            val isAdded = when {
                initialNavigation -> {
                    true
                }
                isSingleTopReplacement -> {
                    // Single Top means we only want one instance on the back stack
                    if (backStack.size > 1) {
                        // If the Fragment to be replaced is on the FragmentManager's
                        // back stack, a simple replace() isn't enough so we
                        // remove it from the back stack and put our replacement
                        // on the back stack in its place
                        fragmentManager.popBackStack(
                            generateBackStackName(backStack.size, backStack.peekLast()),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                        ft.addToBackStack(generateBackStackName(backStack.size, destId))

                        if (fragmentManager.fragments.isNotEmpty()) {
                            var preIndex = backStack.size - 2
                            if (preIndex >= fragmentManager.fragments.size) {
                                preIndex = fragmentManager.fragments.lastIndex
                            }
                            val preFragment = fragmentManager.fragments[preIndex]
                            ft.setMaxLifecycle(preFragment, Lifecycle.State.STARTED)
                        }
                    }
                    false
                }
                else -> {
                    ft.addToBackStack(generateBackStackName(backStack.size + 1, destId))
                    true
                }
            }
            if (navigatorExtras is Extras) {
                for ((key, value) in navigatorExtras.sharedElements) {
                    if (key != null && value != null) {
                        ft.addSharedElement(key, value)
                    }
                }
            }
            ft.setReorderingAllowed(true)
            ft.commit()

            // The commit succeeded, update our view of the world
            return if (isAdded) {
                backStack.add(destId)
                destination
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun onSaveState(): Bundle {
        val b = Bundle()
        val backStack = IntArray(backStack.size)
        var index = 0
        for (id in this.backStack) {
            backStack[index++] = id
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack)
        return b
    }

    override fun onRestoreState(savedState: Bundle) {
        val backStack = savedState.getIntArray(KEY_BACK_STACK_IDS)
        if (backStack != null) {
            this.backStack.clear()
            for (destId in backStack) {
                this.backStack.add(destId)
            }
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int?): String {
        return "$backStackIndex-$destId"
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun getDestId(backStackName: String?): Int? {
        val split = backStackName?.split("-")?.toTypedArray()
            ?: arrayOfNulls<String>(0)
        check(split.size == 2) {
            ("Invalid back stack entry on the "
                    + "NavHostFragment's back stack - use getChildFragmentManager() "
                    + "if you need to do custom FragmentTransactions from within "
                    + "Fragments created via your navigation graph.")
        }
        return try {
            // Just make sure the backStackIndex is correctly formatted
            split[0]?.toInt()
            split[1]?.toInt()
        } catch (e: NumberFormatException) {
            throw IllegalStateException(
                "Invalid back stack entry on the "
                        + "NavHostFragment's back stack - use getChildFragmentManager() "
                        + "if you need to do custom FragmentTransactions from within "
                        + "Fragments created via your navigation graph."
            )
        }
    }

    /**
     * NavDestination specific to [FragmentNavigator]
     */
    @ClassType(Fragment::class)
    class Destination
    /**
     * Construct a new fragment destination. This destination is not valid until you set the
     * Fragment via [setClassName].
     *
     * @param fragmentNavigator The [FragmentNavigator] which this destination
     * will be associated with. Generally retrieved via a
     * [androidx.navigation.NavController]'s
     * [NavigatorProvider.getNavigator] method.
     */
    internal constructor(fragmentNavigator: Navigator<out Destination?>) :
        NavDestination(fragmentNavigator) {
        private var mClassName: String? = null

        /**
         * Construct a new fragment destination. This destination is not valid until you set the
         * Fragment via [.setClassName].
         *
         * @param navigatorProvider The [androidx.navigation.NavController] which this destination
         * will be associated with.
         */
        constructor(navigatorProvider: NavigatorProvider) : this(
            navigatorProvider.getNavigator<FragmentNavigator>(FragmentNavigator::class.java)
        )

        @CallSuper
        override fun onInflate(
            context: Context,
            attrs: AttributeSet
        ) {
            super.onInflate(context, attrs)
            val ta = context.resources.obtainAttributes(attrs, R.styleable.FragmentNavigator)
            ta.getString(R.styleable.FragmentNavigator_android_name)?.let { setClassName(it) }
            ta.recycle()
        }

        /**
         * Set the Fragment class name associated with this destination
         *
         * @param className The class name of the Fragment to show when you navigate to this
         * destination
         * @return this [Destination]
         */
        fun setClassName(className: String): Destination {
            mClassName = className
            return this
        }

        /**
         * Gets the Fragment's class name associated with this destination
         *
         * @throws IllegalStateException when no Fragment class was set.
         */
        val className: String
            get() {
                checkNotNull(mClassName) { "Fragment class was not set" }
                return mClassName!!
            }

        override fun toString(): String {
            return StringBuilder().apply {
                append(super.toString())
                append(" class=")
                if (mClassName == null) {
                    append("null")
                } else {
                    append(mClassName)
                }
            }.toString()
        }
    }

    /**
     * Extras that can be passed to FragmentNavigator to enable Fragment specific behavior
     */
    internal class Extras constructor(sharedElements: Map<View?, String?>) : Navigator.Extras {
        private val sharedElementMaps = LinkedHashMap<View?, String?>()

        /**
         * Gets the map of shared elements associated with these Extras. The returned map
         * is an [unmodifiable][Collections.unmodifiableMap] copy of the underlying
         * map and should be treated as immutable.
         */
        val sharedElements: Map<View?, String?>
            get() = Collections.unmodifiableMap(sharedElementMaps)

        init {
            sharedElementMaps.putAll(sharedElements)
        }

        /**
         * Builder for constructing new [Extras] instances. The resulting instances are
         * immutable.
         */
        class Builder {
            private val sharedElementsMaps = LinkedHashMap<View?, String?>()

            /**
             * Adds multiple shared elements for mapping Views in the current Fragment to
             * transitionNames in the Fragment being navigated to.
             *
             * @param sharedElements Shared element pairs to add
             * @return this [Builder]
             */
            fun addSharedElements(sharedElements: Map<View?, String?>): Builder {
                for ((view, name) in sharedElements) {
                    if (view != null && name != null) {
                        addSharedElement(view, name)
                    }
                }
                return this
            }

            /**
             * Maps the given View in the current Fragment to the given transition name in the
             * Fragment being navigated to.
             *
             * @param sharedElement A View in the current Fragment to match with a View in the
             * Fragment being navigated to.
             * @param name          The transitionName of the View in the Fragment being navigated to that
             * should be matched to the shared element.
             * @return this [Builder]
             * @see FragmentTransaction.addSharedElement
             */
            fun addSharedElement(
                sharedElement: View,
                name: String
            ): Builder {
                sharedElementsMaps[sharedElement] = name
                return this
            }

            /**
             * Constructs the final [Extras] instance.
             *
             * @return An immutable [Extras] instance.
             */
            fun build(): Extras {
                return Extras(sharedElementsMaps)
            }
        }
    }
}