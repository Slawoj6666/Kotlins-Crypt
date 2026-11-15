package com.kotlincrypt.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.yellowstonegames.grid.Coord

class TouchGridController(
    private val viewport: ExtendViewport,
    private val camera: OrthographicCamera,
    private val world: PlayerGridWorld
) : GestureDetector.GestureAdapter() {

    private val touchPos = Vector3()

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean
    {
        if (world.pathQueue.isNotEmpty())
        {
            world.pathQueue.clear()
            return true
        }

        if (world.turnState != TurnState.AWAITING_INPUT) return false

        touchPos.set(x, y, 0f)
        viewport.unproject(touchPos)
        world.handleTapAt(Coord.get(touchPos.x.toInt(), touchPos.y.toInt()))
        return true
    }

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean
    {
        camera.position.add(
            -deltaX * (viewport.worldWidth / Gdx.graphics.width),
            deltaY * (viewport.worldHeight / Gdx.graphics.height),
            0f
        )
        return true
    }
}
