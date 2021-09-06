package io.exzocoin.wallet.modules.settings.faq

import io.exzocoin.wallet.core.managers.ConnectivityManager
import io.exzocoin.wallet.core.managers.FaqManager
import io.exzocoin.wallet.entities.Faq
import io.exzocoin.core.ILanguageManager
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class FaqRepository(
        private val faqManager: FaqManager,
        private val connectivityManager: ConnectivityManager,
        private val languageManager: ILanguageManager) {

    val faqList: Observable<DataState<Array<Faq>>>
        get() = faqListSubject

    private val faqListSubject = BehaviorSubject.create<DataState<Array<Faq>>>()
    private val disposables = CompositeDisposable()
    private val retryLimit = 3

    init {
        fetch()

        connectivityManager.networkAvailabilitySignal
                .subscribeOn(Schedulers.io())
                .subscribe {
                    if (connectivityManager.isConnected && faqListSubject.value is DataState.Error) {
                        fetch()
                    }
                }
                .let {
                    disposables.add(it)
                }
    }

    fun clear() {
        disposables.clear()
    }

    private fun fetch() {
        faqListSubject.onNext(DataState.Loading())

        faqManager.getFaqList()
                //retry on error java.lang.AssertionError: No System TLS
                .retryWhen { errors ->
                    errors.zipWith(
                            Flowable.range(1, retryLimit + 1),
                            BiFunction<Throwable, Int, Int> { error: Throwable, retryCount: Int ->
                                if (retryCount < retryLimit && (error is AssertionError)) {
                                    retryCount
                                } else {
                                    throw error
                                }
                            }
                    ).flatMap {
                        Flowable.timer(1, TimeUnit.SECONDS)
                    }
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val faqListByLanguage = getFaqListByLocalLanguage(it, languageManager.currentLocale.language, languageManager.fallbackLocale.language)
                    faqListSubject.onNext(DataState.Success(faqListByLanguage))
                }, {
                    faqListSubject.onNext(DataState.Error(it))
                })
                .let {
                    disposables.add(it)
                }
    }

    private fun getFaqListByLocalLanguage(faqListMultiLanguage: List<HashMap<String, Faq>>, language: String, fallbackLanguage: String): Array<Faq> {
        val faqs = faqListMultiLanguage.mapNotNull { faqListMultiLang ->
            faqListMultiLang[language] ?: faqListMultiLang[fallbackLanguage]
        }

        return faqs.toTypedArray()
    }
}
