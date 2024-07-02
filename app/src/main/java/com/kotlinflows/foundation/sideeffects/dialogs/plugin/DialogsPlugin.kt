package com.kotlinflows.foundation.sideeffects.dialogs.plugin

import android.content.Context
import com.kotlinflows.foundation.sideeffects.SideEffectMediator
import com.kotlinflows.foundation.sideeffects.SideEffectPlugin

/**
 * Plugin for displaying dialogs from view-models.
 * Allows adding [Dialogs] interface to view-model constructor.
 */
class DialogsPlugin : SideEffectPlugin<DialogsSideEffectMediator, DialogsSideEffectImpl> {

    override val mediatorClass: Class<DialogsSideEffectMediator>
        get() = DialogsSideEffectMediator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<DialogsSideEffectImpl> {
        return DialogsSideEffectMediator()
    }

    override fun createImplementation(mediator: DialogsSideEffectMediator): DialogsSideEffectImpl {
        return DialogsSideEffectImpl(mediator.retainedState)
    }
}