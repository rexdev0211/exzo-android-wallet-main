package io.exzocoin.wallet.entities

sealed class ApiError : Exception() {
    object ApiLimitExceeded : ApiError()
    object ContractNotFound : ApiError()
    object TokenNotFound : ApiError()
    object InvalidResponse : ApiError()
}
