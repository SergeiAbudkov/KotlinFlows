package com.kotlinflows.foundation.sideeffects.toasts.plugin

import android.content.Context
import android.widget.Toast
import com.kotlinflows.foundation.model.dispatchers.MainThreadDispatcher
import com.kotlinflows.foundation.sideeffects.SideEffectMediator
import com.kotlinflows.foundation.sideeffects.toasts.Toasts

/**
 * Android implementation of [Toasts]. Displaying simple toast message and getting string from resources.
 */
class ToastsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Toasts {

    private val dispatcher = MainThreadDispatcher()

    override fun toast(message: String) {
        dispatcher.dispatch {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}