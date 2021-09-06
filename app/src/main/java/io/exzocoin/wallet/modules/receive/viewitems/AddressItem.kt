package io.exzocoin.wallet.modules.receive.viewitems

import io.exzocoin.coinkit.models.Coin

data class AddressItem(var address: String, var addressType: String?,  var coin: Coin)
