package com.matorralesjordi.android.simon_says_game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.matorralesjordi.android.simon_says_game.databinding.ActivityCoopGameBinding

var isMyMove = isCodeMaker

class CoopGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoopGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoopGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //el botó apareixerà habilitat o no segons si ets el creador o no del codi
        binding.button.isEnabled = isCodeMaker==true
        //connexió a la base dades i listener per detectar canvis
        FirebaseDatabase.getInstance("https://simon-say-s-game-default-rtdb.europe-west1.firebasedatabase.app/")
            .reference.child("data").child(code).addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }
                //quan s'afegeix un "fill" a la taula "data" es canvïa el torn del jugador i l'estat del botó
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var data = snapshot.value
                    if(isMyMove==true){
                        binding.button.isEnabled = false
                        isMyMove = false
                    }
                    else{
                        binding.button.isEnabled = true
                        isMyMove = true
                    }

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

            })
        binding.button.setOnClickListener{
            if(isMyMove)  updateDatabase((1..6).random())
        }
    }
    //afegim un nou valor a la taula "data"
    fun updateDatabase(cellId : Int)
    {
        FirebaseDatabase.getInstance("https://simon-say-s-game-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("data")
            .child(code).push().setValue(cellId);
    }

}