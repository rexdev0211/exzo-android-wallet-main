package io.exzocoin.wallet.modules.coin

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.google.android.material.tabs.TabLayout
import io.exzocoin.wallet.R
import io.exzocoin.wallet.core.App
import io.exzocoin.wallet.core.BaseFragment
import io.exzocoin.wallet.modules.markdown.MarkdownFragment
import io.exzocoin.wallet.modules.metricchart.MetricChartFragment
import io.exzocoin.wallet.modules.metricchart.MetricChartType
import io.exzocoin.wallet.modules.settings.notifications.bottommenu.BottomNotificationMenu
import io.exzocoin.wallet.modules.settings.notifications.bottommenu.NotificationMenuMode
import io.exzocoin.wallet.modules.transactions.transactionInfo.CoinInfoItemView
import io.exzocoin.wallet.ui.extensions.createTextView
import io.exzocoin.wallet.ui.helpers.AppLayoutHelper
import io.exzocoin.chartview.Chart
import io.exzocoin.chartview.models.ChartIndicator
import io.exzocoin.chartview.models.PointInfo
import io.exzocoin.coinkit.models.CoinType
import io.exzocoin.core.findNavController
import io.exzocoin.core.helpers.DateHelper
import io.exzocoin.core.helpers.HudHelper
import io.exzocoin.views.ListPosition
import io.exzocoin.views.SettingsView
import io.exzocoin.views.helpers.LayoutHelper
import io.exzocoin.xrateskit.entities.ChartType
import io.exzocoin.xrateskit.entities.CoinCategory
import io.exzocoin.xrateskit.entities.CoinMeta
import io.exzocoin.xrateskit.entities.LinkType
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.spans.LastLineSpacingSpan
import kotlinx.android.synthetic.main.coin_market_details.*
import kotlinx.android.synthetic.main.fragment_coin.*
import org.commonmark.node.Heading
import org.commonmark.node.Paragraph
import java.math.BigDecimal
import java.util.*

class CoinFragment : BaseFragment(), Chart.Listener, TabLayout.OnTabSelectedListener {

    private val coinTitle by lazy {
        requireArguments().getString(COIN_TITLE_KEY) ?: ""
    }
    private val coinCode by lazy {
        requireArguments().getString(COIN_CODE_KEY) ?: ""
    }
    private val vmFactory by lazy {
        CoinModule.Factory(
                coinTitle,
                requireArguments().getParcelable(COIN_TYPE_KEY)!!,
                coinCode
        )
    }

    private val viewModel by navGraphViewModels<CoinViewModel>(R.id.coinFragment) { vmFactory }

    private val formatter = App.numberFormatter
    private var notificationMenuItem: MenuItem? = null
    private val actions = listOf(
            Pair(ChartType.TODAY, R.string.CoinPage_TimeDuration_Today),
            Pair(ChartType.DAILY, R.string.CoinPage_TimeDuration_Day),
            Pair(ChartType.WEEKLY, R.string.CoinPage_TimeDuration_Week),
            Pair(ChartType.WEEKLY2, R.string.CoinPage_TimeDuration_TwoWeeks),
            Pair(ChartType.MONTHLY, R.string.CoinPage_TimeDuration_Month),
            Pair(ChartType.MONTHLY3, R.string.CoinPage_TimeDuration_Month3),
            Pair(ChartType.MONTHLY6, R.string.CoinPage_TimeDuration_HalfYear),
            Pair(ChartType.MONTHLY12, R.string.CoinPage_TimeDuration_Year),
            Pair(ChartType.MONTHLY24, R.string.CoinPage_TimeDuration_Year2)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_coin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = coinCode
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuFavorite -> {
                    viewModel.onFavoriteClick()
                    true
                }
                R.id.menuUnfavorite -> {
                    viewModel.onUnfavoriteClick()
                    true
                }
                R.id.menuNotification -> {
                    viewModel.onNotificationClick()
                    true
                }
                else -> false
            }
        }
        notificationMenuItem = toolbar.menu.findItem(R.id.menuNotification)
        updateNotificationMenuItem()

        coinName.text = coinTitle
        val drawableResId = context?.let { AppLayoutHelper.getCoinDrawableResId(it, viewModel.coinType) }
                ?: R.drawable.place_holder
        coinIcon.setImageResource(drawableResId)

        chart.setListener(this)

        observeData()
        bindActions()

        activity?.onBackPressedDispatcher?.addCallback(this) {
            findNavController().popBackStack()
        }

        aboutTextToggle.setOnClickListener {
            if (aboutText.maxLines == Integer.MAX_VALUE) {
                aboutText.maxLines = ABOUT_MAX_LINES
                aboutTextToggle.text = getString(R.string.CoinPage_ReadMore)
            } else {
                aboutText.maxLines = Integer.MAX_VALUE
                aboutTextToggle.text = getString(R.string.CoinPage_ReadLess)
            }
        }
    }

    private fun updateNotificationMenuItem() {
        notificationMenuItem?.apply {
            isVisible = viewModel.notificationIconVisible
            icon = context?.let {
                val iconRes = if (viewModel.notificationIconActive) R.drawable.ic_notification_24 else R.drawable.ic_notification_disabled
                ContextCompat.getDrawable(it, iconRes)
            }
        }
    }

    //  ChartView Listener

    override fun onTouchDown() {
        scroller.setScrollingEnabled(false)

        setViewVisibility(chartPointsInfo, isVisible = true)
        setViewVisibility(tabLayout, isVisible = false)
    }

    override fun onTouchUp() {
        scroller.setScrollingEnabled(true)

        setViewVisibility(chartPointsInfo, isVisible = false)
        setViewVisibility(tabLayout, isVisible = true)
    }

    override fun onTouchSelect(point: PointInfo) {
        viewModel.onTouchSelect(point)
        HudHelper.vibrate(requireContext())
    }

    //  TabLayout.OnTabSelectedListener

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewModel.onSelect(actions[tab.position].first)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
    }

    //  Private

    private fun observeData() {
        viewModel.chartSpinner.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                chart.showSinner()
            } else {
                chart.hideSinner()
            }
        })

        viewModel.marketSpinner.observe(viewLifecycleOwner, Observer { isLoading ->
            marketSpinner.isVisible = isLoading
        })

        viewModel.setDefaultMode.observe(viewLifecycleOwner, Observer { type ->
            val indexOf = actions.indexOfFirst { it.first == type }
            if (indexOf > -1) {
                tabLayout.removeOnTabSelectedListener(this)
                tabLayout.selectTab(tabLayout.getTabAt(indexOf))
                tabLayout.addOnTabSelectedListener(this)
            }
        })

        viewModel.showChartInfo.observe(viewLifecycleOwner, Observer { item ->
            chart.showChart()
            setViewVisibility(indicatorEMA, indicatorMACD, indicatorRSI, isVisible = true)

            rootView.post {
                chart.setData(item.chartData, item.chartType, item.maxValue, item.minValue)
            }

            coinRateDiff.setDiff(item.diffValue)
        })

        viewModel.latestRateLiveData.observe(viewLifecycleOwner) {
            coinRateLast.text = formatter.formatFiat(it.value, it.currency.symbol, 2, 4)
        }

        viewModel.coinDetailsLiveData.observe(viewLifecycleOwner, Observer { item ->
            marketDetails.isVisible = true

            // Coin Rating
            coinRating.isVisible = !item.coinMeta.rating.isNullOrBlank()
            coinRating.setImageDrawable(getRatingIcon(item.coinMeta.rating))
            coinRating.isEnabled = false

            // Performance

            setCoinPerformance(item)

            // Market

            setMarketData(item.marketDataList)

            // TVL

            setTvlData(item.tvlInfo)


            // About

            if (item.coinMeta.description.isNotBlank()) {
                aboutGroup.isVisible = true
                aboutTitle.isVisible = item.coinMeta.descriptionType == CoinMeta.DescriptionType.HTML
                val aboutTextSpanned = when (item.coinMeta.descriptionType) {
                    CoinMeta.DescriptionType.HTML -> {
                        Html.fromHtml(item.coinMeta.description.replace("\n", "<br />"), Html.FROM_HTML_MODE_COMPACT)
                    }
                    CoinMeta.DescriptionType.MARKDOWN -> {
                        val markwon = Markwon.builder(requireContext())
                                .usePlugin(object : AbstractMarkwonPlugin() {

                                    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                                        builder.setFactory(Heading::class.java) { configuration, props ->
                                            arrayOf(
                                                    TextAppearanceSpan(context, R.style.Headline2),
                                                    ForegroundColorSpan(resources.getColor(R.color.bran, null))
                                            )
                                        }
                                        builder.setFactory(Paragraph::class.java) { configuration, props ->
                                            arrayOf(
                                                    LastLineSpacingSpan(LayoutHelper.dp(24f, requireContext())),
                                                    TextAppearanceSpan(context, R.style.Subhead2),
                                                    ForegroundColorSpan(resources.getColor(R.color.grey, null))
                                            )
                                        }
                                    }
                                })
                                .build()

                        markwon.toMarkdown(item.coinMeta.description)
                    }
                }

                aboutText.text = removeLinkSpans(aboutTextSpanned)
                aboutText.maxLines = Integer.MAX_VALUE
                aboutText.isVisible = false
                aboutText.doOnPreDraw {
                    if (aboutText.lineCount > ABOUT_MAX_LINES + ABOUT_TOGGLE_LINES) {
                        aboutText.maxLines = ABOUT_MAX_LINES
                        aboutTextToggle.isVisible = true
                    } else {
                        aboutTextToggle.isVisible = false
                    }
                }
                aboutText.isVisible = true
            } else {
                aboutGroup.isVisible = false
            }
            // Categories/Platforms/Links
            setCategoriesAndContractInfo(item.coinMeta.categories, item.contractInfo)

            //Links
            setLinks(item.coinMeta.links, item.guideUrl)
        })

        viewModel.setSelectedPoint.observe(viewLifecycleOwner, Observer { item ->
            pointInfoVolume.isInvisible = true
            pointInfoVolumeTitle.isInvisible = true

            macdHistogram.isInvisible = true
            macdSignal.isInvisible = true
            macdValue.isInvisible = true

            pointInfoDate.text = DateHelper.getDayAndTime(Date(item.date * 1000))
            pointInfoPrice.text = formatter.formatFiat(item.price.value, item.price.currency.symbol, 2, 4)

            item.volume?.let {
                pointInfoVolumeTitle.isVisible = true
                pointInfoVolume.isVisible = true
                pointInfoVolume.text = formatFiatShortened(item.volume.value, item.volume.currency.symbol)
            }

            item.macdInfo?.let { macdInfo ->
                macdInfo.histogram?.let {
                    macdHistogram.isVisible = true
                    getHistogramColor(it)?.let { it1 -> macdHistogram.setTextColor(it1) }
                    macdHistogram.text = formatter.format(it, 0, 2)
                }
                macdInfo.signal?.let {
                    macdSignal.isVisible = true
                    macdSignal.text = formatter.format(it, 0, 2)
                }
                macdInfo.macd?.let {
                    macdValue.isVisible = true
                    macdValue.text = formatter.format(it, 0, 2)
                }
            }
        })

        viewModel.showChartError.observe(viewLifecycleOwner, Observer {
            chart.showError(getString(R.string.CoinPage_Error_NotAvailable))
        })

        viewModel.showEma.observe(viewLifecycleOwner, Observer { visible ->
            chart.setIndicator(ChartIndicator.Ema, visible)
        })

        viewModel.showMacd.observe(viewLifecycleOwner, Observer { visible ->
            chart.setIndicator(ChartIndicator.Macd, visible)
            setMacdInfoVisible(visible)
        })

        viewModel.showRsi.observe(viewLifecycleOwner, Observer { visible ->
            chart.setIndicator(ChartIndicator.Rsi, visible)
        })

        viewModel.uncheckIndicators.observe(viewLifecycleOwner, { indicators ->
            indicators.forEach {
                when (it) {
                    ChartIndicator.Ema -> indicatorEMA.isChecked = false
                    ChartIndicator.Macd -> {
                        indicatorMACD.isChecked = false
                        setMacdInfoVisible(false)
                    }
                    ChartIndicator.Rsi -> indicatorRSI.isChecked = false
                }
            }
        })

        viewModel.alertNotificationUpdated.observe(viewLifecycleOwner, Observer {
            updateNotificationMenuItem()
        })

        viewModel.showNotificationMenu.observe(viewLifecycleOwner, Observer { (coinType, coinName) ->
            BottomNotificationMenu.show(childFragmentManager, NotificationMenuMode.All, coinName, coinType)
        })

        viewModel.isFavorite.observe(viewLifecycleOwner, Observer {
            toolbar.menu.findItem(R.id.menuFavorite).isVisible = !it
            toolbar.menu.findItem(R.id.menuUnfavorite).isVisible = it
        })

        viewModel.extraPages.observe(viewLifecycleOwner, { pages ->
            setExtraPages(pages)
        })

        viewModel.setChartIndicatorsEnabled.observe(viewLifecycleOwner, { enabled ->
            indicatorEMA.isEnabled = enabled
            indicatorMACD.isEnabled = enabled
            indicatorRSI.isEnabled = enabled
        })
    }

    private fun setCoinPerformance(item: CoinDetailsViewItem) {
        if (item.rateDiffs.isEmpty()) {
            return
        }
        context?.let { ctx ->
            item.rateDiffs.forEachIndexed { index, rowViewItem ->
                val row = when (rowViewItem) {
                    is RoiViewItem.HeaderRowViewItem -> {
                        CoinPerformanceRowView(ctx).apply {
                            bindHeader(rowViewItem.title, rowViewItem.periods)
                        }
                    }
                    is RoiViewItem.RowViewItem -> {
                        CoinPerformanceRowView(ctx).apply {
                            bind(rowViewItem.title, rowViewItem.values, item.rateDiffs.size - 1, index)
                        }
                    }
                }
                coinPerformanceLayout.addView(row)
            }

        }
    }

    private fun setMacdInfoVisible(visible: Boolean) {
        setViewVisibility(pointInfoVolume, pointInfoVolumeTitle, isVisible = !visible)
        setViewVisibility(macdSignal, macdHistogram, macdValue, isVisible = visible)
    }

    private fun setExtraPages(pages: List<CoinExtraPage>) {
        extraPagesLayout.removeAllViews()

        context?.let { context ->
            pages.forEach { item ->
                val coinInfoView = CoinInfoItemView(context).apply {
                    when (item) {
                        is CoinExtraPage.TradingVolume -> {
                            bind(
                                    title = getString(R.string.CoinPage_TradingVolume),
                                    value = item.value,
                                    listPosition = item.position,
                                    icon = if (item.canShowMarkets) R.drawable.ic_arrow_right else null
                            )
                            if (item.canShowMarkets) {
                                setOnClickListener {
                                    findNavController().navigate(R.id.coinFragment_to_coinMarketsFragment, null, navOptions())
                                }
                            }
                        }
                        is CoinExtraPage.Investors -> {
                            bind(
                                    title = getString(R.string.CoinPage_FundsInvested),
                                    listPosition = item.position,
                                    icon = R.drawable.ic_arrow_right
                            )
                            setOnClickListener {
                                findNavController().navigate(R.id.coinFragment_to_coinInvestorsFragment, null, navOptions())
                            }
                        }
                    }
                }

                extraPagesLayout.addView(coinInfoView)
            }
        }
    }

    private fun getRatingIcon(rating: String?): Drawable? {
        val icon = when (rating?.toLowerCase(Locale.ENGLISH)) {
            "a" -> R.drawable.ic_rating_a
            "b" -> R.drawable.ic_rating_b
            "c" -> R.drawable.ic_rating_c
            "d" -> R.drawable.ic_rating_d
            else -> return null
        }
        return context?.let { ContextCompat.getDrawable(it, icon) }
    }

    private fun setMarketData(marketDataList: List<CoinDataItem>) {
        marketDataLayout.removeAllViews()

        context?.let { context ->
            marketDataList.forEachIndexed { index, marketData ->
                val coinInfoView = CoinInfoItemView(context).apply {
                    bind(
                            title = getString(marketData.title),
                            value = marketData.value,
                            listPosition = ListPosition.getListPosition(marketDataList.size, index)
                    )
                }

                marketDataLayout.addView(coinInfoView)
            }
        }
    }

    private fun setTvlData(tvlDataList: List<CoinDataItem>) {
        tvlLayout.removeAllViews()

        context?.let { context ->
            tvlDataList.forEachIndexed { index, marketData ->
                val view = CoinInfoItemView(context).apply {
                    bind(
                            title = getString(marketData.title),
                            value = marketData.value,
                            listPosition = ListPosition.getListPosition(tvlDataList.size, index),
                            icon = marketData.icon
                    )
                }

                if (index == 0) {
                    view.setOnClickListener {
                        MetricChartFragment.show(childFragmentManager, MetricChartType.Coin(viewModel.coinType))
                    }
                }

                tvlLayout.addView(view)
            }
        }
    }

    private fun setLinks(links: Map<LinkType, String>, guideUrl: String?) {
        context?.let { context ->
            linksLayout.removeAllViews()

            guideUrl?.let {
                val link = SettingsView(context)
                link.showTitle(getString(R.string.CoinPage_Guide))
                link.showIcon(ContextCompat.getDrawable(context, R.drawable.ic_academy_20))
                link.setListPosition(ListPosition.getListPosition(links.size + 1, 0))
                link.setOnClickListener {
                    val arguments = bundleOf(
                            MarkdownFragment.markdownUrlKey to guideUrl,
                            MarkdownFragment.handleRelativeUrlKey to true
                    )
                    findNavController().navigate(R.id.coinFragment_to_markdownFragment, arguments, navOptions())
                }
                linksLayout.addView(link)
            }

            links.onEachIndexed { index, entry ->
                val link = SettingsView(context)
                when (entry.key) {
                    LinkType.WEBSITE -> {
                        link.showTitle(getString(R.string.CoinPage_Website))
                        link.showIcon(ContextCompat.getDrawable(context, R.drawable.ic_globe))
                    }
                    LinkType.WHITEPAPER -> {
                        link.showTitle(getString(R.string.CoinPage_Whitepaper))
                        link.showIcon(ContextCompat.getDrawable(context, R.drawable.ic_clipboard))
                    }
                    LinkType.TWITTER -> {
                        link.showTitle(getString(R.string.CoinPage_Twitter))
                        link.showIcon(ContextCompat.getDrawable(context, R.drawable.ic_twitter))
                    }
                    LinkType.TELEGRAM -> {
                        link.showTitle(getString(R.string.CoinPage_Telegram))
                        link.showIcon(ContextCompat.getDrawable(context, R.drawable.ic_telegram))
                    }
                    LinkType.REDDIT -> {
                        link.showTitle(getString(R.string.CoinPage_Reddit))
                        link.showIcon(ContextCompat.getDrawable(context, R.drawable.ic_reddit))
                    }
                    LinkType.GITHUB -> {
                        link.showTitle(getString(R.string.CoinPage_Github))
                        link.showIcon(ContextCompat.getDrawable(context, R.drawable.ic_github))
                    }
                }
                val shiftPosition = if (guideUrl != null) 1 else 0
                link.setListPosition(ListPosition.getListPosition(links.size + shiftPosition, index + shiftPosition))
                link.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(entry.value.trim())))
                }

                linksLayout.addView(link)
            }
        }
    }

    private fun setCategoriesAndContractInfo(categories: List<CoinCategory>, contractInfo: ContractInfo?) {
        categoriesText.text = categories.joinToString(", ") { it.name }
        categoriesGroup.isVisible = categories.isNotEmpty()

        context?.let { context ->
            platformsLayout.removeAllViews()

            contractInfo?.let {
                val platformView = CoinInfoItemView(context).apply {
                    bind(
                            title = contractInfo.title,
                            decoratedValue = contractInfo.value,
                            listPosition = ListPosition.Single
                    )
                }

                platformsLayout.addView(platformView)
            }
        }
    }

    private fun removeLinkSpans(spanned: Spanned): Spannable {
        val spannable = SpannableString(spanned)
        for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
            spannable.removeSpan(u)
        }
        return spannable
    }

    private fun formatFiatShortened(value: BigDecimal, symbol: String): String {
        val shortCapValue = App.numberFormatter.shortenValue(value)
        return formatter.formatFiat(shortCapValue.first, symbol, 0, 2) + " " + shortCapValue.second
    }

    private fun getHistogramColor(value: Float): Int? {
        val textColor = if (value > 0) R.color.green_d else R.color.red_d
        return context?.getColor(textColor)
    }

    private fun bindActions() {
        actions.forEach { (_, textId) ->
            tabLayout.newTab()
                    .setCustomView(createTextView(requireContext(), R.style.TabComponent).apply {
                        id = android.R.id.text1
                    })
                    .setText(getString(textId))
                    .let {
                        tabLayout.addTab(it, false)
                    }
        }

        tabLayout.tabRippleColor = null
        tabLayout.setSelectedTabIndicator(null)
        tabLayout.addOnTabSelectedListener(this)

        indicatorEMA.setOnClickListener {
            viewModel.setIndicatorChanged(ChartIndicator.Ema, indicatorEMA.isChecked)
        }

        indicatorMACD.setOnClickListener {
            viewModel.setIndicatorChanged(ChartIndicator.Macd, indicatorMACD.isChecked)
        }

        indicatorRSI.setOnClickListener {
            viewModel.setIndicatorChanged(ChartIndicator.Rsi, indicatorRSI.isChecked)
        }

    }

    private fun setViewVisibility(vararg views: View, isVisible: Boolean) {
        views.forEach { it.isInvisible = !isVisible }
    }

    companion object {
        private const val ABOUT_MAX_LINES = 8
        private const val ABOUT_TOGGLE_LINES = 2
        private const val COIN_TYPE_KEY = "coin_type_key"
        private const val COIN_CODE_KEY = "coin_code_key"
        private const val COIN_TITLE_KEY = "coin_title_key"

        fun prepareParams(coinType: CoinType, coinCode: String, coinTitle: String): Bundle {
            return bundleOf(
                    COIN_TYPE_KEY to coinType,
                    COIN_CODE_KEY to coinCode,
                    COIN_TITLE_KEY to coinTitle
            )
        }
    }
}
