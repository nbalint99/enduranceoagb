package hu.bme.aut.android.enduranceoagb.public

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.LoginActivity
import hu.bme.aut.android.enduranceoagb.MainActivity
import hu.bme.aut.android.enduranceoagb.databinding.ActivityFirebasehandlerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirebaseHandler : AppCompatActivity() {
    private lateinit var binding: ActivityFirebasehandlerBinding

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirebasehandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*Handler().postDelayed({
            val intent = Intent(this@FirebaseHandler, MainActivity::class.java)
            startActivity(intent)
        }, 2000)*/

        // Ellenőrizd, hogy a Firebase inicializálás befejeződött-e
        GlobalScope.launch(Dispatchers.Main) {
            // Várj a Firebase inicializálás befejeződésére
            while (FirebaseApp.getApps(this@FirebaseHandler).isEmpty()) {
                delay(100)
            }
            Handler().postDelayed({
                val intent = Intent(this@FirebaseHandler, LoginActivity::class.java)
                startActivity(intent)
            }, 2000)
            // Firebase inicializálás befejeződött, indítsd az új Activity-t
            //startActivity(Intent(this@FirebaseHandler, MainActivity::class.java))
            //finish() // Befejezi az aktuális Activity-t, ha szükséges
        }

    }

    override fun onBackPressed() {

    }
}