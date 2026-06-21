package presentation.viewmodels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class ViewModel {
    protected val viewModelScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main
    )

    protected fun onCleared() {
        viewModelScope.cancel()
    }
}