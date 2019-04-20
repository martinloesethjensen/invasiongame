package dk.martin.invasionoftheblocks.invasionoftheblocks

import dk.kea.androidgame.martin.myfirstgameengine.engine.core.Screen
import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine
import dk.martin.invasionoftheblocks.invasionoftheblocks.screen.MainMenuScreen

class InvasionOfTheBlocks : GameEngine() {
    override fun createStartScreen(): Screen {
        return MainMenuScreen(this)
    }

}
