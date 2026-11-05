package fr.formation.popote.data

import fr.formation.popote.data.local.ShoppingDao
import fr.formation.popote.data.local.ShoppingItemEntity
import fr.formation.popote.domain.MeasureUnit
import fr.formation.popote.domain.Quantity
import fr.formation.popote.domain.Recipe
import fr.formation.popote.domain.ShoppingItem
import fr.formation.popote.domain.ShoppingListBuilder
import fr.formation.popote.domain.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// region roomshopping
class RoomShoppingRepository(private val dao: ShoppingDao) : ShoppingRepository {

    override fun observeItems(): Flow<List<ShoppingItem>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addRecipe(recipe: Recipe) {
        val current = dao.getAll().map { it.toDomain() }
        val merged = ShoppingListBuilder.merge(current, ShoppingListBuilder.build(listOf(recipe)))
        dao.replaceAll(merged.map { it.toEntity() })   // la règle métier reste dans le Domain
    }

    override suspend fun toggle(item: ShoppingItem) =
        dao.upsert(item.copy(checked = !item.checked).toEntity())

    override suspend fun clearChecked() = dao.deleteChecked()
}

// Mappers Entity <-> Domain (fonctions d'extension)
fun ShoppingItemEntity.toDomain() = ShoppingItem(
    name = name,
    quantity = if (amount != null && unit.isNotEmpty()) Quantity(amount, MeasureUnit.valueOf(unit)) else null,
    notes = notes.split('\n').filter { it.isNotBlank() },
    checked = checked,
)

fun ShoppingItem.toEntity() = ShoppingItemEntity(
    name = name,
    unit = quantity?.unit?.name.orEmpty(),
    amount = quantity?.amount,
    notes = notes.joinToString("\n"),
    checked = checked,
)
// endregion roomshopping
