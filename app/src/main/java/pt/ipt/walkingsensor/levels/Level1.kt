package pt.ipt.walkingsensor.levels

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ipt.WalkingSensorGame.R
import java.sql.DriverManager.println
import kotlin.math.floor


//Para duvidas verificar no video https://www.youtube.com/watch?v=xcsuDDQHrLo&ab_channel=Indently

class Level1 : AppCompatActivity(), SensorEventListener {

    private lateinit var walkableMatrix: Array<Array<Int>>
    private lateinit var sensorManager: SensorManager
    private lateinit var character: ImageView
    private lateinit var mainGame: ImageView
    private lateinit var texto: TextView
    private var animSet = AnimatorSet()
    private var unitX: Float? = null
    private var unitY: Float? = null
    private var baseX: Int = 0
    private var baseY: Int = 0
    private var screenWidth: Float = 0F
    private var screenHeight: Float = 0F
    private val smoothness = 0.2f
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

        //Setup the game
        setupGame()
        setupCordSystem()

        //Start the game
        //Play fade-in animation
        fadeAnimations()

        //setupSensors()
    }
    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    private fun fadeAnimations() {

        //setup Black Screen
        setupBlackScreen()

        //Vars
        var currentDuration = 0L
        val blackScreen = findViewById<TextView>(R.id.blankTextView)
        val scrollImage = findViewById<ImageView>(R.id.ImageScroll)
        val scrollText =  findViewById<TextView>(R.id.textViewTypewritter)
        val redNPCImage = findViewById<ImageView>(R.id.imageViewCharacterRed)
        val blueNPCImage = findViewById<ImageView>(R.id.imageViewCharacterBlue)
        val greenNPCImage = findViewById<ImageView>(R.id.imageViewCharacterGreen)
        character.x = getScreenWidth() / 2f
        character.y = getScreenHeight()* (2 / 3f)

        //Black screen
        val fadeBlackScreen1Half: ValueAnimator = ObjectAnimator.ofFloat(blackScreen, "alpha", 1f, 0.5f)
        fadeBlackScreen1Half.duration = 5_000
        animSet.play(fadeBlackScreen1Half)

        //Play "What is going on" animation
        //Play character going down from the middle of the screen
        //character appears and becomes huge because he's close to the screen

        var playerMessageAnim1 = writeMessage("HAAAAAA!!!  ",3_000)
        val characterOnAnim = ObjectAnimator.ofFloat(character,"alpha",0f,1f)
        characterOnAnim.duration = 500
        animSet.play(characterOnAnim,)
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
        var redNpcOnAnim1 = ObjectAnimator.ofFloat(redNPCImage,"alpha",0f,1f)
        var redNpcOffAnim1 = ObjectAnimator.ofFloat(redNPCImage,"alpha",1f,0f)
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

        fadeBlackScreen2Half.doOnEnd { setupSensors() }
        animSet.play(fadeBlackScreen2Half).after(greenNpcOnAnim3)
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

    fun scaleView(v: View, startXScale: Float, endXScale: Float, startYScale: Float, endYScale: Float,duration: Long) {
        val anim: Animation = ScaleAnimation(
            startXScale, endXScale,  // Start and end values for the X axis scaling
            startYScale, endYScale,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_PARENT, 0.5f,  // Pivot point of X scaling
            Animation.RELATIVE_TO_PARENT, 0.5f  // Pivot point of Y scaling
        )

        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = duration
        v.startAnimation(anim)
    }

    // Animation https://stackoverflow.com/questions/4813995/set-alpha-opacity-of-layout
    private fun setupBlackScreen() {
        val blackScreen = findViewById<TextView>(R.id.blankTextView)
        blackScreen.x = 0f
        blackScreen.y = 0f
        blackScreen.width = getScreenWidth()
        blackScreen.height = getScreenHeight()


    }

    private fun setupGame() {
        character = findViewById(R.id.character)
        mainGame = findViewById(R.id.IslandsImageView)
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
            val a = character.height
            baseX = mainGameLocation[0]
            baseY = mainGameLocation[1] + 32

            //baseY = (mainGame.height - screenHeight).toInt()
            //baseY = (screenHeight - mainGame.height).toInt() * -1
        }


        character.post{
            //Definir tamanho da personagem baseado no resizing

            val mapHeight = 736
            val mapWidth = 416
            val xratio = screenWidth
            val yratio = screenHeight

            //Definir tiles
            unitX = (character.width/2f)
            unitY = (character.height/2f)

            character.layoutParams.width = (character.width * (1- (mapWidth/screenWidth))).toInt()
            character.layoutParams.height = (character.height * (1- (mapHeight/screenHeight))).toInt()
            character.requestLayout();

            //Cordenadas teste
            val cordX = 0
            val cordY = 0

            //Desvios para centrar
            val desvioX = character.width/(1/3f)
            val desvioY = character.height*(2/3f)

            val valX =getXCordOnScreen(cordX)
            val valY =getYCordOnScreen(cordY)
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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = -event.values[0]
            val updown = event.values[1]

            if (isFirstTime) {
                // Capture baseline values on the first sensor change event
                captureBaseline(updown, sides)
                isFirstTime = false
            }

            if (unitX != null && unitY != null){
                val adjustedSides = (sides - baselineSides) * sensitivity
                val adjustedUpdown = (updown - baselineUpdown) * sensitivity

                val targetX = character.translationX + adjustedSides
                val targetY = character.translationY + adjustedUpdown

                // Limit character position to the screen boundaries
                //character.translationX = character.translationX.coerceIn(0f, mainGame.width.toFloat())
                //character.translationY = character.translationY.coerceIn(0f, mainGame.height.toFloat())

                //Check for colisions
                if(!isCollidingWithWall(targetX, targetY)) {

                    // If no collision, update the character's position with smoothness
                    character.translationX = lerp(character.translationX, targetX, smoothness)
                    character.translationY = lerp(character.translationY, targetY, smoothness)

                    // Limit character position to the screen boundaries
                    character.translationX = character.translationX.coerceIn(0f, mainGame.width.toFloat() - character.width)
                    character.translationY = character.translationY.coerceIn(0f, mainGame.height.toFloat() - character.height)

                }

                //texto.text = "up/down ${adjustedUpdown.toInt()}\nleft/right ${adjustedSides.toInt()}"

            }
        }
    }

    private fun getXCordOnScreen(x: Int): Float{
        return baseX + unitX!!.times(x)
    }
    private fun getYCordOnScreen(y: Int): Float{
        return baseY + unitY!!.times(y)
    }

    private fun getXCord(valX: Float): Float{
        return floor((valX -baseX)/ unitX!!)
    }

    private fun getYCord(valY: Float) : Float{
        return floor((valY - baseY) / unitY!!)
    }

    private fun captureBaseline(updown: Float, sides: Float) {
        baselineSides = sides
        baselineUpdown = updown
    }

    private fun isCollidingWithWall(targetX: Float, targetY: Float): Boolean {
        //Get player location and cords
        val characterLocation = IntArray(2)
        val characterLocation2 = IntArray(2)
        character.getLocationInWindow(characterLocation)
        character.getLocationOnScreen(characterLocation2)

        if(characterLocation[0]<0 || characterLocation[1]<0){
            return  true
        }
        val valX = characterLocation[0]*1f
        val valY = characterLocation[1]*1f
        val cordX = getXCord(valX)
        val cordY = getYCord(valY)
        //texto.text = "Cords : $cordX, $cordY."
        println("Cords : $cordX, $cordY.")

        //Get player orientation and next cordXY
        val playerOriX = (targetX / (character.width*1f/2f)).toInt()
        val playerOriY = (targetY / (character.height*1f/2f)).toInt()
        println("orientation : $playerOriX, $playerOriY.")

        //Check if next walkable[cordXY] is 1


        return false // No collision with any wall
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
