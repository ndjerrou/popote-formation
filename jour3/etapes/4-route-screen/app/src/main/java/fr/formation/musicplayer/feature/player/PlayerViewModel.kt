package fr.formation.musicplayer.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.formation.musicplayer.data.TrackCatalog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// LE CERVEAU de l'écran : il garde l'état et applique les règles. Il ne connaît pas Compose.
class PlayerViewModel : ViewModel() {

    private val tracks = TrackCatalog.tracks

    // Privé et modifiable : seul le ViewModel peut écrire dedans.
    private val _uiState = MutableStateFlow(PlayerUiState(track = tracks.first()))

    // Public et en lecture seule : l'écran peut seulement l'observer.
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // La coroutine qui fait avancer la lecture (null quand on est en pause)
    private var playbackJob: Job? = null

    // LA porte d'entrée unique : toutes les actions de l'écran arrivent ici.
    fun onAction(action: PlayerAction) {
        when (action) {
            PlayerAction.PlayPause -> if (_uiState.value.isPlaying) pause() else play()
            PlayerAction.Next -> changeTrack(+1)
            PlayerAction.Previous -> changeTrack(-1)
            PlayerAction.ToggleFavorite -> toggleFavorite()
            is PlayerAction.SeekTo -> _uiState.update { state ->
                state.copy(positionSec = action.positionSec.coerceIn(0, state.track.durationSec))
            }
        }
    }

    private fun play() {
        _uiState.update { it.copy(isPlaying = true) }
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                tick()
            }
        }
    }

    private fun pause() {
        playbackJob?.cancel()
        playbackJob = null
        _uiState.update { it.copy(isPlaying = false) }
    }

    // Une seconde de lecture : on avance, ou on passe au morceau suivant à la fin
    private fun tick() {
        val state = _uiState.value
        if (state.positionSec < state.track.durationSec) {
            _uiState.update { it.copy(positionSec = it.positionSec + 1) }
        } else {
            changeTrack(+1)
        }
    }

    // +1 = suivant, -1 = précédent ; on boucle aux deux bouts de la playlist
    private fun changeTrack(delta: Int) {
        _uiState.update { state ->
            val index = tracks.indexOf(state.track)
            val newIndex = (index + delta + tracks.size) % tracks.size
            state.copy(track = tracks[newIndex], positionSec = 0)
        }
    }

    private fun toggleFavorite() {
        _uiState.update { state ->
            val id = state.track.id
            val newFavorites = if (id in state.favoriteIds) state.favoriteIds - id else state.favoriteIds + id
            state.copy(favoriteIds = newFavorites)
        }
    }
}
