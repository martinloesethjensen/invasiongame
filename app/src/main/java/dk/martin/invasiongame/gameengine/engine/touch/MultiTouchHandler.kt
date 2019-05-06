package dk.martin.invasiongame.gameengine.engine.touch

import android.view.MotionEvent
import android.view.View


class MultiTouchHandler(view: View, private val touchEventBuffer: MutableList<TouchEvent> // buffer with touch events
                        , private val touchEventPool: TouchEventPool
) : TouchHandler, View.OnTouchListener {
    private val isTouched = BooleanArray(20) // store the first 20 touches
    private val touchX = IntArray(20)
    private val touchY = IntArray(20)

    init {
        view.setOnTouchListener(this)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        var touchEvent: TouchEvent?
        val action = event.action and MotionEvent.ACTION_MASK
        val pointerIndex = event.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
        val pointerId = event.getPointerId(pointerIndex)

        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                touchEvent = touchEventPool.obtains()
                touchEvent.type = TouchEvent.TouchEventType.DOWN
                touchEvent.pointer = pointerId
                touchEvent.x = event.x.toInt()
                touchX[pointerId] = touchEvent.x
                touchEvent.y = event.y.toInt()
                touchY[pointerId] = touchEvent.y
                isTouched[pointerId] = true
                synchronized(touchEventBuffer) {
                    touchEventBuffer.add(touchEvent!!)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                touchEvent = touchEventPool.obtains()
                touchEvent.type = TouchEvent.TouchEventType.UP
                touchEvent.pointer = pointerId
                touchEvent.x = event.x.toInt()
                touchX[pointerId] = touchEvent.x
                touchEvent.y = event.y.toInt()
                touchY[pointerId] = touchEvent.y
                isTouched[pointerId] = false
                synchronized(touchEventBuffer) {
                    touchEventBuffer.add(touchEvent!!)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerCount = event.pointerCount
                synchronized(touchEventBuffer) {
                    for (i in 0 until pointerCount) {
                        touchEvent = touchEventPool.obtains()
                        touchEvent!!.type = TouchEvent.TouchEventType.DRAGGED
                        touchEvent!!.pointer = pointerId
                        touchEvent!!.x = event.x.toInt()
                        touchX[pointerId] = touchEvent!!.x
                        touchEvent!!.y = event.y.toInt()
                        touchY[pointerId] = touchEvent!!.y
                        isTouched[pointerId] = true
                        touchEventBuffer.add(touchEvent!!)
                    }
                }
            }
        }
        return true
    }

    override fun isTouchDown(pointer: Int): Boolean {
        return isTouched[pointer]
    }

    override fun getTouchX(pointer: Int): Int {
        return touchX[pointer]
    }

    override fun getTouchY(pointer: Int): Int {
        return touchY[pointer]
    }
}
