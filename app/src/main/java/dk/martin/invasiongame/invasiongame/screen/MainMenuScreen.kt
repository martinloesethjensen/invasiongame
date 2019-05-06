package dk.martin.invasiongame.invasiongame.screen

import dk.martin.invasiongame.gameengine.engine.core.GameEngine
import dk.martin.invasiongame.gameengine.engine.core.Screen
import dk.martin.invasiongame.gameengine.engine.sound.Music

class MainMenuScreen(gameEngine: GameEngine) : Screen(gameEngine = gameEngine) {
    private var background = gameEngine.loadBitmap("invasiongame/background.png")
    private var startGame = gameEngine.loadBitmap("invasiongame/resumeplay.png")
    private val music: Music = gameEngine.loadMusic("engine/music.ogg")
    private var passedTime = 0f
    private var startTime: Long = 0

    init {
        startTime = System.nanoTime()
        music.play()
        music.isLooping = true
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
        music.pause()
    }

    override fun resume() {
        music.play()
    }

    override fun dispose() {
        music.dispose()
    }
}