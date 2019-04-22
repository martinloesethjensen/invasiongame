package dk.martin.invasionoftheblocks.invasionoftheblocks.model

class Bullet {
    companion object {
        const val WIDTH = 9
        const val HEIGHT = 9
    }

    var x: Float = (160 + WIDTH / 2).toFloat()
    var y = 365
    var speed = 5
    var degrees = 0f
}
