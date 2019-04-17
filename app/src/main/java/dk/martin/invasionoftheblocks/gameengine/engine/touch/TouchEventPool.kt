package dk.kea.androidgame.martin.myfirstgameengine.engine.touch

import dk.kea.androidgame.martin.myfirstgameengine.engine.core.Pool

class TouchEventPool : Pool<TouchEvent>() {
    override fun newItem(): TouchEvent {
        return TouchEvent()
    }
}
