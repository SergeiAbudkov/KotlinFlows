package com.kotlinflows.simplemvvmkotlinflows.views.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.simplemvvmkotlinflows.R
import com.kotlinflows.foundation.model.EmptyProgress
import com.kotlinflows.foundation.model.PendingResult
import com.kotlinflows.foundation.model.PercentageProgress
import com.kotlinflows.foundation.model.Progress
import com.kotlinflows.foundation.model.Result
import com.kotlinflows.foundation.model.getPercentage
import com.kotlinflows.foundation.model.isInProgress
import com.kotlinflows.foundation.model.takeSuccess
import com.kotlinflows.foundation.sideeffects.navigator.Navigator
import com.kotlinflows.foundation.sideeffects.resources.Resources
import com.kotlinflows.foundation.sideeffects.toasts.Toasts
import com.kotlinflows.foundation.utils.finiteShareIn
import com.kotlinflows.foundation.views.BaseViewModel
import com.kotlinflows.simplemvvmkotlinflows.model.colors.ColorsRepository
import com.kotlinflows.simplemvvmkotlinflows.model.colors.NamedColor
import com.kotlinflows.simplemvvmkotlinflows.views.changecolor.ChangeColorFragment.Screen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), ColorsAdapter.Listener {


    //input sources
    private val _availableColors = MutableStateFlow<Result<List<NamedColor>>>(PendingResult())
    private val _currentColorId = savedStateHandle.asStateFlow("currentColorId", screen.currentColorId)

    private var _instantSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private var _sampledSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)

    //main destination (contains merged values from _availableColors & _currentColorId)
    val viewState: Flow<Result<ViewState>> = combine(
        _availableColors,
        _currentColorId,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )


    //side destination, also the same result can be achieved by using Transformations.map() function.
    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    init {
        load()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_instantSaveInProgress.value.isInProgress()) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _instantSaveInProgress.value = PercentageProgress.START
            _sampledSaveInProgress.value = PercentageProgress.START
            val currentColorId =
                _currentColorId.value
            val currentColor = colorsRepository.getById(currentColorId)

            // here we don't want to listen progress but only wait for operation completion
            // so we use collect() without any inner block:
            val sharedFlowCurrentColor = colorsRepository.setCurrentColor(currentColor)
                .finiteShareIn(this, started = SharingStarted.Eagerly, replay = 1)


            val instantJob = launch {
                sharedFlowCurrentColor.collect { percentage ->
                    _instantSaveInProgress.value = PercentageProgress(percentage)

                }
            }

            val sampledJob = launch {
                sharedFlowCurrentColor.sample(200).collect {percentageText ->
                    _sampledSaveInProgress.value = PercentageProgress(percentageText)

                }
            }

            instantJob.join()
            sampledJob.join()

            navigator.goBack(result = currentColor)
        } catch (e: Exception) {
            if (e !is CancellationException) { toasts.toast(resources.getString(R.string.error_happened))
            }
        } finally {
            _instantSaveInProgress.value = EmptyProgress
            _sampledSaveInProgress.value = EmptyProgress
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    /**
     * [MediatorLiveData] can listen other LiveData instances (even more than 1)
     * and combine their values.
     * Here we listen the list of available colors ([_availableColors] live-data) + current color id
     * ([_currentColorId] live-data), then we use both of these values in order to create a list of
     * [NamedColorListItem], it is a list to be displayed in RecyclerView.
     */

    private fun setScreenTitle() {
        val colors = _availableColors.value
        val currentColorId = _currentColorId.value
        val currentColor = colors.map {
            it.first { it.id == currentColorId }
        }
        val nameCurrentColor: String? = currentColor.takeSuccess()?.name
        _screenTitle.value =
            if (nameCurrentColor != null) {
                resources.getString(
                    R.string.change_color_screen_title,
                    nameCurrentColor
                )
            } else {
                resources.getString(R.string.change_color_screen_title_simple)
            }
    }

    private fun mergeSources(
        colors: Result<List<NamedColor>>,
        currentColorId: Long,
        instantSaveInProgress: Progress,
        sampledSaveInProgress: Progress
    ): Result<ViewState> {
        setScreenTitle()
        return colors.map { colorsList ->
            ViewState(
                colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !instantSaveInProgress.isInProgress(),
                showCancelButton = !instantSaveInProgress.isInProgress(),
                showSaveProgressBar = instantSaveInProgress.isInProgress(),

                saveProgressPercentage = instantSaveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(R.string.percentage_value, sampledSaveInProgress.getPercentage())
            )
        }
    }

    data class ViewState(
        val colorList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean,

        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String,

    )

    fun tryAgain() {
        load()
    }

    private fun load() = into(_availableColors) {
        return@into colorsRepository.getAvailableColors()
    }
}