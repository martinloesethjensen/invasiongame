package dk.kea.androidgame.martin.myfirstgameengine.engine.core

import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine

abstract class Screen protected constructor(protected open val gameEngine: GameEngine) {

    abstract fun update(deltaTime: Float)

    abstract fun pause()

    abstract fun resume()

    abstract fun dispose()
}
