package fr.formation.musicplayer.feature.player

import fr.formation.musicplayer.data.AudioPlayer
import fr.formation.musicplayer.data.Track

// Un faux haut-parleur pour les tests : il ne joue rien, il NOTE ce qu'on lui demande.
class FakeAudioPlayer : AudioPlayer {
    var loadedTrack: Track? = null
    var isPlaying = false
    var lastSeekSec: Int? = null
    var released = false

    override fun load(track: Track) { loadedTrack = track }
    override fun play() { isPlaying = true }
    override fun pause() { isPlaying = false }
    override fun seekTo(positionSec: Int) { lastSeekSec = positionSec }
    override fun release() { released = true }
}
