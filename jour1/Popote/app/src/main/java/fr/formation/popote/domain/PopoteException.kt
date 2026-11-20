package fr.formation.popote.domain

// region errors
/** Erreurs "métier" : l'UI n'a pas à connaître IOException ou HttpException. */
sealed class PopoteException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class Network(cause: Throwable? = null) : PopoteException("Pas de connexion internet", cause)
    class Server(val code: Int) : PopoteException("Le serveur a répondu $code")
    class NotFound(what: String) : PopoteException("$what introuvable")
    class Parsing(cause: Throwable) : PopoteException("Réponse illisible", cause)
}
// endregion errors
