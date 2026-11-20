package fr.formation.popote.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// region daos
@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>        // Flow : Room ré-émet à chaque changement

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY name")
    fun observeAll(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items")
    suspend fun getAll(): List<ShoppingItemEntity>

    @Query("SELECT COUNT(*) FROM shopping_items WHERE checked = 0")
    suspend fun countToBuy(): Int

    @Upsert
    suspend fun upsert(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(items: List<ShoppingItemEntity>)

    @Delete
    suspend fun delete(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE checked = 1")
    suspend fun deleteChecked()

    /** Remplacement atomique : tout réussit ou rien. */
    @Transaction
    suspend fun replaceAll(items: List<ShoppingItemEntity>) {
        deleteAll()
        insertAll(items)
    }
}

@Dao
interface RecipeCacheDao {
    @Query("SELECT * FROM recipe_cache WHERE id = :id")
    suspend fun get(id: String): RecipeCacheEntity?

    @Upsert
    suspend fun upsert(entity: RecipeCacheEntity)
}
// endregion daos
