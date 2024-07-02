package com.kotlinflows.foundation

import androidx.lifecycle.ViewModel
import com.kotlinflows.foundation.sideeffects.SideEffectMediator
import com.kotlinflows.foundation.sideeffects.SideEffectMediatorsHolder


/**
 * Holder for side-effect mediators.
 * It is based on activity view-model because instances of side-effect mediators
 * should be available from fragments' view-models (usually they are passed to the view-model constructor).
 */
class ActivityScopeViewModel : ViewModel() {

    internal val sideEffectMediatorsHolder = SideEffectMediatorsHolder()

    // contains the list of side-effect mediators that can be
    // passed to view-model constructors
    val sideEffectMediators: List<SideEffectMediator<*>>
        get() = sideEffectMediatorsHolder.mediators

    override fun onCleared() {
        super.onCleared()
        sideEffectMediatorsHolder.clear()
    }

}