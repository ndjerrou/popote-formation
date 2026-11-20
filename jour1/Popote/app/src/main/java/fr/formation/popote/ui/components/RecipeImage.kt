package fr.formation.popote.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.formation.popote.R

/** Image distante chargée par Coil (permission INTERNET requise dans le Manifest). */
@Composable
fun RecipeImage(url: String?, modifier: Modifier = Modifier, height: Dp = 180.dp) {
    AsyncImage(
        model = url,
        contentDescription = null,
        placeholder = painterResource(R.drawable.ic_restaurant),
        error = painterResource(R.drawable.ic_restaurant),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp)),
    )
}
