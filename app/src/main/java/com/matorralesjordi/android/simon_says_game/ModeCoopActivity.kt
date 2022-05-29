package com.matorralesjordi.android.simon_says_game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.database.*
import com.matorralesjordi.android.simon_says_game.databinding.ActivityModeCoopBinding

var isCodeMaker = true //aquesta variable indica si una determinada instància del programa és la del creador el codi o no
lateinit var code: String //codi associata a cada “partida”

class ModeCoopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModeCoopBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModeCoopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance("https://simon-say-s-game-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("codes")
        //listener associat al botó que permet crear un codi
        binding.btnHost.setOnClickListener {
            if(binding.txtCode.isEnabled){
                val rnd = (10000..99999).random()
                binding.txtCode.setText(rnd.toString())
                binding.txtCode.isEnabled = false
                binding.btnAccess.isEnabled = false
            }

            code = binding.txtCode.text.toString()
            //indiquem que aquesta isntància del programa és la del creador del codi
            isCodeMaker = true
            //afegim un listener a la base de dades que detecta quan s'afegeix un nou valor
            database.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
                //si el codi no existeix ja a la base de dades, s'afegeix i s'obre l'altra pantalla
                override fun onDataChange(snapshot: DataSnapshot) {
                    var check = isValueAvailable(snapshot , code)
                    if(check==false) {
                        //afegim el nou codi
                        database.push().setValue(code)
                        //obrim l'altra finestra
                        val gameIntent = Intent(this@ModeCoopActivity, CoopGameActivity::class.java)
                        startActivity(gameIntent)
                    }
                }
            })
        }
        //listener associat al botó que permet unir-se a un codi
        binding.btnAccess.setOnClickListener{
            code = binding.txtCode.text.toString()
            //indiquem que aquesta instància del programa no és la del creador del codi
            isCodeMaker = false;

            database.addValueEventListener(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
                //si el codi ja existeix (algú l'ha creat) obrim la nova pantalla
                override fun onDataChange(snapshot: DataSnapshot) {
                    var data: Boolean = isValueAvailable(snapshot, code)
                    if (data == true) {
                        delay(10000){
                            val gameIntent = Intent(this@ModeCoopActivity, CoopGameActivity::class.java)
                            startActivity(gameIntent)
                        }
                    }
                }
            })
        }
    }

    //   Comprovem si el codi existeix a la base de dades i retornem true o false segons el resultat
    fun isValueAvailable(snapshot: DataSnapshot, code : String): Boolean {
        var data = snapshot.children
        data.forEach{
            var value = it.getValue().toString()
            if(value == code) return true
        }
        return false
    }

    inline fun delay(delay: Long, crossinline completion: () -> Unit){
        Handler(Looper.getMainLooper()).postDelayed({completion()}, delay)
    }
}