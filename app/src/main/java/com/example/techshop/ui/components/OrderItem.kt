package com.example.techshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.techshop.Model.OrderItemModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Composable
fun OrderItem(item: OrderItemModel) {
    val formatSymbols = DecimalFormatSymbols().apply { groupingSeparator = '.' }
    val formatter = DecimalFormat("#,###", formatSymbols)

    val totalPrice = item.price * item.numberInCart
    val formattedPrice = formatter.format(totalPrice) + " ₫"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${item.title} (x${item.numberInCart})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = formattedPrice,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}