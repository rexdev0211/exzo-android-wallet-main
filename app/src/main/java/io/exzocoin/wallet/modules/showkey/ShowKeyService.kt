package io.exzocoin.wallet.modules.showkey

import io.exzocoin.wallet.core.managers.EthereumKitManager
import io.exzocoin.wallet.entities.Account
import io.exzocoin.wallet.entities.AccountType
import io.exzocoin.core.IPinComponent
import io.exzocoin.core.toHexString
import io.horizontalsystems.ethereumkit.core.EthereumKit

class ShowKeyService(
        account: Account,
        private val pinComponent: IPinComponent,
        private val ethereumKitManager: EthereumKitManager
) {
    val words: List<String>
    val passphrase: String

    init {
        if (account.type is AccountType.Mnemonic) {
            words = account.type.words
            passphrase = account.type.passphrase ?: ""
        } else {
            words = listOf()
            passphrase = ""
        }
    }

    val isPinSet: Boolean
        get() = pinComponent.isPinSet

    val evmPrivateKey: String
        get() = EthereumKit.privateKey(words, passphrase, ethereumKitManager.networkType).toByteArray().toHexString()

}
