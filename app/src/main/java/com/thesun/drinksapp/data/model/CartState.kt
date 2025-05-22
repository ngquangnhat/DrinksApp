package com.thesun.drinksapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_state")
data class CartState(
    @PrimaryKey val orderId: Long,
    @ColumnInfo(name = "items") val items: List<Drink>,
    @ColumnInfo(name = "payment_method") val paymentMethod: PaymentMethod?,
    @ColumnInfo(name = "address") val address: Address?,
    @ColumnInfo(name = "voucher") val voucher: Voucher?
)