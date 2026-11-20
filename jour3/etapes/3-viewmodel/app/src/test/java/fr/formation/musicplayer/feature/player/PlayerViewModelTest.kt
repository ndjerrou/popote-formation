package fr.formation.musicplayer.feature.player

import fr.formation.musicplayer.data.TrackCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    // viewModelScope tourne sur le thread principal d'Android, absent dans un test JVM :
    // on le remplace par un "faux" thread principal dont on contrôle le temps.
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(testDispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val tracks = TrackCatalog.tracks

    @Test
    fun `au départ, le premier morceau est en pause au début`() {
        val state = PlayerViewModel().uiState.value
        assertEquals(tracks.first(), state.track)
        assertFalse(state.isPlaying)
        assertEquals(0, state.positionSec)
    }

    @Test
    fun `Next puis Previous revient au premier morceau`() {
        val viewModel = PlayerViewModel()
        viewModel.onAction(PlayerAction.Next)
        assertEquals(tracks[1], viewModel.uiState.value.track)
        viewModel.onAction(PlayerAction.Previous)
        assertEquals(tracks[0], viewModel.uiState.value.track)
    }

    @Test
    fun `Previous sur le premier morceau boucle sur le dernier`() {
        val viewModel = PlayerViewModel()
        viewModel.onAction(PlayerAction.Previous)
        assertEquals(tracks.last(), viewModel.uiState.value.track)
    }

    @Test
    fun `ToggleFavorite ajoute puis retire le morceau des favoris`() {
        val viewModel = PlayerViewModel()
        viewModel.onAction(PlayerAction.ToggleFavorite)
        assertTrue(viewModel.uiState.value.isFavorite)
        viewModel.onAction(PlayerAction.ToggleFavorite)
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `SeekTo est borné entre 0 et la durée du morceau`() {
        val viewModel = PlayerViewModel()
        viewModel.onAction(PlayerAction.SeekTo(9999))
        assertEquals(tracks.first().durationSec, viewModel.uiState.value.positionSec)
        viewModel.onAction(PlayerAction.SeekTo(-5))
        assertEquals(0, viewModel.uiState.value.positionSec)
    }

    @Test
    fun `PlayPause fait avancer la lecture d'une seconde par seconde`() = runTest(testDispatcher) {
        val viewModel = PlayerViewModel()
        viewModel.onAction(PlayerAction.PlayPause)
        assertTrue(viewModel.uiState.value.isPlaying)

        advanceTimeBy(3_500)                       // on avance l'horloge de 3,5 secondes
        assertEquals(3, viewModel.uiState.value.positionSec)

        viewModel.onAction(PlayerAction.PlayPause) // pause
        advanceTimeBy(5_000)
        assertEquals(3, viewModel.uiState.value.positionSec)
        assertFalse(viewModel.uiState.value.isPlaying)
    }
}
