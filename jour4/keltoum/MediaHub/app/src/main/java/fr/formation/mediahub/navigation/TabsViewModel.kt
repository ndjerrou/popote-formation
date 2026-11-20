package fr.formation.mediahub.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// La liste des onglets est un ÉTAT : elle peut changer pendant que l'appli tourne
// (option activée, droits de l'utilisateur, configuration reçue du serveur…).
class TabsViewModel : ViewModel() {
    private val _onglets = MutableStateFlow(listOf(ONGLET_MUSIQUE, ONGLET_FILMS))
    val onglets: StateFlow<List<NavTab>> = _onglets.asStateFlow()

    fun activerPodcast(actif: Boolean) {
        _onglets.update { onglets ->
            when {
                actif && ONGLET_PODCAST !in onglets -> onglets + ONGLET_PODCAST
                !actif -> onglets - ONGLET_PODCAST
                else -> onglets
            }
        }
    }
}
