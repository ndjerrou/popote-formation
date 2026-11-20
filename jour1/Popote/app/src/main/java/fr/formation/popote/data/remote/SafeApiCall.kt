package fr.formation.popote.data.remote

import fr.formation.popote.domain.PopoteException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

// region safecall
/** Traduit les exceptions techniques en erreurs métier. */
suspend fun <T> safeApiCall(block: suspend () -> T): T =
    try {
        block()
    } catch (e: CancellationException) {
        throw e                                   // ne JAMAIS avaler l'annulation d'une coroutine
    } catch (e: IOException) {
        throw PopoteException.Network(e)          // pas de réseau, timeout, DNS...
    } catch (e: HttpException) {
        throw PopoteException.Server(e.code())    // 4xx / 5xx
    } catch (e: SerializationException) {
        throw PopoteException.Parsing(e)          // JSON inattendu
    }
// endregion safecall
