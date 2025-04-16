package com.thesun.drinksapp.data.model

import android.os.Parcelable
import com.thesun.drinksapp.utils.Constant
import kotlinx.parcelize.Parcelize

@Parcelize
data class Voucher(
    var id: Long = 0,
    var discount: Int = 0,
    var minimum: Int = 0,
    var isSelected: Boolean = false
) : Parcelable {

    val title: String
        get() = "Giảm giá $discount%"

    val minimumText: String
        get() = if (minimum > 0) {
            "Áp dụng cho đơn hàng tối thiểu $minimum${Constant.CURRENCY}"
        } else "Áp dụng cho mọi đơn hàng"

    fun getCondition(amount: Int): String {
        if (minimum <= 0) return ""
        val condition = minimum - amount
        return if (condition > 0) {
            "Hãy mua thêm $condition${Constant.CURRENCY} để nhận được khuyến mại này"
        } else ""
    }

    fun isVoucherEnable(amount: Int): Boolean {
        if (minimum <= 0) return true
        val condition = minimum - amount
        return condition <= 0
    }

    fun getPriceDiscount(amount: Int): Int {
        return amount * discount / 100
    }
}

