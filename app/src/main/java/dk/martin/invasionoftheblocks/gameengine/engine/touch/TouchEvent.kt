package dk.kea.androidgame.martin.myfirstgameengine.engine.touch

/**
 * Class with responsibilities a touch needs
 */
class TouchEvent {
    internal var type: TouchEventType? = null //The type of the event
    internal var x: Int = 0 //The x-coordinate of the event
    internal var y: Int = 0 //The y-coordinate of the event
    internal var pointer: Int = 0 //The pointer id (from the Android system)

    /***
     * Enum of touch event types
     */
    enum class TouchEventType {
        DOWN,
        UP,
        DRAGGED
    }
}
