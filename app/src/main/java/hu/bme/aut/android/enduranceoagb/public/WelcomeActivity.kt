package hu.bme.aut.android.enduranceoagb.public

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.enduranceoagb.LoginActivity
import hu.bme.aut.android.enduranceoagb.MainActivity
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.RaceActivity
import hu.bme.aut.android.enduranceoagb.databinding.ActivityLoginBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser != null) {
            binding.tvUser.text = Firebase.auth.currentUser!!.email
        }
        else {
            binding.tvUser.text = "Hiba! Nincs bejelentkezve!"
        }

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races")

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val raceName = p0.result.child("Actual").child("name").value.toString()
                binding.tvRaceName.text = raceName
            }
        }

        /*binding.signOut.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }*/


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.signout, menu)
        return true
    }

    override fun onBackPressed() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.signOutMenu) {
            Firebase.auth.signOut()

            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)

    }
}