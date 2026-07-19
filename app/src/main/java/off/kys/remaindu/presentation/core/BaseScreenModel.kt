package off.kys.remaindu.presentation.core

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseScreenModel<S, E, F>(initialState: S) : ScreenModel {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<F>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    abstract fun onEvent(event: E)

    protected fun updateState(reducer: (S) -> S) {
        _state.update(reducer)
    }

    protected fun sendEffect(effect: F) {
        _effects.trySend(effect)
    }
}
