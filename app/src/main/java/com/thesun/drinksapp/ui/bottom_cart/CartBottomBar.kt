package com.thesun.drinksapp.ui.bottom_cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.utils.Constant

@Composable
fun CartBottomBar(
    viewModel: BottomCartViewModel = hiltViewModel(),
    onClick: () -> Unit
) {
    val drinks by viewModel.cartList.collectAsState()
    val amount by viewModel.totalAmount.collectAsState()

    if (drinks.isEmpty()) return

    val count = drinks.size
    val drinkNames = drinks.joinToString(", ") { it.name.orEmpty() }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ColorPrimaryDark)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text("$count đồ uống", color = Color.White, fontSize = 12.sp)
                Text(
                    text = drinkNames,
                    color = Color.White,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "$amount${Constant.CURRENCY}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_shopping_cart),
                contentDescription = "Cart Icon",
                tint = Color.White
            )
        }
    }
}



