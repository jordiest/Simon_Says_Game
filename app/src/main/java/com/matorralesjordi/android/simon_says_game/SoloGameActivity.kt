package com.matorralesjordi.android.simon_says_game

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.matorralesjordi.android.simon_says_game.databinding.ActivitySoloGameBinding
import java.lang.Exception
import kotlin.system.exitProcess

class SoloGameActivity : AppCompatActivity() {
    lateinit var binding: ActivitySoloGameBinding
    // Declaración de botones y sonidos para el juego
    private var btns = arrayOfNulls<View>(4)
    private var sounds = arrayOfNulls<MediaPlayer>(4)
    private val chainSounds = mutableListOf<Int>()
    private lateinit var startGame: MediaPlayer
    private lateinit var endGame: MediaPlayer

    // Declaracion de marcas y otros
    private var highestScore: Int = 0
    private var score: Int = 0
    private var hits: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoloGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Indicamos los botones y sonidos
        createBtnsSounds()
        // Indicamos las marcas y la información inicial
        binding.txtRecord.text = getString(R.string.record) + ": $highestScore"
        binding.txtScore.text = getString(R.string.score) + ": $score"
        binding.txtInfo.text = getString(R.string.initplay)
        // deshabilitamos los botones de juego
        stateButtons(false)
        // Habilitamos el boton para empezar la partida
        binding.btnStartSolo.setOnClickListener {
            binding.txtScore.text = getString(R.string.score) + ": $score"
            turnComputer()
            binding.btnStartSolo.isEnabled = false

        }
        // Habilitamos la opcion de salir de la partida
        binding.btnExit.setOnClickListener { exitProcess(-1) }

    }
    // Funcion cuando el usuario pulsa un boton de juego
    fun btnImage(view: View){
        val index: Int = when(view.id){
            R.id.imgBlue -> 0
            R.id.imgRed -> 1
            R.id.imgYellow -> 2
            else -> 3
        }
        if(index == chainSounds[hits]){
            //sounds[index]?.setVolume(1.0f, 1.0f)
            //sounds[index]?.start()
            // # mod 1
            btnClicked(index)
            //
            hits += 1
            if(hits == chainSounds.size){
                score = hits
                binding.txtScore.text = getString(R.string.score) + ": $score"
                stateButtons(false)
                delay(500L){
                    turnComputer()
                }
            }
        }else{
            binding.txtInfo.text = getString(R.string.yourlost)
            checkScore()
            delay(500L){
                binding.btnStartSolo.isEnabled = true
                chainSounds.clear()
                stateButtons(false)
            }
        }
    }
    // Funcion para comprobar si hay un nuevo record personal
    private fun checkScore() {
        if(score > highestScore){
            highestScore = score
            score = 0
            binding.txtRecord.text = getString(R.string.record) + ": $highestScore"
            binding.txtScore.text = getString(R.string.score) + ": $score"
        }
    }

    // Funcion cuando es el turno de la ia
    private fun turnComputer() {
        binding.txtInfo.text = getString(R.string.simonstxt)
        chainSounds?.add((0..3).random())
        delay(1000){
            for(i in 0 until (chainSounds.size)){
                delay(1500L * i){
                    btnClicked(chainSounds[i])
                }
            }
            delay(chainSounds.size * 1600L){
                binding.txtInfo.text = getString(R.string.yourtuns)
                stateButtons(true)
            }
            hits = 0
        }

    }
    // Funcion para activar o deshabilitar los botones de juego
    private fun stateButtons(bool: Boolean) {
        binding.imgBlue.isEnabled = bool
        binding.imgRed.isEnabled = bool
        binding.imgYellow.isEnabled = bool
        binding.imgGreen.isEnabled = bool
    }
    // Funcion de la ia para hacer la animacion de los botones
    private fun btnClicked(index: Int) {
        val before: Int
        val after: Int
        try{
            when(index){
                0 -> {
                    before = R.drawable.rosco_blue_shining
                    after = R.drawable.rosco_blue
                }
                1 -> {
                    before = R.drawable.rosco_red_shining
                    after = R.drawable.rosco_red
                }
                2 -> {
                    before = R.drawable.rosco_yellow_shining
                    after = R.drawable.rosco_yellow
                }
                else -> {
                    before = R.drawable.rosco_green_shining
                    after = R.drawable.rosco_green
                }
            }
            btns[index]?.setBackgroundResource(before)
            sounds[index]?.setVolume(1.0F, 1.0F)
            sounds[index]?.start()

            delay(250){
                btns[index]?.setBackgroundResource(after)
            }
        }catch (e: Exception){}

    }

    private fun createBtnsSounds() {
        // creacion de botones
        btns[0] = binding.imgBlue
        btns[1] = binding.imgRed
        btns[2] = binding.imgYellow
        btns[3] = binding.imgGreen

        // Creacion de sonidos
        sounds[0] = MediaPlayer.create(this, R.raw.blue_sound)
        sounds[1] = MediaPlayer.create(this, R.raw.red_sound)
        sounds[2] = MediaPlayer.create(this, R.raw.yellow_sound)
        sounds[3] = MediaPlayer.create(this, R.raw.green_sound)

        startGame = MediaPlayer.create(this, R.raw.center_pressed)
        endGame = MediaPlayer.create(this, R.raw.error)
    }

    inline fun delay(delay: Long, crossinline completion: () -> Unit){
        Handler(Looper.getMainLooper()).postDelayed({completion()}, delay)
    }
}