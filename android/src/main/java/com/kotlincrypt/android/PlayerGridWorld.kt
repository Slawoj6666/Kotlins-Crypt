package com.kotlincrypt.android

import com.badlogic.gdx.math.Vector2
import com.github.yellowstonegames.grid.Coord
import com.github.yellowstonegames.grid.FOV
import com.github.yellowstonegames.grid.Measurement
import com.github.yellowstonegames.grid.Region
import com.github.yellowstonegames.path.DijkstraMap
import com.github.yellowstonegames.place.DungeonProcessor

enum class TurnState
{
    AWAITING_INPUT,
    PLAYER_MOVING,
    ENEMY_TURN,
    ENEMY_MOVING,
    END_OF_TURN
}

class PlayerGridWorld(val width: Int = 32, val height: Int = 32)
{

    val dungeonMap: Array<CharArray> = DungeonProcessor(width, height).generate()
    val fovMap = Array(width) { FloatArray(height) }
    val exploredMap = Region(width,height);

    private val visibleRegion = Region(width, height)
    private val resistanceMap = FOV.generateSimpleResistances(dungeonMap)
    private val dijkstra = DijkstraMap(dungeonMap, Measurement.MANHATTAN)

    var playerGridPos: Coord = findStartFloor()
    val playerVisualPos = Vector2(playerGridPos.x.toFloat(), playerGridPos.y.toFloat())

    val pathQueue = mutableListOf<Coord>()
    var turnState = TurnState.AWAITING_INPUT
    var turnCount = 1

    init
    {
        updateFOV()
    }

    private fun findStartFloor(): Coord
    {
        return Region(dungeonMap, '.').first() ?: Coord.get(1,1);
    }

    fun updateFOV()
    {
        FOV.reuseFOV(resistanceMap, fovMap, playerGridPos.x.toInt(), playerGridPos.y.toInt(), 8.5f)
        visibleRegion.refill(fovMap, 0.001f, 1f)
        exploredMap.or(visibleRegion)
    }

    fun handleTapAt(target: Coord)
    {
        if (target.x !in 0 until width || target.y !in 0 until height) return

        if (target == playerGridPos)
        {
            turnState = TurnState.ENEMY_TURN
            return
        }

        if (!exploredMap.contains(target) || dungeonMap[target.x.toInt()][target.y.toInt()] == '#') return


        dijkstra.clearGoals()
        dijkstra.setGoal(target)
        dijkstra.scan(null, null)

        pathQueue.clear()
        pathQueue.addAll(dijkstra.findPath(50, null, null, playerGridPos, target))
        startNextStep()
    }

    fun startNextStep()
    {
        if (pathQueue.isNotEmpty())
        {
            playerGridPos = pathQueue.removeAt(0)
            updateFOV()
            turnState = TurnState.PLAYER_MOVING
        }
    }

    fun processEnemyTurn()
    {

        turnState = TurnState.ENEMY_MOVING
    }

    fun areEnemiesFinishedMoving(): Boolean
    {
        return true
    }

    fun finishTurn()
    {
        turnCount++
        if (pathQueue.isNotEmpty()) {
            startNextStep()
        } else {
            turnState = TurnState.AWAITING_INPUT
        }
    }
}
