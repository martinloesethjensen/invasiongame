package dk.martin.invasionoftheblocks.invasionoftheblocks.screen

import dk.kea.androidgame.martin.myfirstgameengine.engine.core.Screen
import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine

class MainMenuScreen(gameEngine: GameEngine) : Screen(gameEngine = gameEngine) {
    private var background = gameEngine.loadBitmap("invasionoftheblocks/background.png")
    private var startGame = gameEngine.loadBitmap("invasionoftheblocks/resumeplay.png")
    private var passedTime = 0f
    private var startTime: Long = 0

    init {
        startTime = System.nanoTime()
    }

    override fun update(deltaTime: Float) {
        if (gameEngine.isTouchDown(0) && passedTime > 0.5f) {
            gameEngine.setScreen(GameScreen(gameEngine = gameEngine))
            return
        }

        gameEngine.drawBitmap(background, 0f, 0f)
        passedTime += deltaTime

        if (passedTime - passedTime.toInt() > 0.5f) {
            gameEngine.drawBitmap(
                startGame, (160 - startGame.width / 2).toFloat(),
                (240 - startGame.height / 2).toFloat()
            )
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}