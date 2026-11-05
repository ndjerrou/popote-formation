package fr.formation.popote.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// region database
@Database(
    entities = [FavoriteEntity::class, ShoppingItemEntity::class, RecipeCacheEntity::class],
    version = 1,
    exportSchema = true,   // schéma JSON versionné dans app/schemas (utile pour les migrations)
)
abstract class PopoteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun recipeCacheDao(): RecipeCacheDao
}
// endregion database
