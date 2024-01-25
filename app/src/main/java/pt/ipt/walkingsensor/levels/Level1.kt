package pt.ipt.walkingsensor.levels

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Point
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ipt.WalkingSensorGame.R
import java.sql.DriverManager.println
import java.util.Random
import java.util.Timer
import java.util.TimerTask


//Para duvidas verificar no video https://www.youtube.com/watch?v=xcsuDDQHrLo&ab_channel=Indently

class Level1 : AppCompatActivity(), SensorEventListener {

    private lateinit var deadScreen: ConstraintLayout
    private lateinit var blackScreen: TextView
    private lateinit var character: ImageView
    private lateinit var mainGame: ImageView
    private lateinit var redNPCImage: ImageView
    private lateinit var blueNPCImage: ImageView
    private lateinit var greenNPCImage: ImageView

    private lateinit var walkableMatrix: Array<Array<Int>>
    private lateinit var sensorManager: SensorManager
    private lateinit var walkableLayout: ConstraintLayout
    private lateinit var collectables: ConstraintLayout
    private lateinit var endingView: ImageView
    private lateinit var progressBar: ProgressBar
    private var collectablesMissing = 0
    private var animSet = AnimatorSet()
    private var unitX: Float? = null
    private var unitY: Float? = null
    private var baseX: Int = 0
    private var baseY: Int = 0
    private var isDead: Boolean = true
    private var screenWidth: Float = 0F
    private var screenHeight: Float = 0F
    private val sensitivity = 6f

    // Baseline variables
    private var baselineSides: Float = 0f
    private var baselineUpdown: Float = 0f

    // Flag to track whether it's the first time
    private var isFirstTime = true

    private fun stringToArray(input: String): Array<Array<Int>> {
        val cleanedInput = input.replace("\n", "")
        val lines = cleanedInput.trim().substring(2, cleanedInput.length - 2).split("], [")
        val result = Array(lines.size) { Array(0) { 0 } }

        for ((i, line) in lines.withIndex()) {
            val values = line.split(", ")
            result[i] = Array(values.size) { values[it].toInt() }
        }

        return result
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level1)

        setupSensors()
        setupGame()
        setupCordSystem()

        //Start the game
        //Play fade-in animation
        //startAnimations()
        startFastAnimations()


    }
    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }


    private fun startAnimations() {

        //Vars
        animSet = AnimatorSet()
        character.x = getScreenWidth() / 2f
        character.y = getScreenHeight()* (1 / 3f)

        //Black screen
        val fadeBlackScreen1Half: ValueAnimator = ObjectAnimator.ofFloat(blackScreen, "alpha", 1f, 0.5f)
        fadeBlackScreen1Half.duration = 5_000
        animSet.play(fadeBlackScreen1Half)

        //Play "What is going on" animation
        //Play character going down from the middle of the screen
        //character appears and becomes huge because he's close to the screen

        val playerMessageAnim1 = writeMessage("HAAAAAA!!!  ",3_000)
        val characterOnAnim = ObjectAnimator.ofFloat(character,"alpha",0f,1f)
        characterOnAnim.duration = 500
        animSet.play(characterOnAnim)
        var scaleX = ObjectAnimator.ofFloat(character, "scaleX", 1f, 6f)
        var scaleY = ObjectAnimator.ofFloat(character, "scaleY", 1f, 6f)
        animSet.play(characterOnAnim).with(scaleX).with(scaleY).after(fadeBlackScreen1Half)


        animSet.play(playerMessageAnim1).after(characterOnAnim).after(1000)


        //character "descends", just resizing gives the effect of apearing to be dropped down
        scaleX = ObjectAnimator.ofFloat(character, "scaleX", 6f, 1f)
        scaleX.duration = 3_000
        scaleY = ObjectAnimator.ofFloat(character, "scaleY", 6f, 1f)
        scaleY.duration = 3_000
        animSet.play(scaleX).with(scaleY).after(characterOnAnim)


        //Red guy appears
        var txt = "Mais um!? Coitado ainda não sabe o que lhe espera..."
        var duration = 4_000L
        val redMessageAnim1: AnimatorSet = writeMessage(txt,duration)
        val redNpcOnAnim1 = ObjectAnimator.ofFloat(redNPCImage,"alpha",0f,1f)
        val redNpcOffAnim1 = ObjectAnimator.ofFloat(redNPCImage,"alpha",1f,0f)
        redNpcOnAnim1.duration = 3_000
        redNpcOffAnim1.duration = 3_000
        redNpcOffAnim1.startDelay = duration + txt.length*50L // typeDuration
        animSet.play(redNpcOnAnim1).with(redMessageAnim1).with(redNpcOffAnim1).after(playerMessageAnim1)

        //Blue guy appears
        txt = "Se isto já era dificil, agora com ele... "
        duration = 4_000L
        val blueMessageAnim1 = writeMessage(txt,duration)
        val blueNpcOnAnim1 = ObjectAnimator.ofFloat(blueNPCImage,"alpha",0f,1f)
        val blueNpcOffAnim1 = ObjectAnimator.ofFloat(blueNPCImage,"alpha",1f,0f)
        blueNpcOnAnim1.duration = 3_000
        blueNpcOffAnim1.duration = 3_000
        blueNpcOffAnim1.startDelay = duration + txt.length*50L // typeDuration
        animSet.play(blueNpcOnAnim1).with(blueMessageAnim1).with(blueNpcOffAnim1).after(redMessageAnim1)

        //Player Responds
        txt = "Uhm? Ahm...  Como assim??"
        duration = 4_000L
        val playerMessageAnim2 = writeMessage(txt,duration)
        animSet.play(playerMessageAnim2).after(blueMessageAnim1)

        //Green guy appears
        txt = "Olha, este conseguiu sobreviver. Como é que te chamas?"
        duration = 4_000L
        val greenMessageAnim1 = writeMessage(txt,duration)
        val greenNpcOnAnim1 = ObjectAnimator.ofFloat(greenNPCImage,"alpha",0f,1f)
        val greenNpcOffAnim1 = ObjectAnimator.ofFloat(greenNPCImage,"alpha",1f,0f)
        greenNpcOnAnim1.duration = 3_000
        greenNpcOffAnim1.duration = 3_000
        greenNpcOffAnim1.startDelay = duration + txt.length*50L // typeDuration
        animSet.play(greenNpcOnAnim1).with(greenMessageAnim1).with(greenNpcOffAnim1).after(playerMessageAnim2)

        //Player Responds
        txt = "Boas sou o player, estou confuso... O que é que se passa?"
        duration = 4_000L
        val playerMessageAnim3 = writeMessage(txt,duration)
        animSet.play(playerMessageAnim3).after(greenMessageAnim1)

        //Blue guy responds
        txt = "É simples, morreste e vieste aqui parar. Sorte a tua, sobreviveste à queda."
        duration = 4_000L
        val blueMessageAnim2 = writeMessage(txt,duration)
        val blueNpcOnAnim2 = ObjectAnimator.ofFloat(blueNPCImage,"alpha",0f,1f)
        blueNpcOnAnim2.duration = 3_000
        animSet.play(blueNpcOnAnim2).with(blueMessageAnim2).after(playerMessageAnim3)

        //Red guy responds
        txt = "Até te safaste, Hahaha."
        duration = 4_000L
        val redMessageAnim2: AnimatorSet = writeMessage(txt,duration)
        val redNpcOnAnim2 = ObjectAnimator.ofFloat(redNPCImage,"alpha",0f,1f)
        redNpcOnAnim2.duration = 3_000
        animSet.play(redNpcOnAnim2).with(redMessageAnim2).after(blueMessageAnim2)

        //Green guy responds
        txt = "Nós quando chegamos abusamos um pouco. Ahh, que boa vida ."
        duration = 4_000L
        val greenMessageAnim2 = writeMessage(txt,duration)
        val greenNpcOnAnim2 = ObjectAnimator.ofFloat(greenNPCImage,"alpha",0f,1f)
        greenNpcOnAnim2.duration = 3_000
        animSet.play(greenNpcOnAnim2).with(greenMessageAnim2).after(redMessageAnim2)


        //Green guy responds
        txt = "Tinhamos tudo o que precisavamos e por uns tempos não mexemos uma palha."
        duration = 4_000L
        val greenMessageAnim3 = writeMessage(txt,duration)
        animSet.play(greenMessageAnim3).after(greenMessageAnim2)

        //Red guy responds
        txt = "Comemos até não haver mais."
        duration = 4_000L
        val redMessageAnim3: AnimatorSet = writeMessage(txt,duration)
        animSet.play(redMessageAnim3).after(greenMessageAnim3)

        //Green guy continues
        txt = "Até que nos aprecebemos que... tinhamos de começar a poupar os recursos."
        duration = 4_000L
        val greenMessageAnim4 = writeMessage(txt,duration)
        animSet.play(greenMessageAnim4).after(redMessageAnim3)

        //Blue guy continues
        txt = "Como já estamos aqui á algum tempo, já não conseguimos sair."
        duration = 4_000L
        val blueMessageAnim3 = writeMessage(txt,duration)
        animSet.play(blueMessageAnim3).after(greenMessageAnim4)

        //Blue guy continues
        txt = "Mas tu ainda tens forma de te salvar."
        duration = 4_000L
        val blueMessageAnim4 = writeMessage(txt,duration)
        animSet.play(blueMessageAnim4).after(blueMessageAnim3)

        //Red guy responds
        txt = "Cheira-me a negócio. Nós ensimamos-te como sair e em troca tu ajudas-nos."
        duration = 4_000L
        val redMessageAnim4: AnimatorSet = writeMessage(txt,duration)
        animSet.play(redMessageAnim4).after(blueMessageAnim4)


        //All disappear
        val greenNpcOnAnim3 = ObjectAnimator.ofFloat(greenNPCImage,"alpha",1f,0f)
        val blueNpcOnAnim3 = ObjectAnimator.ofFloat(blueNPCImage,"alpha",1f,0f)
        val redNpcOnAnim3 = ObjectAnimator.ofFloat(redNPCImage,"alpha",1f,0f)
        animSet.play(greenNpcOnAnim3).with(blueNpcOnAnim3).with(redNpcOnAnim3).after(redMessageAnim4)


        //Fade the rest out
        val fadeBlackScreen2Half: ValueAnimator = ObjectAnimator.ofFloat(blackScreen, "alpha", 0.5f, 0.0f)
        fadeBlackScreen2Half.duration = 2_000

        animSet.play(fadeBlackScreen2Half).after(greenNpcOnAnim3)
        animSet.doOnEnd {
            setupProgressBarTimer()
            isDead = false
        }
        animSet.start()

    }

    private fun startFastAnimations()  {
        //Vars
        animSet = AnimatorSet()
        character.x = getScreenWidth() / 2f
        character.y = getScreenHeight()* (1 / 3f)

        //Black screen
        val fadeBlackScreen1Half: ValueAnimator = ObjectAnimator.ofFloat(blackScreen, "alpha", 1f, 0.5f)
        fadeBlackScreen1Half.duration = 5_000
        animSet.play(fadeBlackScreen1Half)

        //Play "What is going on" animation
        //Play character going down from the middle of the screen
        //character appears and becomes huge because he's close to the screen

        val playerMessageAnim1 = writeMessage("HAAAAAA!!!  ",3_000)
        val characterOnAnim = ObjectAnimator.ofFloat(character,"alpha",0f,1f)
        characterOnAnim.duration = 500
        animSet.play(characterOnAnim)
        var scaleX = ObjectAnimator.ofFloat(character, "scaleX", 1f, 6f)
        var scaleY = ObjectAnimator.ofFloat(character, "scaleY", 1f, 6f)
        animSet.play(characterOnAnim).with(scaleX).with(scaleY).after(fadeBlackScreen1Half)

        animSet.play(playerMessageAnim1).after(characterOnAnim).after(1000)

        //character "descends", just resizing gives the effect of apearing to be dropped down
        scaleX = ObjectAnimator.ofFloat(character, "scaleX", 6f, 1f)
        scaleX.duration = 3_000
        scaleY = ObjectAnimator.ofFloat(character, "scaleY", 6f, 1f)
        scaleY.duration = 3_000
        animSet.play(scaleX).with(scaleY).after(characterOnAnim)

        //Fade the rest out
        val fadeBlackScreen2Half: ValueAnimator = ObjectAnimator.ofFloat(blackScreen, "alpha", 0.5f, 0.0f)
        fadeBlackScreen2Half.duration = 2_000

        animSet.play(fadeBlackScreen2Half).after(playerMessageAnim1)//.after(greenNpcOnAnim3)
        animSet.doOnEnd {
            setupProgressBarTimer()
            isDead = false
        }
        animSet.start()

    }

    private fun setupProgressBarTimer() {
        val t = Timer()
        val tt: TimerTask = object : TimerTask() {
            override fun run() {
                if (progressBar.progress > 0) {
                    runOnUiThread {
                        progressBar.progress = progressBar.progress - 2
                        Log.d("Debug", "progress/Max = ${progressBar.progress}/${progressBar.max}")
                    }
                } else {
                    t.cancel()
                    isDead = true
                    runOnUiThread {
                        gameOver()
                    }
                }
            }
        }

        t.schedule(tt, 0, 500)
    }

    private fun gameOver() {
        // Darken Screen
        val fadeBlackScreen1Half: ValueAnimator = ObjectAnimator.ofFloat(blackScreen, "alpha", 0f, 0.5f)
        fadeBlackScreen1Half.duration = 2_500
        fadeBlackScreen1Half.doOnEnd {


            runOnUiThread {
                deadScreen.visibility = View.VISIBLE
            }

        }
        animSet = AnimatorSet()
        animSet.play(fadeBlackScreen1Half)
        animSet.start()


    }

    private fun writeMessage(txt:String, duration: Long): AnimatorSet {
        val animationSet = AnimatorSet()
        val scrollImage = findViewById<ImageView>(R.id.ImageScroll)
        val scrollText =  findViewById<TextView>(R.id.textViewTypewritter)
        val ScrollImgOnAnim = ObjectAnimator.ofFloat(scrollImage,"alpha",0f,1f)
        val ScrollTextOnAnim = ObjectAnimator.ofFloat(scrollText,"alpha",0f,1f)
        ScrollTextOnAnim.doOnEnd {
            scrollText.typeWrite(
                this,
                txt,
                50L,
                )
        }

        ScrollImgOnAnim.duration = 1_500
        ScrollTextOnAnim.duration = 1_500
        animationSet.play(ScrollImgOnAnim).with(ScrollTextOnAnim)

        val ScrollImgOffAnim = ObjectAnimator.ofFloat(scrollImage,"alpha",1f,0f)
        val ScrollTextOffAnim = ObjectAnimator.ofFloat(scrollText,"alpha",1f,0f)
        ScrollTextOffAnim.doOnEnd {
            scrollText.text = ""
        }

        ScrollImgOffAnim.duration = 3_000
        ScrollTextOffAnim.duration = 3_000
        animationSet.play(ScrollImgOffAnim)
                    .with(ScrollTextOffAnim)
                    .after(duration + txt.length*50L)
                    .after(ScrollTextOnAnim)

        return animationSet
    }

    // https://stackoverflow.com/questions/64460165/making-texts-appear-one-by-one-aka-typewriter-effect-in-android-kotlin
    fun TextView.typeWrite(lifecycleOwner: LifecycleOwner, text: String, intervalMs: Long) {
        this@typeWrite.text = ""
        lifecycleOwner.lifecycleScope.launch {
            repeat(text.length) {
                delay(intervalMs)
                this@typeWrite.text = text.take(it + 1)
            }
        }
    }

    // Animation https://stackoverflow.com/questions/4813995/set-alpha-opacity-of-layout
    private fun setupBlackScreen() {
        blackScreen.x = 0f
        blackScreen.y = 0f
        blackScreen.width = getScreenWidth()
        blackScreen.height = getScreenHeight()


    }

    private fun restartActivity() {
        //reset vars
        animSet = AnimatorSet()

        //Fade All Black
        val fadeBlackScreen: ValueAnimator = ObjectAnimator.ofFloat(blackScreen, "alpha", 0.5f, 0.0f)
        val fadeDeadScreen: ValueAnimator = ObjectAnimator.ofFloat(deadScreen, "alpha", 0.5f, 0.0f)
        fadeBlackScreen.duration = 2_000
        fadeDeadScreen.duration = 2_000
        animSet.play(fadeBlackScreen).with(fadeDeadScreen)
        animSet.start()

        startFastAnimations()
        //startAnimations()
        setupMushrooms()

    }

    private fun setupGame() {

        //Setup vars
        blackScreen = findViewById(R.id.blackScreen)
        character = findViewById(R.id.character)
        mainGame = findViewById(R.id.IslandsImageView)
        redNPCImage = findViewById(R.id.CharacterRed)
        blueNPCImage = findViewById(R.id.CharacterBlue)
        deadScreen = findViewById(R.id.deadScreen)
        greenNPCImage = findViewById(R.id.CharacterGreen)
        endingView = findViewById(R.id.endImageView)

        //setup Black Screen
        setupBlackScreen()

        //back button
        val backbutton = findViewById<Button>(R.id.backButton)
        val restartButton = findViewById<Button>(R.id.restartButton)
        backbutton.setOnClickListener{
            goBack()
        }
        restartButton.setOnClickListener{
                restartActivity()
            }

        //texto = findViewById(R.id.texto)

        val mapHeight = 736
        val mapWidth = 416
        val baseCharXY = 32

        //Init temporario, vem da API
        val input = """[[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 
1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 
0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 
1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
0, 0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]]""".trimIndent()
        walkableMatrix = stringToArray(input)
        walkableLayout = findViewById(R.id.walkablegroup)
        collectables = findViewById(R.id.collectables)

        setupMushrooms()

    }

    private fun setupMushrooms() {
        val totalMushrooms = 10
        setupHealthBar(totalMushrooms)
        collectablesMissing = totalMushrooms
        Log.d("Debug","progressMax = ${progressBar.max}")
        createAndPlaceMushroomsWithinWalkables(totalMushrooms)
    }

    private fun setupHealthBar(number: Int) {
        progressBar = findViewById(R.id.progressBar)
        progressBar.max = number * 10
        progressBar.progress = progressBar.max
    }

    private fun goBack() {
        this.finish()
    }

    private fun createAndPlaceMushroomsWithinWalkables(numberOfMushrooms: Int) {
        val mushroomImageResource = R.drawable.mushroom1 // Replace with your mushroom image resource ID

        val random = Random()

        collectables.post {
            // This code will be executed after the layout is measured

            for (i in 0 until numberOfMushrooms) {
                val mushroomImageView = ImageView(this)
                mushroomImageView.setImageResource(mushroomImageResource)

                // Set the layout parameters
                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                mushroomImageView.layoutParams = layoutParams

                mushroomImageView.tag = "collectable"
                // Add the mushroom ImageView to the layout
                collectables.addView(mushroomImageView)

                // Set random X and Y coordinates within the selected walkable ImageView
                val randomWalkableView = getRandomWalkableView()
                setRandomPositionInWalkableView(randomWalkableView, mushroomImageView, random)
            }
        }
    }

    private fun setRandomPositionInWalkableView(walkableView: ImageView, imageView: ImageView, random: Random) {
        val viewTreeObserver = walkableView.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)

                // Get the width and height of the mushroom ImageView
                val mushroomWidth = imageView.width
                val mushroomHeight = imageView.height

                // Get the width and height of the selected walkable ImageView
                val walkableWidth = walkableView.width
                val walkableHeight = walkableView.height

                // Set random X and Y coordinates within the selected walkable ImageView
                val maxX = walkableWidth - mushroomWidth
                val maxY = walkableHeight - mushroomHeight

                Log.d("Debug","MaxX = $maxX; MaxY = $maxY; ")
                Log.d("Debug","walkableWidth = $walkableWidth; walkableHeight = $walkableHeight; ")
                Log.d("Debug","mushroomWidth = $mushroomWidth; mushroomHeight = $mushroomHeight; ")

                val randomX = random.nextInt(maxX)
                val randomY = random.nextInt(maxY)

                // Set the position of the mushroom ImageView within the selected walkable ImageView
                imageView.x = walkableView.x + randomX
                imageView.y = walkableView.y + randomY

                return true
            }
        })
    }

    // Helper function to get a random walkable ImageView from walkableLayout
    private fun getRandomWalkableView(): ImageView {
        val random = Random()
        val walkableViews = mutableListOf<ImageView>()

        for (i in 0 until walkableLayout.childCount) {
            val childView = walkableLayout.getChildAt(i)
            if (childView is ImageView && childView.tag?.toString()?.contains("walkable") == true) {
                walkableViews.add(childView)
            }
        }

        val walkableViewCount = walkableViews.size
        val randomIndex = random.nextInt(walkableViewCount)
        Log.d("Debug", "currentIndex/total = $randomIndex/$walkableViewCount")
        return walkableViews[randomIndex]
    }

    private fun setupCordSystem(){

        // Get screen dimensions
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        screenWidth = size.x.toFloat()
        screenHeight = size.y.toFloat()

        //Define the baseXY of the cscreen based on the character
        mainGame.post {
            val mainGameLocation = IntArray(2)

            println(" mainGame : "+mainGame.x.toString()+", "+ mainGame.y)
            println(" Screen : $screenWidth, $screenHeight")
            mainGame.getLocationInWindow(mainGameLocation)
            baseX = mainGameLocation[0]
            baseY = mainGameLocation[1] + 32

            //baseY = (mainGame.height - screenHeight).toInt()
            //baseY = (screenHeight - mainGame.height).toInt() * -1
        }


        character.post{
            //Definir tamanho da personagem baseado no resizing

            val mapHeight = 736
            val mapWidth = 416

            //Definir tiles
            unitX = (character.width/2f)
            unitY = (character.height/2f)

            character.layoutParams.width = (character.width * (1- (mapWidth/screenWidth))).toInt()
            character.layoutParams.height = (character.height * (1- (mapHeight/screenHeight))).toInt()
            character.requestLayout()

        }
    }

    private fun setupSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME,
            )
        }
    }

    private fun areRectanglesIntersecting(rect1: RectF, rect2: RectF): Boolean {
        return rect1.left < rect2.right &&
                rect1.right > rect2.left &&
                rect1.top < rect2.bottom &&
                rect1.bottom > rect2.top
    }

    // Function to check collision with a specific ImageView
    private fun isColliding(characterRect: RectF, imageView: ImageView): Boolean {
        val imageViewRect = RectF(
            imageView.x,
            imageView.y,
            imageView.x + imageView.width,
            imageView.y + imageView.height
        )

        // Check if the rectangles are intersecting
        return areRectanglesIntersecting(characterRect, imageViewRect)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && !isDead) {
            val sides = -event.values[0]
            val updown = event.values[1]

            if (isFirstTime) {
                // Capture baseline values on the first sensor change event
                captureBaseline(updown, sides)
                isFirstTime = false
            }

            if (unitX != null && unitY != null) {
                val adjustedSides = (sides - baselineSides) * sensitivity
                val adjustedUpdown = (updown - baselineUpdown) * sensitivity

                val targetX = character.translationX + adjustedSides
                val targetY = character.translationY + adjustedUpdown

                val characterRect = RectF(
                    targetX,
                    targetY,
                    targetX + character.width,
                    targetY + character.height
                )



                var isCollectableFound = false
                for (i in 0 until collectables.childCount) {
                    val collectableView = collectables.getChildAt(i) as? ImageView
                    collectableView?.let {
                        if (isColliding(characterRect, collectableView)) {
                            //Found collectable
                            Log.d("Debug", "Colliding with collectable:${collectableView.tag}")
                            if (collectableView.tag.toString().contains("collectable")){
                                collectableView.visibility = View.GONE
                                progressBar.progress += 10
                                isCollectableFound= true
                                collectableView.tag = "collected"
                                collectablesMissing--
                                if (collectablesMissing == 0){
                                    endingView.setBackgroundResource(R.drawable.exitopen)
                                    Log.d("Debug","FIM ")
                                }
                            }

                        }
                    }
                    if(isCollectableFound){break}
                }

                var isFound = false
                // Iterate over all walkable areas in the layout
                for (i in 0 until walkableLayout.childCount) {
                    val walkableView = walkableLayout.getChildAt(i) as? ImageView

                    walkableView?.let {
                        if (isColliding(characterRect, walkableView)) {
                            if (walkableView.tag.toString().contains("walkable")) {
                                // Update the character's position only if it is colliding with this ImageView
                                character.translationX = targetX.coerceIn(
                                    walkableView.x- character.width,
                                    walkableView.x + walkableView.width - 2*character.width
                                )
                                character.translationY = targetY.coerceIn(
                                    walkableView.y- character.height,
                                    walkableView.y + walkableView.height - 2*character.height
                                )
                                //Log.d("Debug", "Character at:(${character.x},${character.y}); Pos checking (${walkableView.x} - ${walkableView.x + walkableView.width} )")
                            } else {
                                Log.d("Debug", "Colliding with tag:${walkableView.tag}")
                            }
                            // You can break the loop if you want to handle collisions with only one ImageView
                            isFound = true
                        }
                    }
                    if(isFound){
                        break
                    }
                }
            }
        }
    }

    private fun captureBaseline(updown: Float, sides: Float) {
        baselineSides = sides
        baselineUpdown = updown
    }

    private fun lerp(start: Float, stop: Float, amount: Float): Float {
        return start + amount * (stop - start)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Not necessary to implement
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}
