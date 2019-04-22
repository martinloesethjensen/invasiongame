package dk.martin.invasionoftheblocks.invasionoftheblocks.world

import android.util.Log
import dk.martin.invasionoftheblocks.gameengine.engine.core.GameEngine
import dk.martin.invasionoftheblocks.invasionoftheblocks.CollisionListener
import dk.martin.invasionoftheblocks.invasionoftheblocks.model.Bullet
import dk.martin.invasionoftheblocks.invasionoftheblocks.model.Enemy
import dk.martin.invasionoftheblocks.invasionoftheblocks.model.Eye

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
    var canon = Eye()
    var isShot = false
    var shots = arrayListOf<Bullet>()
    var maxShots = 4

    init {
//        initEnemies()
    }

    fun update(deltaTime: Float, accelerometerX: Float) {
        canon.acceleratorX = accelerometerX * deltaTime
        canon.rotate = canon.acceleratorX * 25

        for (shot in shots) {
            if (shot.y < 0 - Bullet.HEIGHT) {
                shot.x = (160 + Bullet.WIDTH / 2).toFloat()
                shot.y = 365
                shot.speed = 5
                Log.d("World", "Shot was recycled.")
            }
            shot.speed += (shot.speed * deltaTime).toInt()
            shot.y -= (shot.speed * deltaTime * 25).toInt()
        }
    }

    fun addShotToList() {
        if (isShot) shots.add(Bullet())
        isShot = false
    }

    private fun initEnemies() {

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}