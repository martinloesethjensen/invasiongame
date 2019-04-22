package dk.martin.invasionoftheblocks.invasionoftheblocks.model

class Bullet {
    companion object {
        const val WIDTH = 9
        const val HEIGHT = 9
        const val START_Y = 365
        const val START_X = 160 + WIDTH / 2
    }

    var x: Float = START_X.toFloat()
    var y = START_Y
    var speed = 6
    var degrees = 0f
}
