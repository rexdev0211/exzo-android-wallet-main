package io.exzocoin.wallet.modules.market.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.smarteist.autoimageslider.SliderViewAdapter

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import io.exzocoin.wallet.R
import io.exzocoin.wallet.entities.ExzoAds

class MarketAdsAdapter(context: Context) :
    SliderViewAdapter<MarketAdsAdapter.SliderAdapterVH>() {
    private val context: Context
    private var mSliderItems: MutableList<ExzoAds> = ArrayList()
    fun renewItems(sliderItems: MutableList<ExzoAds>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun setItems(sliderItems: List<ExzoAds>) {
        mSliderItems = sliderItems.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: ExzoAds) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_advertise_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: ExzoAds = mSliderItems[position]
        viewHolder.description.setText(sliderItem.description)
        viewHolder.title.setText(sliderItem.title)
        viewHolder.button.setText(sliderItem.buttonTitle)
        viewHolder.itemView.setOnClickListener({
            fun onClick(v: View?) {
                Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        var title: TextView
        var description: TextView
        var button: Button

        init {
            title = itemView.findViewById<TextView>(R.id.title)
            description = itemView.findViewById<TextView>(R.id.subtitle)
            button = itemView.findViewById<Button>(R.id.moreButton)
        }
    }

    init {
        this.context = context
    }
}