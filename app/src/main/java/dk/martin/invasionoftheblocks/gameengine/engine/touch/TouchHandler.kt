package dk.kea.androidgame.martin.myfirstgameengine.engine.touch

interface TouchHandler {
    fun isTouchDown(pointer: Int): Boolean
    fun getTouchX(pointer: Int): Int
    fun getTouchY(pointer: Int): Int
}
