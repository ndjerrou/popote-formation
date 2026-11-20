package fr.formation.musicplayer.data

import android.content.Context
import android.media.MediaPlayer

// LE CONTRAT du haut-parleur : ce qu'on attend de lui, sans dire comment il le fait.
interface AudioPlayer {
    fun load(track: Track)
    fun play()
    fun pause()
    fun seekTo(positionSec: Int)
    fun release()
}

// L'implémentation Android, avec le MediaPlayer du système.
class MediaAudioPlayer(private val context: Context) : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    override fun load(track: Track) {
        mediaPlayer?.release()                              // on libère toujours l'ancien lecteur
        mediaPlayer = MediaPlayer.create(context, track.audioRes)
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.let { if (it.isPlaying) it.pause() }  // pause() hors lecture = erreur
    }

    override fun seekTo(positionSec: Int) {
        mediaPlayer?.seekTo(positionSec * 1000)             // MediaPlayer compte en millisecondes
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
