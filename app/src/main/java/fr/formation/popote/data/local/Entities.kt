package fr.formation.popote.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// region entities
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val thumbnailUrl: String?,
    val addedAt: Long = System.currentTimeMillis(),
)

/** Clé primaire composite : un même ingrédient peut exister en g ET en pièces. */
@Entity(tableName = "shopping_items", primaryKeys = ["name", "unit"])
data class ShoppingItemEntity(
    val name: String,
    val unit: String,          // "" si pas de quantité
    val amount: Double?,
    val notes: String,         // notes séparées par '\n'
    val checked: Boolean,
)

/** Cache "offline-first" : on garde le JSON brut de la recette. */
@Entity(tableName = "recipe_cache")
data class RecipeCacheEntity(
    @PrimaryKey val id: String,
    val json: String,
    val cachedAt: Long = System.currentTimeMillis(),
)
// endregion entities
