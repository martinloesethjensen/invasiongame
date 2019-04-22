package dk.martin.invasionoftheblocks.invasionoftheblocks.world

import android.util.Log
import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine
import dk.martin.invasionoftheblocks.invasionoftheblocks.CollisionListener
import dk.martin.invasionoftheblocks.invasionoftheblocks.model.Canon
import dk.martin.invasionoftheblocks.invasionoftheblocks.model.Enemy
import dk.martin.invasionoftheblocks.invasionoftheblocks.model.Laser

class World(
    var gameEngine: GameEngine,
    var collisionListener: CollisionListener
) {

    //Todo change values to fit the screen
    companion object {
        const val MIN_X = 0
        const val MAX_X = 320
        const val MIN_Y = 0
        const val MAX_Y = 480
    }

    var lives = 3
    var enemies = arrayListOf<Enemy>()
    var maxEnemies = 10
    var gameOver = false
    var points = 0
    var canon = Canon()
    var isShot = false
    var laserShots = arrayListOf<Laser>()
    var maxLaserShots = 4

    init {
//        initEnemies()
    }

    fun update(deltaTime: Float, accelerometerX: Float) {
        canon.acceleratorX = accelerometerX * deltaTime
        canon.rotate = canon.acceleratorX * 25

        for (shot in laserShots) {
            if (shot.y < 0 - Laser.HEIGHT) {
                shot.x = 160 + Laser.WIDTH / 2
                shot.y = 365
                shot.speed = 5
                Log.d("World", "Shot was recycled.")
            }
            shot.speed += (shot.speed * deltaTime).toInt()
            shot.y -= (shot.speed * deltaTime * 25).toInt()
        }
    }

    fun addShotToList() {
        if (isShot) laserShots.add(Laser())
        isShot = false
    }

    private fun initEnemies() {

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}