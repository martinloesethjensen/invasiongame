package dk.martin.invasionoftheblocks.invasionoftheblocks.screen

import android.graphics.Bitmap
import dk.kea.androidgame.martin.myfirstgameengine.engine.core.Screen
import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine
import dk.martin.invasionoftheblocks.invasionoftheblocks.CollisionListener
import dk.martin.invasionoftheblocks.invasionoftheblocks.world.World
import dk.martin.invasionoftheblocks.invasionoftheblocks.world.WorldRenderer

class GameScreen(gameEngine: GameEngine) : Screen(gameEngine = gameEngine) {
    enum class State {
        PAUSED,
        RUNNING,
        GAME_OVER
    }

    private var background = gameEngine.loadBitmap("invasionoftheblocks/background.png")
    private lateinit var heart: Bitmap
    // private var resume = gameEngine.loadBitmap("")  // todo: implement pics
    //private var pause = gameEngine.loadBitmap("") // todo: implement pics
    //private var gameOver = gameEngine.loadBitmap("") // todo: implement pics

    private var state = State.RUNNING

    // Sounds variables below here

    private var world = World(gameEngine, object : CollisionListener {
        override fun collisionRoadSide() {
        }

        override fun collisionMonster() {
        }

        override fun gameOver() {
        }
    })

    private var worldRenderer = WorldRenderer(gameEngine, world)

    override fun update(deltaTime: Float) {
        gameEngine.drawBitmap(background, 0f, 0f)

        if (world.lives != 0 || world.lives > 3) heart =
            gameEngine.loadBitmap("invasionoftheblocks/heart${world.lives}.png")
        gameEngine.drawBitmap(heart, (160 - heart.width / 2).toFloat(), 430f)

        if (state === State.RUNNING) {
            world.update(deltaTime = deltaTime, accelerometerX = gameEngine.accelerometer[0])
        }
        worldRenderer.render()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}