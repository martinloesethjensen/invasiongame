package dk.martin.invasionoftheblocks.invasionoftheblocks.screen

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

    private var passedTime = 0f
    private var tempTime = 0f
    private var startTime: Long = 0

    init {
        startTime = System.nanoTime()
    }

    override fun update(deltaTime: Float) {

//        if ((state === State.RUNNING
//                    && gameEngine.getTouchY(0) > 420
//                    && gameEngine.getTouchX(0) > (320 - 60))
//        ) {
//        }

        if (state === State.RUNNING) {
            //Log.d("GameScreen", "Start time: ${passedTime - tempTime}")
            passedTime += deltaTime

            if (passedTime > 2.5f && world.bullets.size < world.maxShots) {
                passedTime -= 2.5f
                world.isShot = true
                world.addShotToList()
                startTime = System.nanoTime()
            }

            world.update(deltaTime = deltaTime, accelerometerX = gameEngine.accelerometer[0])
        }

        gameEngine.drawBitmap(background, 0f, 0f)
        worldRenderer.render()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}