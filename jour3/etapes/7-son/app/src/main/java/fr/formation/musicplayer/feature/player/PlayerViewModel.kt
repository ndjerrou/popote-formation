package fr.formation.musicplayer.feature.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.formation.musicplayer.data.AudioPlayer
import fr.formation.musicplayer.data.MediaAudioPlayer
import fr.formation.musicplayer.data.TrackCatalog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// LE CERVEAU de l'écran : il garde l'état et applique les règles. Il ne connaît pas Compose.
// SavedStateHandle : un petit carnet qu'Android sauvegarde pour nous si le système tue l'appli.
// AudioPlayer : le haut-parleur. Le ViewModel ne sait pas lequel : on le lui donne (injection).
class PlayerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val audioPlayer: AudioPlayer,
) : ViewModel() {

    private val tracks = TrackCatalog.tracks

    // L'argument de navigation : la Navigation l'a rangé dans le même carnet (SavedStateHandle),
    // sous le nom de la propriété. PlayerDestination(trackId = 4) -> savedStateHandle["trackId"] == 4
    private val startTrackId: Int = savedStateHandle[KEY_NAV_TRACK_ID] ?: TrackCatalog.tracks.first().id

    // Privé et modifiable : seul le ViewModel peut écrire dedans.
    // Au démarrage, on relit le carnet : le morceau sauvegardé s'il existe (mort du processus),
    // sinon le morceau demandé par la navigation.
    private val _uiState = MutableStateFlow(
        PlayerUiState(
            track = TrackCatalog.byId(savedStateHandle[KEY_TRACK_ID] ?: startTrackId),
            positionSec = savedStateHandle[KEY_POSITION] ?: 0,
        )
    )

    // Public et en lecture seule : l'écran peut seulement l'observer.
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // La coroutine qui fait avancer la lecture (null quand on est en pause)
    private var playbackJob: Job? = null

    init {
        // Le son démarre là où l'état démarre (début, ou position restaurée)
        audioPlayer.load(_uiState.value.track)
        audioPlayer.seekTo(_uiState.value.positionSec)

        // À chaque nouvel état, on recopie dans le carnet ce qui doit survivre.
        // isPlaying n'est pas sauvegardé : après une mort du processus, on reprend en pause.
        viewModelScope.launch {
            uiState.collect { state ->
                savedStateHandle[KEY_TRACK_ID] = state.track.id
                savedStateHandle[KEY_POSITION] = state.positionSec
            }
        }
    }

    // LA porte d'entrée unique : toutes les actions de l'écran arrivent ici.
    fun onAction(action: PlayerAction) {
        when (action) {
            PlayerAction.PlayPause -> if (_uiState.value.isPlaying) pause() else play()
            PlayerAction.Next -> changeTrack(+1)
            PlayerAction.Previous -> changeTrack(-1)
            PlayerAction.ToggleFavorite -> toggleFavorite()
            is PlayerAction.SeekTo -> {
                _uiState.update { state ->
                    state.copy(positionSec = action.positionSec.coerceIn(0, state.track.durationSec))
                }
                audioPlayer.seekTo(_uiState.value.positionSec)
            }
        }
    }

    private fun play() {
        _uiState.update { it.copy(isPlaying = true) }
        audioPlayer.play()
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
        audioPlayer.pause()
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
        audioPlayer.load(_uiState.value.track)
        if (_uiState.value.isPlaying) audioPlayer.play()
    }

    private fun toggleFavorite() {
        _uiState.update { state ->
            val id = state.track.id
            val newFavorites = if (id in state.favoriteIds) state.favoriteIds - id else state.favoriteIds + id
            state.copy(favoriteIds = newFavorites)
        }
    }

    // Appelé quand l'écran quitte définitivement la pile de navigation : on rend le haut-parleur
    override fun onCleared() {
        audioPlayer.release()
    }

    companion object {
        // LA FABRIQUE : explique à Android comment construire ce ViewModel avec ses deux paramètres
        val Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                PlayerViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    audioPlayer = MediaAudioPlayer(application),
                )
            }
        }

        // Les clés du carnet : des constantes, pour ne jamais faire de faute de frappe
        private const val KEY_NAV_TRACK_ID = "trackId"          // = le nom de la propriété de PlayerDestination
        private const val KEY_TRACK_ID = "currentTrackId"
        private const val KEY_POSITION = "positionSec"
    }
}
