package com.matorralesjordi.android.simon_says_game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.matorralesjordi.android.simon_says_game.databinding.ActivityUserHomeBinding


class UserHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserHomeBinding
    // Creamos conexion con la bbdd
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        setup(email?:"")
    }

    private fun setup(email: String) {
        binding.txtEmail.text = email

        // Recuperamos datos del recor personal en solitario mas alto
        db.collection("usersSolo").document(email).get().addOnSuccessListener {
            binding.txtSoloRecord1.text = it.get("soloRecord1") as String?
            binding.txtSoloRecord2.text = it.get("soloRecord2") as String?
            binding.txtSoloRecord3.text = it.get("soloRecord3") as String?
        }
        // Recuperamos datos del recor personal cooperativo mas alto
        db.collection("usersCoop").document(email).get().addOnSuccessListener {
            binding.txtCoopRecord1.text = it.get("coopRecord1") as String?
            binding.txtCoopRecord2.text = it.get("coopRecord2") as String?
            binding.txtCoopRecord3.text = it.get("coopRecord3") as String?
        }

        // Btn cerrar sesion
        binding.btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //tornem a la pantalla d'inici
            onBackPressed()
        }

        // Btn modo solitario
        binding.btnPlaySolo.setOnClickListener {
            val soloIntent = Intent(this, SoloGameActivity::class.java).apply{
                putExtra("email", email)
                putExtra("soloRecord1", binding.txtSoloRecord1.text)
                putExtra("soloRecord2", binding.txtSoloRecord2.text)
                putExtra("soloRecord3", binding.txtSoloRecord3.text)
            }
            startActivity(soloIntent)
        }

        binding.btnPlayCoop.setOnClickListener{
            val popUpIntent = Intent(this, ModeCoopActivity::class.java).apply{
                putExtra("email", email)
                putExtra("coopRecord1", binding.txtCoopRecord1.text)
                putExtra("coopRecord2", binding.txtCoopRecord2.text)
                putExtra("coopRecord3", binding.txtCoopRecord3.text)
            }
            startActivity(popUpIntent)
        }

    }

}