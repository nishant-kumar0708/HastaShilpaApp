package com.hastashilpa.app.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hastashilpa.app.ui.screens.PriceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PricerViewModel : ViewModel() {

    private val _result = MutableStateFlow<PriceResult?>(null)
    val result: StateFlow<PriceResult?> = _result

    var materialCost = MutableStateFlow("")
    var hoursWorked  = MutableStateFlow("")
    var overhead     = MutableStateFlow("20")

    fun calculate() {
        viewModelScope.launch {
            val mat   = materialCost.value.toDoubleOrNull() ?: 0.0
            val hrs   = hoursWorked.value.toDoubleOrNull()  ?: 0.0
            val ovh   = (overhead.value.toDoubleOrNull()    ?: 20.0) / 100.0
            val lab   = hrs * 80.0
            val base  = mat + lab
            val price = base * (1.0 + ovh)
            _result.value = PriceResult(
                material       = mat,
                labour         = lab,
                base           = base,
                margin         = price - base,
                suggestedPrice = price,
                overheadPct    = ovh * 100
            )
        }
    }

    fun reset() { _result.value = null }
}