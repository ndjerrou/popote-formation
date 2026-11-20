package fr.formation.filmlib

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import fr.formation.filmlib.databinding.FragmentFilmPlayerBinding

// L'ÉCRAN de l'équipe Film : un Fragment classique (XML + ViewBinding).
// Il ne sait PAS qu'il est affiché dans une appli Compose.
class FilmPlayerFragment : Fragment() {

    // Le lien vers l'hôte : l'appli qui affiche ce Fragment décide quoi faire quand on ferme.
    var onFermer: (() -> Unit)? = null

    // ViewBinding : n'existe qu'entre onCreateView et onDestroyView
    private var _binding: FragmentFilmPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var lecteur: LecteurFilm
    private val horloge = Handler(Looper.getMainLooper())
    private val tic = object : Runnable {
        override fun run() {
            lecteur.avancer()
            afficher()
            if (lecteur.enLecture) horloge.postDelayed(this, 1000)
        }
    }

    // Retour : si le synopsis est ouvert, on le ferme ; sinon on laisse l'hôte gérer (callback désactivé)
    private val retourSynopsis = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = basculerSynopsis()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
        val film = FilmCatalog.byId(requireArguments().getInt(ARG_FILM_ID))
        // La position survit à la rotation grâce au Bundle sauvegardé
        lecteur = LecteurFilm(film, savedInstanceState?.getInt(KEY_POSITION) ?: 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        log("onCreateView")
        _binding = FragmentFilmPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        log("onViewCreated")
        binding.titre.text = lecteur.film.titre
        binding.realisateur.text = lecteur.film.realisateur
        binding.synopsis.text = lecteur.film.synopsis
        binding.boutonLecture.setOnClickListener {
            lecteur.lectureOuPause()
            horloge.removeCallbacks(tic)
            if (lecteur.enLecture) horloge.postDelayed(tic, 1000)
            afficher()
        }
        binding.boutonSynopsis.setOnClickListener { basculerSynopsis() }
        binding.boutonFermer.setOnClickListener { onFermer?.invoke() }
        // viewLifecycleOwner : le callback est retiré automatiquement quand la vue est détruite
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, retourSynopsis)
        afficher()
    }

    private fun basculerSynopsis() {
        val ouvert = binding.synopsis.visibility != View.VISIBLE
        binding.synopsis.visibility = if (ouvert) View.VISIBLE else View.GONE
        binding.boutonSynopsis.text = if (ouvert) "Masquer le synopsis" else "Synopsis"
        retourSynopsis.isEnabled = ouvert
    }

    private fun afficher() {
        binding.position.text = "${formatTemps(lecteur.position)} / ${formatTemps(lecteur.film.dureeSec)}"
        binding.boutonLecture.text = when {
            lecteur.termine -> "✔ Terminé"
            lecteur.enLecture -> "⏸ Pause"
            else -> "▶ Lecture"
        }
    }

    override fun onStart() { super.onStart(); log("onStart") }
    override fun onResume() { super.onResume(); log("onResume") }
    override fun onPause() { super.onPause(); log("onPause") }

    override fun onStop() {
        super.onStop()
        log("onStop")
        horloge.removeCallbacks(tic)          // l'écran n'est plus visible : on arrête l'horloge
        if (lecteur.enLecture) lecteur.lectureOuPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        log("onSaveInstanceState")
        outState.putInt(KEY_POSITION, lecteur.position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log("onDestroyView")
        _binding = null                       // sinon fuite mémoire : la vue est morte mais encore référencée
    }

    override fun onDestroy() { super.onDestroy(); log("onDestroy") }

    private fun log(etape: String) = Log.d(TAG, "${nomFilm()} $etape")
    private fun nomFilm() = if (::lecteur.isInitialized) "[${lecteur.film.titre}]" else "[?]"

    companion object {
        const val TAG = "FilmLib"
        const val ARG_FILM_ID = "filmId"
        private const val KEY_POSITION = "position"

        // La façon « officielle » de créer ce Fragment avec ses arguments
        fun arguments(filmId: Int): Bundle = bundleOf(ARG_FILM_ID to filmId)
    }
}
