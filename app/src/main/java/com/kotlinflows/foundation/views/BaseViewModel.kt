package com.kotlinflows.foundation.views

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import com.kotlinflows.foundation.model.ErrorResult
import com.kotlinflows.foundation.model.Result
import com.kotlinflows.foundation.model.SuccessResult
import com.kotlinflows.foundation.utils.Event
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow


typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

/**
 * Base class for all view-models.
 */
open class BaseViewModel : ViewModel() {

    private val coroutineContext = (SupervisorJob() + Dispatchers.Main.immediate)
    protected val viewModelScope = CoroutineScope(coroutineContext)

    override fun onCleared() {
        super.onCleared()
        clearViewModelScope()
    }

    /**
     * Override this method in child classes if you want to listen for results
     * from other screens
     */
    open fun onResult(result: Any) {

    }

    /**
     * Override this method in child classes if you want to control go-back behaviour.
     * Return `true` if you want to abort closing this screen
     */
    open fun onBackPressed(): Boolean {
        clearViewModelScope()
        return false
    }

    /**
     * Launch task asynchronously and map its result to the specified
     * [liveResult].
     * Task is cancelled automatically if view-model is going to be destroyed.
     */
    fun <T> into(liveResult: MutableLiveResult<T>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                liveResult.postValue(SuccessResult(block()))
            } catch (e: Exception) {
                liveResult.postValue(ErrorResult(e))
            }
        }
    }

    /**
     * Launch the specified suspending [block] and use its result as a value for the
     * provided [stateFlow].
     */
    fun <T> into(stateFlow: MutableStateFlow<Result<T>>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                stateFlow.value = SuccessResult(block())
            } catch (e: Exception) {
                if (e !is CancellationException) stateFlow.value = ErrorResult(e)
            }
        }
    }



    /**
     * Create a [MutableStateFlow] which reflects a state of value with the
     * specified key managed by [SavedStateHandle]. When the value is updated,
     * the instance of [MutableStateFlow] emits a new item with the updated value.
     * When some new value is assigned to the [MutableStateFlow] via [MutableStateFlow.value]
     * it is recorded into [SavedStateHandle]. So actually this method creates a
     * [MutableStateFlow] which works in the same way as [MutableLiveData] returned
     * by [SavedStateHandle.getLiveData].
     */
    fun <T> SavedStateHandle.asStateFlow(key: String, initialValue: T): MutableStateFlow<T> {
        val savedStateHandle = this
        val mutableFlow = MutableStateFlow(savedStateHandle[key] ?: initialValue)

        viewModelScope.launch {
            mutableFlow.collect {
                savedStateHandle[key] = it
            }
        }

        viewModelScope.launch {
            savedStateHandle.getLiveData<T>(key).asFlow().collect {
                mutableFlow.value = it
            }
        }

        return mutableFlow

    }

    private fun clearViewModelScope() {
        viewModelScope.cancel()
    }

}