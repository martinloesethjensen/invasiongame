package dk.martin.invasiongame.invasiongame.screen

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import dk.martin.invasiongame.gameengine.engine.core.GameEngine
import dk.martin.invasiongame.gameengine.engine.core.Screen
import dk.martin.invasiongame.gameengine.engine.sound.Music
import dk.martin.invasiongame.gameengine.engine.touch.TouchEvent
import dk.martin.invasiongame.invasiongame.CollisionListener
import dk.martin.invasiongame.invasiongame.world.World
import dk.martin.invasiongame.invasiongame.world.WorldRenderer

class GameScreen(gameEngine: GameEngine) : Screen(gameEngine = gameEngine) {
    enum class State {
        PAUSED,
        RUNNING,
        GAME_OVER
    }

    // Bitmaps
    private val background = gameEngine.loadBitmap("invasiongame/background.png")
    private val resume = gameEngine.loadBitmap("invasiongame/resumeplay.png")
    private val pause = gameEngine.loadBitmap("invasiongame/pause.png")
    private val gameOver = gameEngine.loadBitmap("invasiongame/gameover.png")

    // Bitmap buttons
    private val muteButton = gameEngine.loadBitmap("invasiongame/buttons/mute-button.png")
    private val volumeOnButton = gameEngine.loadBitmap("invasiongame/buttons/volume-on-button.png")
    private val pauseButton = gameEngine.loadBitmap("invasiongame/buttons/pause-button.png")
    private val playButton = gameEngine.loadBitmap("invasiongame/buttons/play-button.png")

    // Mute and paused booleans
    private var isMuted = false
    private var isPaused = false

    // State variable
    private var state = State.RUNNING

    // Sounds variables below here
    private val music: Music = gameEngine.loadMusic("engine/music.ogg")

    private val world = World(gameEngine, object : CollisionListener {
        override fun collisionGround() {
        }

        override fun collisionEnemy() {
        }

        override fun gameOver() {
        }
    })

    private var worldRenderer = WorldRenderer(gameEngine, world)

    private var passedTime = 0f
    private var startTime: Long = 0
    private var font: Typeface = gameEngine.loadFont("engine/font.ttf")
    private var showText = "{{text to show}}"

    init {
        startTime = System.nanoTime()
        music.play()
        music.isLooping = true
    }

    override fun update(deltaTime: Float) {

        if (world.gameOver) state = State.GAME_OVER

        if (state === State.PAUSED && gameEngine.isTouchDown(0)) { // if paused
            state = State.RUNNING
            resume()
        }

        if (state === State.GAME_OVER && gameEngine.isTouchDown(0)) {
            drawFlashingGameOver()

            Log.d("GameScreen", "TouchEvents size: ${gameEngine.getTouchEvents().size}")

            gameEngine.getTouchEvents().forEach { event ->
                Log.d("GameScreen", "TouchEvent type: ${event.type}")
                if (event.type === TouchEvent.TouchEventType.DOWN || event.type === TouchEvent.TouchEventType.DRAGGED) {
                    gameEngine.setScreen(MainMenuScreen(gameEngine))
                    return
                }
            }
        }

        if ((state === State.RUNNING
                    && gameEngine.getTouchY(0) > 440
                    && gameEngine.getTouchX(0) > (320 - 40)
                    && gameEngine.isTouchDown(0))
        ) {
            gameEngine.getTouchEvents().forEach { event ->
                Log.d("GameScreenPause", "TouchEvent type: ${event.type}")
                if (event.type === TouchEvent.TouchEventType.DOWN || event.type === TouchEvent.TouchEventType.DRAGGED) {
                    if (!isPaused) {
                        state = State.PAUSED
                        pause()
                    }

                    gameEngine.drawBitmap(
                        resume, (World.MAX_X / 2 - resume.width / 2).toFloat(),
                        (World.MAX_Y / 2 - resume.height / 2).toFloat()
                    )

                    pauseOnTouch()
                    checkIsPaused()
                    return
                }
            }
        }

        if (state === State.RUNNING) {

            if (passedTime > 1.5f && world.bullets.size < world.maxShots) {
                world.isShot = true
                world.addShotToList()
                startTime = System.nanoTime()
            }

            if (passedTime > 1.5f && world.enemies.size < world.maxEnemies) {
                passedTime -= 2.5f
                world.addEnemyToList()
            }

            world.update(deltaTime = deltaTime, accelerometerX = gameEngine.accelerometer[0])
        }

        passedTime += deltaTime

        gameEngine.drawBitmap(background, 0f, 0f)

        worldRenderer.render()

        showText = "Level: ${world.level}  Points: ${world.points}"
        gameEngine.drawText(font = font, text = showText, x = 5f, y = 460f, color = Color.RED, size = 8f)

        checkIsPaused()
        checkIsMuted()

        if ((state === State.RUNNING
                    && gameEngine.getTouchY(0) > 440
                    && gameEngine.getTouchX(0) > (320 - 85)
                    && gameEngine.getTouchX(0) < (320 - 45)
                    && gameEngine.isTouchDown(0))
        ) {
            gameEngine.getTouchEvents().forEach { event ->
                Log.d("GameScreenMute", "TouchEvent type: ${event.type}")
                if (event.type === TouchEvent.TouchEventType.DOWN) {
                    muteOnTouch()
                    pause()
                    return
                }
            }
            return
        }

        when (state) {
            State.GAME_OVER -> drawFlashingGameOver()
            State.PAUSED -> {
                pause()
                gameEngine.drawBitmap(resume, (160 - resume.width / 2).toFloat(), (240 - resume.height / 2).toFloat())
            }
            else -> return
        }
    }

    private fun muteOnTouch() {
        isMuted = !isMuted
    }

    private fun pauseOnTouch() {
        isPaused = !isPaused
    }

    private fun checkIsMuted() {
        when (isMuted) {
            true -> {
                gameEngine.drawBitmap(
                    muteButton, (World.MAX_X - ((muteButton.width + 5) + (playButton.width + 5))).toFloat(),
                    (World.MAX_Y - (muteButton.height + 5)).toFloat()
                )
                pause()
            }
            else -> {
                gameEngine.drawBitmap(
                    volumeOnButton, (World.MAX_X - ((volumeOnButton.width + 5) + (pauseButton.width + 5))).toFloat(),
                    (World.MAX_Y - (volumeOnButton.height + 5)).toFloat()
                )
                resume()
            }
        }
    }

    private fun checkIsPaused() {
        when (isPaused) {
            true -> {
                gameEngine.drawBitmap(
                    playButton, (World.MAX_X - (playButton.width + 5)).toFloat(),
                    (World.MAX_Y - (playButton.height + 5)).toFloat()
                )
                pause()
            }
            else -> {
                gameEngine.drawBitmap(
                    pauseButton, (World.MAX_X - (pauseButton.width + 5)).toFloat(),
                    (World.MAX_Y - (pauseButton.height + 5)).toFloat()
                )
                resume()
            }
        }
    }

    private fun drawFlashingGameOver() {
        if (passedTime - passedTime.toInt() > 0.5f) {
            gameEngine.drawBitmap(
                gameOver, (World.MAX_X / 2 - gameOver.width / 2).toFloat(),
                (World.MAX_Y / 2 - gameOver.height / 2).toFloat()
            )
        }
    }

    override fun pause() {
        music.pause()
    }

    override fun resume() {
        music.play()
    }

    override fun dispose() {
        music.dispose()
    }
}