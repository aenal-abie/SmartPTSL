package smartgis.project.app.smartgis

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import smartgis.project.app.smartgis.databinding.ActivityHomeBinding
import smartgis.project.app.smartgis.databinding.ActivityWelcomeLoginBinding

class Home :  LoginRequiredActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}