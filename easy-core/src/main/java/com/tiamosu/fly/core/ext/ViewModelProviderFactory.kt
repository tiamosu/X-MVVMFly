package com.tiamosu.fly.core.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * @author tiamosu
 * @date 2020/5/14.
 */
class ViewModelProviderFactory(private vararg val arguments: Any) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val constructors = modelClass.constructors
        val constructor = find(constructors, object : Predicate<Constructor<*>> {
            override fun test(element: Constructor<*>): Boolean {
                return element.parameterTypes.size == arguments.size
            }
        }) ?: throw RuntimeException(
            "$this constructor arguments do not match the $modelClass constructor arguments."
        )
        return try {
            constructor.newInstance(*arguments) as T
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }

    private interface Predicate<T> {
        fun test(element: T): Boolean
    }

    companion object {
        private fun <T> find(array: Array<T>, predicate: Predicate<T>): T? {
            for (element in array) {
                if (predicate.test(element)) {
                    return element
                }
            }
            return null
        }
    }
}