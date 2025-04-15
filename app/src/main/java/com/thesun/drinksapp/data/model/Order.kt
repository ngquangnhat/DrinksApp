package com.thesun.drinksapp.data.model

import com.thesun.drinksapp.utils.StringUtil.isEmpty

data class Order(
    var id: Long = 0,
    var userEmail: String? = null,
    var dateTime: String? = null,
    var drinks: List<DrinkOrder>? = null,
    var price: Int = 0,
    var voucher: Int = 0,
    var total: Int = 0,
    var paymentMethod: String? = null,
    var status: Int = 0,
    var rate: Double = 0.0,
    var review: String? = null,
    var address: Address? = null
) {

    val listDrinksName: String
        get() {
            if (drinks == null || drinks!!.isEmpty()) return ""
            var result = ""
            for (drinkOrder in drinks!!) {
                result += if (isEmpty(result)) {
                    drinkOrder.name
                } else {
                    ", " + drinkOrder.name
                }
            }
            return result
        }

    companion object {
        const val STATUS_NEW = 1
        const val STATUS_DOING = 2
        const val STATUS_ARRIVED = 3
        const val STATUS_COMPLETE = 4
    }
}
