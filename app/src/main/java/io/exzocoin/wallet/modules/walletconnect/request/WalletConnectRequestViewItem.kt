package io.exzocoin.wallet.modules.walletconnect.request

sealed class WalletConnectRequestViewItem {

    class To(val value: String) : WalletConnectRequestViewItem()
    class Input(val value: String) : WalletConnectRequestViewItem()

}
