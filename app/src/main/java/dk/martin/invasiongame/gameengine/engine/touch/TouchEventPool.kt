package dk.martin.invasiongame.gameengine.engine.touch

import dk.martin.invasiongame.gameengine.engine.core.Pool

class TouchEventPool : Pool<TouchEvent>() {
    override fun newItem(): TouchEvent {
        return TouchEvent()
    }
}
