package dk.martin.invasionoftheblocks.invasionoftheblocks.world

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine

class WorldRenderer(var gameEngine: GameEngine, var world: World) {
    private var canonImage = gameEngine.loadBitmap("invasionoftheblocks/canon.png")
    var enemyBlocks = gameEngine.loadBitmap("invasionoftheblocks/block1.png")
    var matrix = Matrix()
    private val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(
        canonImage,
        50, 71, true
    )


    init {
        render()
    }

    fun render() {
        Log.d("WorldRenderer", "Degrees: ${getDegreeFromMatrixValues()}")

        val degrees = getDegreeFromMatrixValues()

        // boundaries
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

        val rotatedBitmap =
            Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)

        gameEngine.drawBitmap(rotatedBitmap, ((160) - canonImage.width / 2).toFloat(), 365f)
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