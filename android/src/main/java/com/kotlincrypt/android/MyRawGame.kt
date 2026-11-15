package com.kotlincrypt.android

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport

class MyRawGame : ApplicationListener {

    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: ExtendViewport

    private lateinit var floorTex: Texture
    private lateinit var wallTex: Texture
    private lateinit var playerTex: Texture

    private lateinit var floorRegion: TextureRegion
    private lateinit var wallRegion: TextureRegion
    private lateinit var playerRegion: TextureRegion

    private lateinit var world: PlayerGridWorld

    override fun create() {
        batch = SpriteBatch()
        camera = OrthographicCamera()
        viewport = ExtendViewport(15f, 15f, camera)

        floorTex = Texture(Gdx.files.internal("floor.png")).apply {
            setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        }
        wallTex = Texture(Gdx.files.internal("wall.png")).apply {
            setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        }
        playerTex = Texture(Gdx.files.internal("player.png")).apply {
            setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        }

        floorRegion = TextureRegion(floorTex)
        wallRegion = TextureRegion(wallTex)
        playerRegion = TextureRegion(playerTex)

        world = PlayerGridWorld()
        camera.position.set(world.playerVisualPos.x + 0.5f, world.playerVisualPos.y + 0.5f, 0f)
        Gdx.input.inputProcessor = GestureDetector(TouchGridController(viewport, camera, world))
    }

    private fun update(delta: Float)
    {
        when (world.turnState)
        {
            TurnState.AWAITING_INPUT -> Unit

            TurnState.PLAYER_MOVING -> {

                val targetX = world.playerGridPos.x.toFloat()
                val targetY = world.playerGridPos.y.toFloat()
                world.playerVisualPos.lerp(Vector2(targetX, targetY), 25f * delta)

                if (world.playerVisualPos.dst2(targetX, targetY) < 0.002f)
                {
                    world.playerVisualPos.set(targetX, targetY)
                    camera.position.set(targetX + 0.5f, targetY + 0.5f, 0f)
                    world.turnState = TurnState.ENEMY_TURN
                }
            }

            TurnState.ENEMY_TURN -> {
                world.processEnemyTurn()
            }

            TurnState.ENEMY_MOVING -> {
                if (world.areEnemiesFinishedMoving()) {
                    world.turnState = TurnState.END_OF_TURN
                }
            }

            TurnState.END_OF_TURN -> {
                world.finishTurn()
                Gdx.app.log("TurnSystem", "Turn: ${world.turnCount}")
            }
        }
    }

    override fun render()
    {
        update(Gdx.graphics.deltaTime)

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        batch.projectionMatrix = camera.combined
        batch.begin()

        for (x in 0 until world.width)
        {
            for (y in 0 until world.height)
            {
                val visible = world.fovMap[x][y] > 0f

                if (!visible && !world.exploredMap.contains(x, y)) continue

                batch.color = if (visible) Color.WHITE else Color.DARK_GRAY

                val region = if (world.dungeonMap[x][y] == '#') wallRegion else floorRegion

                batch.draw(region, x.toFloat(), y.toFloat(), 1f, 1f)
            }
        }

        batch.color = Color.WHITE
        batch.draw(playerRegion, world.playerVisualPos.x, world.playerVisualPos.y, 1f, 1f)
        batch.end()
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height, false)
    override fun pause() {}
    override fun resume() {}

    override fun dispose()
    {
        batch.dispose()
        floorTex.dispose()
        wallTex.dispose()
        playerTex.dispose()
    }
}
