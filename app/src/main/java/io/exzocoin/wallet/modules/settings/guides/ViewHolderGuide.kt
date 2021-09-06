package io.exzocoin.wallet.modules.settings.guides

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.exzocoin.wallet.core.setOnSingleClickListener
import io.exzocoin.wallet.entities.Guide
import io.exzocoin.core.helpers.DateHelper
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_guide_preview.*

class ViewHolderGuide(override val containerView: View, private val listener: ClickListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    interface ClickListener {
        fun onClick(guide: Guide)
    }

    private var guide: Guide? = null

    init {
        containerView.setOnSingleClickListener {
            guide?.let {
                listener.onClick(it)
            }
        }
    }

    fun bind(guide: Guide) {
        this.guide = guide

        title.text = guide.title
        date.text = DateHelper.shortDate(guide.updatedAt)

        image.setImageDrawable(null)

        guide.imageUrl?.let {
            Picasso.get().load(it).into(image)
        }

    }

}
