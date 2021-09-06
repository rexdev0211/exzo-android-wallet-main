package io.exzocoin.wallet.modules.settings.theme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.exzocoin.wallet.core.ILocalStorage
import io.exzocoin.core.SingleLiveEvent
import io.exzocoin.views.ListPosition

class ThemeSwitchViewModel(private val localStorage: ILocalStorage) : ViewModel() {

    val itemsLiveData = MutableLiveData(getItems())
    val changeThemeEvent = SingleLiveEvent<ThemeType>()

    fun onThemeClick(item: ThemeViewItem) {
        if (!item.checked) {
            localStorage.currentTheme = item.themeType
            itemsLiveData.postValue(getItems())
            changeThemeEvent.postValue(item.themeType)
        }
    }

    private fun getItems(): List<ThemeViewItem> {
        val checkedItem = localStorage.currentTheme
        return ThemeType.values().mapIndexed { index, themeType ->
            ThemeViewItem(
                    themeType,
                    themeType == checkedItem,
                    ListPosition.getListPosition(ThemeType.values().size, index)
            )
        }
    }

}
