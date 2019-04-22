package dk.martin.invasionoftheblocks.invasionoftheblocks.world

import android.graphics.Bitmap
import android.graphics.Matrix
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
    private val scaledEyeBitmap: Bitmap = Bitmap.createScaledBitmap(
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
        drawRotatedEye()
        drawHeartImage()
        drawLaserShots()
    }

    private fun drawLaserShots() {
        for (shot in world.bullets) {
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

            gameEngine.drawBitmap(
                rotatedLaserBitmap,
                shot.x,
                shot.y.toFloat()
            )
        }
    }

    private fun drawHeartImage() {
        if (world.lives != 0 || world.lives > 3) heartImage =
            gameEngine.loadBitmap("invasionoftheblocks/heart${world.lives}.png")
        gameEngine.drawBitmap(heartImage, (160 - heartImage.width / 2).toFloat(), 430f)
    }

    private fun drawRotatedEye() {
        degrees = getDegreeFromMatrixValues()
        checkBoundaries(degrees)
        val rotatedEyeBitmap =
            Bitmap.createBitmap(scaledEyeBitmap, 0, 0, scaledEyeBitmap.width, scaledEyeBitmap.height, matrix, true)
        gameEngine.drawBitmap(rotatedEyeBitmap, ((160) - eyeImage.width / 2).toFloat(), 365f)
    }

    private fun checkBoundaries(degrees: Float) {
        if (degrees > -50 && degrees < 50) {
            matrix.postRotate(
                -world.canon.rotate,
                (scaledEyeBitmap.width / 2).toFloat(),
                (scaledEyeBitmap.height / 2).toFloat()
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