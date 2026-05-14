package com.hastashilpa.app.viewmodel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ListingState(
    val title: String       = "",
    val description: String = "",
    val price: String       = "",
    val category: String    = "Furniture",
    val showPreview: Boolean = false
)

class MarketplaceViewModel : ViewModel() {

    private val _listing = MutableStateFlow(ListingState())
    val listing: StateFlow<ListingState> = _listing

    fun updateTitle(v: String)       { _listing.value = _listing.value.copy(title = v) }
    fun updateDescription(v: String) { _listing.value = _listing.value.copy(description = v) }
    fun updatePrice(v: String)       { _listing.value = _listing.value.copy(price = v) }
    fun updateCategory(v: String)    { _listing.value = _listing.value.copy(category = v) }

    fun previewListing() {
        if (_listing.value.title.isNotBlank()) {
            _listing.value = _listing.value.copy(showPreview = true)
        }
    }
}