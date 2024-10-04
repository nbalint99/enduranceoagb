package hu.bme.aut.android.enduranceoagb.public

import android.app.Application
import android.content.Intent
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializálás aszinkron
        GlobalScope.launch(Dispatchers.IO) {
            try {
                FirebaseApp.initializeApp(this@MyApplication)?.let {
                    // Firebase inicializálás sikeres
                    // Az inicializálás befejeződött, indítjuk az új Activity-t
                    startMainActivity()
                } ?: run {
                    // Firebase inicializálás nem sikerült
                }
            } catch (e: Exception) {
                // Hiba kezelése
                e.printStackTrace()
            }
        }
    }

    private fun startMainActivity() {
        // Hozzáférés az Application Context-hez
        val intent = Intent(applicationContext, FirebaseHandler::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK  // Alkalmazás szintű intent, amely új feladatot hoz létre
        startActivity(intent)
    }
}

