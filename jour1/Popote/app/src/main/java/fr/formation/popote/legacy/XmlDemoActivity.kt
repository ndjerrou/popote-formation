package fr.formation.popote.legacy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.formation.popote.R
import fr.formation.popote.databinding.ActivityXmlDemoBinding

// region xml
/** Le monde "impératif" : on récupère les vues et on les modifie à la main. */
class XmlDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXmlDemoBinding
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityXmlDemoBinding.inflate(layoutInflater)   // View Binding
        setContentView(binding.root)

        // Ancienne méthode, pour comparaison : val button = findViewById<Button>(R.id.incrementButton)
        binding.incrementButton.setOnClickListener {
            counter++
            binding.counterText.text = getString(R.string.xml_counter, counter) // mise à jour MANUELLE
        }
        binding.greetButton.setOnClickListener {
            val name = binding.nameInput.text.toString().ifBlank { "inconnu" }
            binding.greetingText.text = getString(R.string.hello, name)
        }
    }
}
// endregion xml
