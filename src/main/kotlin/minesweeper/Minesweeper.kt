package minesweeper

import java.lang.IllegalArgumentException
import java.util.*

enum class CellState(val displayString: String) {
    Unmarked("."), Marked("*"), Discovered("X");
}

enum class MarkOperation {
    FREE, MINE
}

data class Position(val row: Int, val col: Int) {
    override fun toString() = "($row,$col)"
}

infix fun Int.by(col: Int) = Position(this, col)

abstract class Cell {
    var isDiscovered: Boolean = false
    var isMarked: Boolean = false
    fun toggleMarkedAsMine() {
        isMarked = !isMarked
    }

    fun displayString(): String = if (isDiscovered) discoveredString() else if (isMarked) "*" else "."
    abstract fun discoveredString(): String
}

class MineCell : Cell() {
    override fun discoveredString() = "X"
}

class FreeCell : Cell() {
    var neighborCount = 0
    override fun discoveredString(): String = if (neighborCount == 0) "/" else neighborCount.toString()
}

class Game(private val rowCount: Int, private val colCount: Int, private val mineCount: Int) {
    private val map: Array<Array<Cell>> = Array(rowCount) { Array(colCount) { FreeCell() as Cell } }
    private val header = "\n |%s|\n-|%s|\n".format(
            (1..colCount).toCollection(mutableListOf()).joinToString(""),
            CharArray(colCount) { '-' }.joinToString("")
    )
    private val footer = "-|%s|\n".format(CharArray(colCount) { '-' }.joinToString(""))
    private var gameLost = false

    fun placeMines() {
        val rand = Random()
        val minePositions = HashSet<Position>(mineCount)

        while (minePositions.size < mineCount) {
            minePositions += rand.nextInt(rowCount) by rand.nextInt(colCount)
        }
        for (minePos in minePositions) {
            map[minePos.row][minePos.col] = MineCell()
        }
        for (minePos in minePositions) {
            forEachNeighbor(minePos) { cell, _ ->
                if (cell is FreeCell) {
                    cell.neighborCount++
                }
            }
        }
    }

    fun isFinished() = gameLost || isWon()

    fun isWon() = !gameLost && (allMinesWithoutFreeCellsMarked() || allFreeCellsDiscovered())

    private fun allMinesWithoutFreeCellsMarked(): Boolean {
        return map.all { it.all { cell -> (cell is MineCell && cell.isMarked) || (cell is FreeCell && !cell.isMarked) } }
    }

    private fun allFreeCellsDiscovered(): Boolean {
        return map.all { it.all { cell -> (cell is FreeCell && cell.isDiscovered) || (cell is MineCell) }}
    }

    fun printMap() {
        print(header)

        for (row in 0 until rowCount) {
            print("${row + 1}|")
            for (col in 0 until colCount) {
                print(map[row][col].displayString())
            }
            println("|")
        }
        print(footer)
    }

    private fun isInMap(row: Int, col: Int) = row in 0 until rowCount && col in 0 until colCount

    fun placeMark(markRow: Int, markCol: Int, op: MarkOperation) {
        val displayX = markCol + 1
        val displayY = markRow + 1
        if (markCol !in 0 until colCount || markRow !in 0 until rowCount) {
            println("Input ($displayX, $displayY) out of bounds: [1, $colCount], [1, $rowCount]")
            return
        }
        val cell = map[markRow][markCol]

        when (op) {
            MarkOperation.FREE -> when (cell) {
                is MineCell -> {
                    map.forEach { row ->
                        row.forEach { cell ->
                            if (cell is MineCell) {
                                cell.isDiscovered = true
                            }
                        }
                    }
                    gameLost = true
                }
                is FreeCell -> {
                    discoverFreeCells(cell, markRow by markCol)
                }
            }

            MarkOperation.MINE -> {
                if (cell.isDiscovered) {
                    println("Cell ($displayX, $displayY) is a free discovered cell, not marking it as a mine!")
                } else {
                    cell.toggleMarkedAsMine()
                }
            }
        }
    }

    private fun discoverFreeCells(freeCell: FreeCell, startPos: Position) {
        freeCell.isDiscovered = true
        if (freeCell.neighborCount > 0) {
            return
        }

        val unprocessedQueue = LinkedList(mutableListOf(startPos))
        while (unprocessedQueue.isNotEmpty()) {
            forEachNeighbor(unprocessedQueue.pollFirst()) { cell, pos ->
                cell.takeIf { !it.isDiscovered }?.apply {
                    isDiscovered = true
                    if (this is FreeCell && this.neighborCount == 0) {
                        unprocessedQueue += pos
                    }
                }
            }
        }
    }

    private fun forEachNeighbor(center: Position, forBody: (Cell, Position) -> Unit) {
        for (r in center.row - 1..center.row + 1) {
            for (c in center.col - 1..center.col + 1) {
                if ((r != center.row || c != center.col) && isInMap(r, c)) {
                    forBody(map[r][c], r by c)
                }
            }
        }
    }
}

fun main() {
    print("How many mines do you want on the field? ")
    val scanner = Scanner(System.`in`)
    val mineCount = scanner.nextInt()

    val game = Game(9, 9, mineCount)
    game.placeMines()

    while (!game.isFinished()) {
        game.printMap()
        print("Set/delete mines marks (x and y coordinates): ")
        try {
            val markX = scanner.nextInt()
            val markY = scanner.nextInt()
            val op = MarkOperation.valueOf(scanner.next().toUpperCase())
            game.placeMark(markY - 1, markX - 1, op)
        } catch (e: InputMismatchException) {
            println("Input could not be parsed: '${scanner.nextLine()}'")
        } catch (e: IllegalArgumentException) {
            println("Illegal argument: ${e.message}")
        }
    }

    game.printMap()
    if (game.isWon()) {
        println("Congratulations! You found all mines!")
    } else {
        println("You stepped on a mine and failed!")
    }
}
