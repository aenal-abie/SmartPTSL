package smartgis.project.app.smartgis

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import smartgis.project.app.smartgis.databinding.ActivityMainBinding
import smartgis.project.app.smartgis.databinding.ActivityWelcomeLoginBinding

class MainActivity :  LoginRequiredActivity(),
    OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }
}