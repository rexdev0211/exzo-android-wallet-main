package io.exzocoin.wallet.modules.coin.coinmarkets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.modules.coin.CoinViewModel
import io.exzocoin.core.findNavController
import kotlinx.android.synthetic.main.fragment_coin_markets.*

class CoinMarketsFragment : BaseFragment() {

    private val coinViewModel by navGraphViewModels<CoinViewModel>(R.id.coinFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_coin_markets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = getString(R.string.CoinMarket_Title, coinViewModel.coinCode)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val marketItemsAdapter = CoinMarketItemAdapter()

        recyclerView.adapter = marketItemsAdapter

        coinViewModel.coinMarkets.observe(viewLifecycleOwner, {
            marketItemsAdapter.submitList(it)
        })
    }

}
