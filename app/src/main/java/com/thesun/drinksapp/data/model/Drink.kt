package com.thesun.drinksapp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.PropertyName
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Entity(tableName = "drink")
data class Drink(
    @PrimaryKey
    var id: Long = 0,
    var name: String? = null,
    var description: String? = null,
    var price: Int = 0,
    var image: String? = null,
    var banner: String? = null,
    @get:PropertyName("category_id")
    @set:PropertyName("category_id")
    var categoryId: Long = 0,
    @get:PropertyName("category_name")
    @set:PropertyName("category_name")
    var categoryName: String? = null,
    var sale: Int = 0,
    var isFeatured: Boolean = false,
    @Ignore
    var rating: HashMap<String, Rating>? = null,
    var count: Int = 0,
    var totalPrice: Int = 0,
    var priceOneDrink: Int = 0,
    var option: String? = null,
    var variant: String? = null,
    var size: String? = null,
    var sugar: String? = null,
    var ice: String? = null,
    var toppingIds: String? = null,
    var note: String? = null
) {

    val realPrice: Int
        get() = if (sale <= 0) price else price - price * sale / 100

    val countReviews: Int
        get() = rating?.size ?: 0

    val rate: Double
        get() {
            if (rating == null || rating!!.isEmpty()) return 0.0
            var sum = 0.0
            for (ratingEntity in rating!!.values) {
                sum += ratingEntity.rate
            }
            val symbols = DecimalFormatSymbols()
            symbols.decimalSeparator = '.'
            val formatter = DecimalFormat("#.#")
            formatter.decimalFormatSymbols = symbols
            return formatter.format(sum / rating!!.size).toDouble()
        }

}

