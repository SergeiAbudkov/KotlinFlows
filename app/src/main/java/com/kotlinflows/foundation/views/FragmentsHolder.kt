package com.kotlinflows.foundation.views

import com.kotlinflows.foundation.ActivityScopeViewModel

interface FragmentsHolder {

    fun notifyScreenUpdates()

    fun getActivityScopeViewModel(): ActivityScopeViewModel
}