package dk.martin.invasiongame.invasiongame

interface CollisionListener {
    fun collisionGround()
    fun collisionEnemy()
    fun gameOver()
}