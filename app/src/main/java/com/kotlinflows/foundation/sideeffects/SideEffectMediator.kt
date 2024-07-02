package com.kotlinflows.foundation.sideeffects

import com.kotlinflows.foundation.model.dispatchers.Dispatcher
import com.kotlinflows.foundation.model.dispatchers.MainThreadDispatcher
import com.kotlinflows.foundation.utils.ResourceActions

/**
 * Base class for all side-effect mediators.
 * These mediators live in [ActivityScopeViewModel].
 * Mediator should delegate all UI-related logic to the implementations via [target] field.
 */
open class SideEffectMediator<Implementation>(
    dispatcher: Dispatcher = MainThreadDispatcher()
) {

    protected val target = ResourceActions<Implementation>(dispatcher)

    /**
     * Assign/unassign the target implementation for this provider.
     */
    fun setTarget(target: Implementation?) {
        this.target.resource = target
    }

    fun clear() {
        target.clear()
    }

}