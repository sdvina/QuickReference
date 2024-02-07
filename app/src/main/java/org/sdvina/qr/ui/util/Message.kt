package org.sdvina.qr.ui.util

import androidx.annotation.StringRes

data class Message(
    val id: Long,
    @StringRes val messageId: Int,
    val formatArgs: Array<Any>
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false
        if (messageId != other.messageId) return false
        return formatArgs.contentEquals(other.formatArgs)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + messageId
        result = 31 * result + formatArgs.contentHashCode()
        return result
    }
}