package dk.martin.invasiongame.gameengine.engine.core

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import dk.martin.invasiongame.gameengine.engine.sound.Music
import dk.martin.invasiongame.gameengine.engine.sound.Sound
import dk.martin.invasiongame.gameengine.engine.touch.MultiTouchHandler
import dk.martin.invasiongame.gameengine.engine.touch.TouchEvent
import dk.martin.invasiongame.gameengine.engine.touch.TouchEventPool
import dk.martin.invasiongame.gameengine.engine.touch.TouchHandler
import java.io.IOException
import java.io.InputStream
import java.util.*


abstract class GameEngine : Activity(), Runnable, TouchHandler, SensorEventListener {
    private var mainLoopThread: Thread? = null
    private var state = State.PAUSED
    private var surfaceView: SurfaceView? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var canvas: Canvas? = null
    private var screen: Screen? = null
    private var offScreenSurface: Bitmap? = null
    private var touchHandler: MultiTouchHandler? = null
    private val touchEventPool = TouchEventPool()
    private val touchEventBuffer = ArrayList<TouchEvent>()
    private val touchEventCopied = ArrayList<TouchEvent>()
    val accelerometer = FloatArray(3) // to hold the g-forces in three dimensions, x, y, and z
    private val soundPool = SoundPool.Builder()
            .setMaxStreams(20)
            .build()
    val framePerSecond: Int = 0
    private var currentTime: Long = 0
    private var lastTime: Long = 0
    private var paint = Paint()
    lateinit var music: Music

    val frameBufferWidth: Int
        get() = offScreenSurface!!.width

    val frameBufferHeight: Int
        get() = offScreenSurface!!.height

    private var source = Rect()
    private var destination = Rect()

    abstract fun createStartScreen(): Screen

    fun setScreen(screen: Screen) {
        if (this.screen != null) this.screen!!.dispose()
        this.screen = screen
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Prepared variables used for drawing on screen
        surfaceView = SurfaceView(this)
        setContentView(surfaceView) // places view on the physical screen
        surfaceHolder = surfaceView!!.holder
        screen = createStartScreen()
        touchHandler = MultiTouchHandler(surfaceView!!, touchEventBuffer, touchEventPool)
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size != 0) {
            val accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)[0]
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }

        volumeControlStream = AudioManager.STREAM_MUSIC // uses the volume the user already has set on his phone
    }

    private fun setOffScreenSurface(width: Int, height: Int) {
        if (offScreenSurface != null) offScreenSurface!!.recycle()
        offScreenSurface = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        canvas = Canvas(offScreenSurface!!)
    }

    fun loadBitmap(fileName: String): Bitmap {
        var inputStream: InputStream? = null
        val bitmap: Bitmap?
        try {
            inputStream = assets.open(fileName)
            bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap == null) {
                throw RuntimeException("Couldn't load bitmap from file $fileName")
            }
            return bitmap
        } catch (ioe: IOException) {
            throw RuntimeException("Couldn't load bitmap from assets folder: $fileName")
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (ioe: IOException) {
                    throw RuntimeException("Couldn't close the inputstream")
                }

            }
        }
    }

    fun drawBitmap(bitmap: Bitmap, x: Float, y: Float) {
        if (canvas != null) canvas!!.drawBitmap(bitmap, x, y, null)
    }

    fun drawBitmap(bitmap: Bitmap, x: Int, y: Int, sourceX: Int, sourceY: Int, sourceWidth: Int, sourceHeight: Int) {
        if (canvas != null) {
            source.left = sourceX
            source.top = sourceY
            source.right = sourceX + sourceWidth
            source.bottom = sourceY + sourceHeight

            destination.left = x
            destination.top = y
            destination.right = x + sourceWidth
            destination.bottom = y + sourceHeight

            canvas!!.drawBitmap(bitmap, source, destination, null)
        }
    }

    fun clearFrameBuffer(color: Int) {
        canvas!!.drawColor(color)
    }

    fun loadSound(filename: String): Sound {
        try {
            val assetFileDescriptor = assets.openFd(filename)
            val soundId = soundPool.load(assetFileDescriptor, 0)
            return Sound(soundPool, soundId)
        } catch (e: IOException) {
            throw RuntimeException("Could not load sound file: $filename")
        }
    }

    fun loadMusic(filename: String): Music {
        try {
            val assetFileDescriptor = assets.openFd(filename)
            return Music(assetFileDescriptor)
        } catch (e: IOException) {
            throw RuntimeException("Could not load music file: $filename")
        }
    }

    fun loadFont(filename: String): Typeface {
        val font = Typeface.createFromAsset(assets, filename)
        if (font === null) {
            throw java.lang.RuntimeException("Oh shit, I could not load font from file: $filename")
        }
        return font
    }

    fun drawText(font: Typeface, text: String, x: Float, y: Float, color: Int, size: Float) {
        paint.typeface = font
        paint.textSize = size
        paint.color = color
        canvas?.drawText(text, x, y, paint)
    }

    override fun isTouchDown(pointer: Int): Boolean {
        return touchHandler!!.isTouchDown(pointer)
    }

    fun getTouchEvents(): List<TouchEvent> {
        return touchEventCopied
    }

    /**
     * @return int as a scaled x
     */
    override fun getTouchX(pointer: Int): Int {
        return (touchHandler!!.getTouchX(pointer).toFloat() * offScreenSurface!!.width.toFloat() / surfaceView!!.width.toFloat()).toInt()
    }

    /**
     * @return int as a scaled y
     */
    override fun getTouchY(pointer: Int): Int {
        return (touchHandler!!.getTouchY(pointer).toFloat() * offScreenSurface!!.height.toFloat() / surfaceView!!.height.toFloat()).toInt()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        System.arraycopy(sensorEvent.values, 0, accelerometer, 0, 3)
    }

    private fun fillEvents() {
        synchronized(this) {
            this.touchEventCopied.addAll(this.touchEventBuffer)
            this.touchEventBuffer.clear()
        }
    }

    private fun freeEvents() {
        synchronized(touchEventCopied) {
            for (touchEvent in touchEventCopied) {
                touchEventPool.free(touchEvent)
            }
            touchEventCopied.clear()
        }
    }

    override fun run() {
        while (true) {
            synchronized(STATE_CHANGES) {
                for (i in STATE_CHANGES.indices) {
                    this.state = STATE_CHANGES[i]
                    if (this.state === State.DISPOSED) {
                        //                        Log.d("GameEngine", "State changed to Disposed");
                        return
                    }
                    if (this.state === State.PAUSED) {
                        //                        Log.d("GameEngine", "State changed to Pause");
                        return
                    }
                    if (this.state === State.RESUMED) {
                        //                        Log.d("GameEngine", "State changed to Resumed");
                        this.state = State.RUNNING
                    }
                } // end of for loop
                STATE_CHANGES.clear()
                if (this.state === State.RUNNING) {
//                    Log.d("GameEngine running", "" + surfaceHolder?.surface?.isValid);
                    if (!surfaceHolder!!.surface.isValid) {
                        return@synchronized // continue
                    }
                    val canvas = surfaceHolder!!.lockCanvas()
                    // all drawing happens here
                    //canvas.drawColor(Color.rgb(0, 0, 255));
                    fillEvents()
                    this.currentTime = System.nanoTime()
                    if (screen != null) screen!!.update((this.currentTime - this.lastTime) / 1_000_000_000.0f)
                    this.lastTime = this.currentTime
                    freeEvents()
                    source.left = 0
                    source.top = 0
                    source.right = offScreenSurface!!.width - 1
                    source.bottom = offScreenSurface!!.height - 1
                    destination.left = 0
                    destination.top = 0
                    destination.right = surfaceView!!.width
                    destination.bottom = surfaceView!!.height
                    canvas.drawBitmap(offScreenSurface!!, source, destination, null)
                    surfaceHolder!!.unlockCanvasAndPost(canvas)
                }
            }
        } // end of while loop
    }

    override fun onPause() {
        super.onPause()
        synchronized(STATE_CHANGES) {
            if (isFinishing) {
                STATE_CHANGES.add(STATE_CHANGES.size, State.DISPOSED)
            } else
                STATE_CHANGES.add(STATE_CHANGES.size, State.PAUSED)
        }
        if (isFinishing) {
            (getSystemService(Context.SENSOR_SERVICE) as SensorManager).unregisterListener(this)
            soundPool.release()
        }
    }

    override fun onResume() {
        super.onResume()
        if (surfaceView!!.width > surfaceView!!.height) {
            setOffScreenSurface(480, 320)
        } else
            setOffScreenSurface(320, 480)
        mainLoopThread = Thread(this)
        mainLoopThread!!.start() // starts the thread
        synchronized(STATE_CHANGES) {
            STATE_CHANGES.add(STATE_CHANGES.size, State.RESUMED)
        }
    }

    companion object {
        private val STATE_CHANGES = ArrayList<State>()
    }
}
