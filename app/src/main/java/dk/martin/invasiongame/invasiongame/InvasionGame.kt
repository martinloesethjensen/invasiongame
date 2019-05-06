package dk.martin.invasiongame.invasiongame

import dk.martin.invasiongame.gameengine.engine.core.GameEngine
import dk.martin.invasiongame.gameengine.engine.core.Screen
import dk.martin.invasiongame.invasiongame.screen.MainMenuScreen

class InvasionGame : GameEngine() {
    override fun createStartScreen(): Screen {
        return MainMenuScreen(this)
    }

}
