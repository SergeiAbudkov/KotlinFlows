package com.kotlinflows.foundation.sideeffects.resources.plugin

import android.content.Context
import com.kotlinflows.foundation.sideeffects.SideEffectMediator
import com.kotlinflows.foundation.sideeffects.SideEffectPlugin

/**
 * Plugin for accessing app resources from view-models.
 * Allows adding [Resources] interface to the view-model constructor.
 */
class ResourcesPlugin : SideEffectPlugin<ResourcesSideEffectMediator, Nothing> {

    override val mediatorClass: Class<ResourcesSideEffectMediator>
        get() = ResourcesSideEffectMediator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return ResourcesSideEffectMediator(applicationContext)
    }

}