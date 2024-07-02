package com.kotlinflows.foundation.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.kotlinflows.foundation.model.ErrorResult
import com.kotlinflows.foundation.model.PendingResult
import com.kotlinflows.foundation.model.Result
import com.kotlinflows.foundation.model.SuccessResult
import com.kotlinflows.foundation.views.activity.ActivityDelegateHolder

/**
 * Base class for all fragments
 */
abstract class BaseFragment : Fragment() {

    /**
     * View-model that manages this fragment
     */
    abstract val viewModel: BaseViewModel

    /**
     * Call this method when activity controls (e.g. toolbar) should be re-rendered
     */
    fun notifyScreenUpdates() {
        (requireActivity() as ActivityDelegateHolder).delegate.notifyScreenUpdates()
    }

    fun <T> renderResult(
        root: ViewGroup,
        result: Result<T>,
        onError: (Exception) -> Unit,
        onPending: () -> Unit,
        onSuccess: (T) -> Unit
    ) {
        root.children.forEach {
            it.visibility = View.GONE
        }
        when (result) {
            is ErrorResult -> onError(result.exception)
            is PendingResult -> onPending()
            is SuccessResult -> onSuccess(result.data)
        }
    }
}