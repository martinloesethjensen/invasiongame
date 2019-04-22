package dk.martin.invasionoftheblocks.invasionoftheblocks.world

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine
import dk.martin.invasionoftheblocks.invasionoftheblocks.model.Bullet
import dk.martin.invasionoftheblocks.invasionoftheblocks.model.Eye

class WorldRenderer(var gameEngine: GameEngine, var world: World) {
    // Bitmaps
    private var eyeImage = gameEngine.loadBitmap("invasionoftheblocks/eye.png")
    private var bulletImage = gameEngine.loadBitmap("invasionoftheblocks/bullet.png")
    private lateinit var heartImage: Bitmap
    private lateinit var enemyBlockImage: Bitmap

    // for rotation
    private var matrix = Matrix()
    private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(
        eyeImage,
        Eye.WIDTH,
        Eye.HEIGHT,
        true
    )
    var degrees = 0f

    init {
        render()
    }

    fun render() {
        drawRotatedCanon()
        drawHeartImage()
        drawLaserShots()
    }

    private fun drawLaserShots() {
        // Log.d("WorldRenderer", "Shots fired: ${world.shots.size}")
        for (shot in world.shots) {
            //Log.d("Shot", "Shot y axis: ${shot.y}")
            if (shot.y >= 355) shot.degrees = degrees
            val tempMatrix = Matrix()
            tempMatrix.postRotate(-shot.degrees)
            val scaledLaserBitmap: Bitmap = Bitmap.createScaledBitmap(bulletImage, Bullet.WIDTH, Bullet.HEIGHT, true)
            val rotatedLaserBitmap =
                Bitmap.createBitmap(
                    scaledLaserBitmap,
                    0,
                    0,
                    scaledLaserBitmap.width,
                    scaledLaserBitmap.height,
                    tempMatrix,
                    true
                )

            shot.x += Math.tan(-shot.degrees.toDouble()).toFloat()

            Log.d("Shot", "${Math.tan(-shot.degrees.toDouble()).toInt()}")

            gameEngine.drawBitmap(
                rotatedLaserBitmap,
                shot.x,
                shot.y.toFloat()
            )
        }
    }

    private fun calculateLaserXPath(degrees: Float, x: Float): Float {
        Log.d(
            "Tan(v)", "${Math.round(
                Math.tan(degrees.toDouble())
            ).toFloat()}"
        )
        return x
//        + Math.round(
//            Math.tan(degrees.toDouble())
//        ).toFloat()
    }

    private fun drawHeartImage() {
        if (world.lives != 0 || world.lives > 3) heartImage =
            gameEngine.loadBitmap("invasionoftheblocks/heart${world.lives}.png")
        gameEngine.drawBitmap(heartImage, (160 - heartImage.width / 2).toFloat(), 430f)
    }

    private fun drawRotatedCanon() {
        //        Log.d("WorldRenderer", "Degrees: ${getDegreeFromMatrixValues()}")

        degrees = getDegreeFromMatrixValues()

        checkBoundaries(degrees)

        val rotatedBitmap =
            Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)

        gameEngine.drawBitmap(rotatedBitmap, ((160) - eyeImage.width / 2).toFloat(), 365f)
    }

    private fun checkBoundaries(degrees: Float) {
        if (degrees > -88 && degrees < 88) {
            matrix.postRotate(
                -world.canon.rotate,
                (scaledBitmap.width / 2).toFloat(),
                (scaledBitmap.height / 2).toFloat()
            )
        } else if (degrees > 0) {
            matrix.postRotate((degrees - (degrees - 0.1)).toFloat())
        } else {
            matrix.postRotate((degrees - (degrees + 0.1)).toFloat())
        }
    }

    private fun getDegreeFromMatrixValues(): Float {
        val values = FloatArray(9)
        matrix.getValues(values)

        // calculate the degrees of rotation and return the degrees
        return Math.round(
            Math.atan2(
                values[Matrix.MSKEW_X].toDouble(),
                values[Matrix.MSCALE_X].toDouble()
            ) * (180 / Math.PI)
        ).toFloat()
    }
}