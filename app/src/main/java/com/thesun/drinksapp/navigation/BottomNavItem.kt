package com.thesun.drinksapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.thesun.drinksapp.R

sealed class BottomNavItem(val route: String, val label: String, val icon: Int) {
    data object Home : BottomNavItem("home_tab", "Trang chủ", R.drawable.ic_home)
    data object Category : BottomNavItem("category_tab", "Lịch sử", R.drawable.ic_history)
    data object Profile : BottomNavItem("profile_tab", "Tài khoản", R.drawable.ic_profile)
}
sealed class AdminNavItem(val route: String, val icon: Int, val label: String) {
    data object Category : AdminNavItem("categories", R.drawable.ic_nav_category, "Thể loại")
    data object Drink : AdminNavItem("drinks", R.drawable.ic_nav_drink, "Đồ uống")
    data object Order : AdminNavItem("orders", R.drawable.ic_nav_order, "Đơn hàng")
    data object Settings : AdminNavItem("settings", R.drawable.ic_nav_setting, "Cài đặt")
}
