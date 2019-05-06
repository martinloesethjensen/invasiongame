package dk.martin.invasiongame.invasiongame.model

import kotlin.random.Random

class Enemy(
    var x: Int = Random.nextInt(from = WIDTH, until = 320 - WIDTH),
    var y: Float = 0f - HEIGHT,
    var speed: Int = 0
) {
    companion object {
        const val WIDTH = 50
        const val HEIGHT = 50
    }

    var hit = 1 // default -- used for showing their state of being dead
    var isDead = false
}
