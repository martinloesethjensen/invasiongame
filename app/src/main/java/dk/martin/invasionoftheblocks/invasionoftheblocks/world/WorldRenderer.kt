package dk.martin.invasionoftheblocks.invasionoftheblocks.world

import android.graphics.Bitmap
import android.graphics.Matrix
import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine

class WorldRenderer(var gameEngine: GameEngine, var world: World) {
    // Bitmaps
    private var canonImage = gameEngine.loadBitmap("invasionoftheblocks/canon.png")
    private var laserImage = gameEngine.loadBitmap("invasionoftheblocks/laser.png")
    private lateinit var heartImage: Bitmap
    private lateinit var enemyBlockImage: Bitmap

    // for rotation
    private var matrix = Matrix()
    private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(canonImage, 50, 71, true)
    var degrees = 0f // maybe no use?

    init {
        render()
    }

    fun render() {
        drawRotatedCanon()
        drawHeartImage()
        drawLaserShots()
    }

    private fun drawLaserShots() {
        // Log.d("WorldRenderer", "Shots fired: ${world.laserShots.size}")
        for (shot in world.laserShots) {
            gameEngine.drawBitmap(laserImage, shot.x.toFloat(), shot.y.toFloat())
        }
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

        gameEngine.drawBitmap(rotatedBitmap, ((160) - canonImage.width / 2).toFloat(), 365f)
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

        // calculate the degree of rotation and return the degree
        return Math.round(
            Math.atan2(
                values[Matrix.MSKEW_X].toDouble(),
                values[Matrix.MSCALE_X].toDouble()
            ) * (180 / Math.PI)
        ).toFloat()
    }
}