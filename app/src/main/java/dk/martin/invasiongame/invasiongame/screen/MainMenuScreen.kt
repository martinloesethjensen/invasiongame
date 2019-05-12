package dk.martin.invasiongame.invasiongame.screen

import dk.martin.invasiongame.gameengine.engine.core.GameEngine
import dk.martin.invasiongame.gameengine.engine.core.Screen
import dk.martin.invasiongame.gameengine.engine.sound.Music
import dk.martin.invasiongame.invasiongame.world.World

class MainMenuScreen(gameEngine: GameEngine) : Screen(gameEngine = gameEngine) {
    private val background = gameEngine.loadBitmap("invasiongame/background.png")
    private val startGame = gameEngine.loadBitmap("invasiongame/resumeplay.png")
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
                startGame, (World.MAX_X / 2 - startGame.width / 2).toFloat(),
                (World.MAX_Y / 2 - startGame.height / 2).toFloat()
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