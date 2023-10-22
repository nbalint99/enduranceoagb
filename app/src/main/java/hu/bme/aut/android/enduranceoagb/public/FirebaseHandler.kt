package hu.bme.aut.android.enduranceoagb.public

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.MainActivity
import hu.bme.aut.android.enduranceoagb.databinding.ActivityFirebasehandlerBinding

class FirebaseHandler : AppCompatActivity() {
    private lateinit var binding: ActivityFirebasehandlerBinding

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirebasehandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        Handler().postDelayed({
            val intent = Intent(this@FirebaseHandler, MainActivity::class.java)
            startActivity(intent)
        }, 2000)


    }

    override fun onBackPressed() {

    }
}