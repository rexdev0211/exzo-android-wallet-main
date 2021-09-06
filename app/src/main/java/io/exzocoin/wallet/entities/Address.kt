package io.exzocoin.wallet.entities

class Address(val hex: String, val domain: String? = null) {
    val title: String
        get() = domain ?: hex
}
