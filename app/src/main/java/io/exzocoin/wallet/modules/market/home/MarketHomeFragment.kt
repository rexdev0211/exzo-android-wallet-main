package io.exzocoin.wallet.modules.market.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.modules.market.*
import io.exzocoin.wallet.modules.market.list.MarketListViewModel
import io.exzocoin.wallet.modules.coin.CoinFragment
import io.exzocoin.core.findNavController
import io.exzocoin.core.helpers.HudHelper
import io.exzocoin.wallet.core.managers.ProjectManager
import io.exzocoin.wallet.modules.transactions.FilterAdapter
import kotlinx.android.synthetic.main.fragment_market_discovery.coinRatesRecyclerView
import kotlinx.android.synthetic.main.fragment_market_home.*
import kotlinx.android.synthetic.main.fragment_market_home.tabLayout

class MarketHomeFragment : BaseFragment(), ViewHolderMarketItem.Listener,
    FilterAdapter.Listener {

    private val vmFactory = MarketHomeDiscoveryModule.Factory()

    private val marketDiscoveryViewModel by viewModels<MarketHomeViewModel> { vmFactory }
    private val marketListViewModel by viewModels<MarketListViewModel> { vmFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_market_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val marketItemsAdapter = MarketItemsAdapter(
                this,
                marketListViewModel.marketViewItemsLiveData,
                marketListViewModel.loadingLiveData,
                marketListViewModel.errorLiveData,
                viewLifecycleOwner
        )
        val marketLoadingAdapter = MarketLoadingAdapter(
                marketListViewModel.loadingLiveData,
                marketListViewModel.errorLiveData,
                marketListViewModel::onErrorClick,
                viewLifecycleOwner
        )

        val filterAdapter = FilterAdapter(this)
        recyclerTags.adapter = filterAdapter

        filterAdapter.setFilters(marketListViewModel.sortingFields.map { FilterAdapter.FilterItem(getString(it.titleResId))
        }, FilterAdapter.FilterItem(getString(marketListViewModel.sortingField.titleResId)))

        coinRatesRecyclerView.adapter = ConcatAdapter(marketLoadingAdapter, marketItemsAdapter)
        coinRatesRecyclerView.itemAnimator = null

        /*pullToRefresh.setOnRefreshListener {
            marketListViewModel.refresh()

            pullToRefresh.isRefreshing = false
        }*/

        marketListViewModel.networkNotAvailable.observe(viewLifecycleOwner, {
            HudHelper.showErrorMessage(requireView(), R.string.Hud_Text_NoInternet)
        })
        ProjectManager.getFeaturedProjects()
            .subscribe({
                val size = it.size
                Log.d("Test", size.toString())
                val marketCategoriesAdapter = MarketFeaturedProjectAdapter(requireContext(), tabLayout, it)
            }, {
                Log.d("Error", it.message.toString())
            })
        ProjectManager.getAds()
            .subscribe({
                val size = it.size
                Log.d("Test", size.toString())
                val marketAdsAdapter = MarketAdsAdapter(requireContext())
                marketAdsAdapter.setItems(it)
                adsSlider.setSliderAdapter(marketAdsAdapter)
            }, {
                Log.d("Error", it.message.toString())
            })
    }

    override fun onItemClick(marketViewItem: MarketViewItem) {
        val arguments = CoinFragment.prepareParams(marketViewItem.coinType, marketViewItem.coinCode, marketViewItem.coinName)

        findNavController().navigate(R.id.coinFragment, arguments, navOptions())
    }

    override fun onFilterItemClick(
        item: FilterAdapter.FilterItem?,
        itemPosition: Int,
        itemWidth: Int
    ) {
        //TODO("Not yet implemented")
        val selectedSortingField = marketListViewModel.sortingFields[itemPosition]
        marketListViewModel.update(sortingField = selectedSortingField)
    }
}
