package fr.formation.musicplayer.navigation

import kotlinx.serialization.Serializable

// Les écrans de l'appli. Chaque destination est un type : le compilateur vérifie les arguments.

@Serializable
data object LibraryDestination                     // la bibliothèque : pas d'argument

@Serializable
data class PlayerDestination(val trackId: Int)     // le lecteur : il faut savoir QUEL morceau jouer

@Serializable
data object AboutDestination                       // à propos : pas d'argument
