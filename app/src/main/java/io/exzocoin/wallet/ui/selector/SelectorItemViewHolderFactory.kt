package io.exzocoin.wallet.ui.selector

import android.view.ViewGroup
import io.exzocoin.wallet.R
import io.exzocoin.views.inflate

class SelectorItemViewHolderFactory<ItemClass> : ItemViewHolderFactory<ItemViewHolder<ViewItemWrapper<ItemClass>>> {
    override fun create(parent: ViewGroup, viewType: Int): ItemViewHolder<ViewItemWrapper<ItemClass>> {
        return SelectorItemViewHolder(inflate(parent, R.layout.view_holder_selector_item))
    }
}
