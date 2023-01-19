package org.sdvina.qr.ui.reference

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.sdvina.qr.ui.util.Message
import java.util.*

data class ReferenceViewModelSate(
    val messages: List<Message> = emptyList()
)
class ReferenceViewModel: ViewModel() {
    private var _state = MutableStateFlow(ReferenceViewModelSate())
    val sate: StateFlow<ReferenceViewModelSate>
        get() = _state

    fun showMessage(@StringRes messageId: Int, formatArgs: Array<Any>){
        _state.update { currentState ->
            val messages = currentState.messages + Message(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = messageId,
                formatArgs = formatArgs
            )
            currentState.copy(messages = messages)
        }
    }

    fun messageShown(id: Long) {
        _state.update { currentUiState ->
            val messages = currentUiState.messages.filterNot { it.id == id }
            currentUiState.copy(messages = messages)
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReferenceViewModel() as T
            }
        }
    }
}