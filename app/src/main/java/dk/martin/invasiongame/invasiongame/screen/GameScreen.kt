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

    private var background = gameEngine.loadBitmap("invasiongame/background.png")
    private var resume = gameEngine.loadBitmap("invasiongame/resumeplay.png")
    private var pause = gameEngine.loadBitmap("invasiongame/pause.png")
    private var gameOver = gameEngine.loadBitmap("invasiongame/gameover.png")

    private var state = State.RUNNING

    // Sounds variables below here
    private val music: Music = gameEngine.loadMusic("engine/music.ogg")

    private var world = World(gameEngine, object : CollisionListener {
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

        //if (world.lostLife) pause()

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

//        if ((state === State.RUNNING
//                    && gameEngine.getTouchY(0) > 420
//                    && gameEngine.getTouchX(0) > (320 - 60))) {
//            pause()
//            return
//        }

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

        if (state === State.GAME_OVER) {
            drawFlashingGameOver()
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
        if (state === State.RUNNING) state = State.PAUSED
        music.pause()

    }

    override fun resume() {
        if (state === State.PAUSED) state = State.RUNNING
        music.play()
    }

    override fun dispose() {
        music.dispose()
    }
}