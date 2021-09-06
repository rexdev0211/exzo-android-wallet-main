package io.exzocoin.wallet.ui.selector

import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.providers.Translator

class ViewItemWrapper<T>(val title: String, val item: T, val color: Int? = null) {
    override fun equals(other: Any?) = when {
        other !is ViewItemWrapper<*> -> false
        else -> item == other.item
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (item?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun <T>getAny(): ViewItemWrapper<T?> {
            return ViewItemWrapper(Translator.getString(R.string.Any), null, R.color.grey)
        }
    }
}
