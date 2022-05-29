package com.matorralesjordi.android.simon_says_game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.matorralesjordi.android.simon_says_game.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    // Creamos conexion con la bbdd
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Analytics Event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        setup()
    }

    private fun setup() {
        //clic al botó inscriu-te
        binding.btnSignUp.setOnClickListener {
            if (binding.txtEmail.text.isNotEmpty() && binding.txtPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.txtEmail.text.toString(),
                    binding.txtPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // creamos el campo donde se guardara el winrate del usuario
                        db.collection("usersSolo")
                            .document(binding.txtEmail.text.toString())
                            .set(hashMapOf("soloRecord1" to "0",
                                            "soloRecord2" to "0",
                                            "soloRecord3" to "0"))
                        db.collection("usersCoop")
                            .document(binding.txtEmail.text.toString())
                            .set(hashMapOf("coopRecord1" to "0",
                                "coopRecord2" to "0",
                                "coopRecord3" to "0"))
                        showHome(it.result?.user?.email ?: "")
                    } else {
                        showAlertSignUp()
                    }
                }
            }

        }
        //clic al botó Accedir
        binding.btnLogIn.setOnClickListener {
            if (binding.txtEmail.text.isNotEmpty() && binding.txtPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.txtEmail.text.toString(),
                    binding.txtPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "")
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }

    //falla la autenticació d'usuaris
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error al autentificar al usuario!")
        builder.setPositiveButton("Acceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //falla la autenticació d'usuaris
    private fun showAlertSignUp(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error al registrar al usuario!")
        builder.setPositiveButton("Acceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //l'autenticació funciona correctament
    private fun showHome(email: String){
        val homeIntent = Intent(this, UserHomeActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }


}