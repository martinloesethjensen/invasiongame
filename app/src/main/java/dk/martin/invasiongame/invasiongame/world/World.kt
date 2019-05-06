package dk.martin.invasiongame.invasiongame.world

import android.util.Log
import dk.martin.invasiongame.gameengine.engine.core.GameEngine
import dk.martin.invasiongame.invasiongame.CollisionListener
import dk.martin.invasiongame.invasiongame.model.Bullet
import dk.martin.invasiongame.invasiongame.model.Enemy
import dk.martin.invasiongame.invasiongame.model.Eye
import kotlin.random.Random

class World(
    var gameEngine: GameEngine,
    var collisionListener: CollisionListener
) {
    companion object {
        const val MIN_X = 0
        const val MAX_X = 320
        const val MIN_Y = 0
        const val MAX_Y = 480
    }

    var level = 1
    var hits = 0
    var points = 0
    var lostLife = false
    var lives = 3
    var enemies = arrayListOf<Enemy>()
    var maxEnemies = 5
    var gameOver = false
    var canon = Eye()
    var isShot = false
    var bullets = arrayListOf<Bullet>()
    var maxShots = 4
    var enemyMaxSpeed = 4

    fun update(deltaTime: Float, accelerometerX: Float) {

        Log.d("World", "Level: $level")
        Log.d("World", "Hits: $hits")
        Log.d("World", "Points: $points")
        Log.d("World", "Lives: $lives")

        updateCanon(deltaTime = deltaTime, accelerometerX = accelerometerX)

        updateBullets(deltaTime = deltaTime)

        updateEnemies(deltaTime = deltaTime)

        checkCollisionBulletAndEnemy()
    }

    private fun updateCanon(deltaTime: Float, accelerometerX: Float) {
        canon.acceleratorX = accelerometerX * deltaTime
        canon.rotate = canon.acceleratorX * 25
    }

    private fun updateBullets(deltaTime: Float) {

        bullets.forEach { bullet ->

            // recycle the bullet if it goes off the boundaries
            if (bullet.y < 0 - Bullet.HEIGHT || bullet.x > 320 + Bullet.WIDTH || bullet.x < 0 - Bullet.WIDTH || bullet.shotExhausted) {
                bullet.x = (160 + Bullet.WIDTH / 2).toFloat()
                bullet.y = Bullet.START_Y
                bullet.speed = 5
                bullet.shotExhausted = false
            }

            // updates the speed and the y coordinate of the bullet
            bullet.speed += (bullet.speed * deltaTime).toInt()
            bullet.y -= (bullet.speed * deltaTime * 25).toInt()

            // updates the x coordinate according to the degrees of the bullet
            if (-bullet.degrees > 0) {
                bullet.x += (Math.sin(90 + (-bullet.degrees.toDouble())) * deltaTime + ((-bullet.degrees)) / 10).toFloat()
            } else if (-bullet.degrees < 0) {
                bullet.x += (Math.sin(90 - (-bullet.degrees.toDouble())) * deltaTime - ((bullet.degrees)) / 10).toFloat()
            }
        }
    }

    private fun updateEnemies(deltaTime: Float) {
        enemies.forEach { enemy ->
            if (enemy.isDead) {
                enemy.x = Random.nextInt(Enemy.WIDTH, 320 - Enemy.WIDTH)
                enemy.y = 0f - Enemy.HEIGHT
                enemy.isDead = false
                enemy.hit = 1
            }

            if (enemy.y > MAX_Y - 100) {
                lives--
                lostLife = true

                if (lives == 0) {
                    gameOver = true

                    return@forEach
                }

                enemy.isDead = true
            }

            enemy.y += (enemy.speed * deltaTime) * enemyMaxSpeed

        }
    }

    private fun checkCollisionBulletAndEnemy() {
        enemies.forEach { enemy ->
            bullets.forEach { bullet ->
                if (collideEnemy(
                        bullet.x,
                        bullet.y,
                        Bullet.WIDTH,
                        Bullet.HEIGHT,
                        enemy.x,
                        enemy.y,
                        Enemy.WIDTH,
                        Enemy.HEIGHT
                    )
                ) {
                    bullet.shotExhausted = true
                    if (enemy.hit < 4) enemy.hit++
                    else {
                        points += 10
                        hits++
                        if (hits == maxEnemies) {
                            level++
                            enemyMaxSpeed += level / 10
                            maxEnemies += level
                            Log.d("maxenemies", "maxenemies: $maxEnemies")
                        }
                        enemy.isDead = true
                    }
                }
            }
        }
    }

    private fun collideEnemy(
        x: Float,
        y: Int,
        width: Int,
        height: Int,
        x2: Int,
        y2: Float,
        width2: Int,
        height2: Int
    ): Boolean {
        return (x < (x2 + width2) && (x + width) > x2 && y < (y2 + height2) && (y + height) > y2)
    }

    fun addShotToList() {
        if (isShot) bullets.add(Bullet())
        isShot = false
    }

    fun addEnemyToList() {
        enemies.add(Enemy(speed = Random.nextInt(2, this.enemyMaxSpeed)))
    }
}