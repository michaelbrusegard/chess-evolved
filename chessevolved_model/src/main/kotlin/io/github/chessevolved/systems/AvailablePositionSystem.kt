package io.github.chessevolved.systems

import com.badlogic.gdx.math.Vector2
import io.github.chessevolved.components.BoardSizeComponent
import io.github.chessevolved.components.GameState
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent


class AvailablePositionSystem(
    private val gameState: GameState,
    private var moves: MutableMap<Vector2, List<Position>> = mutableMapOf<Vector2, List<Position>>(),
    private var availableFilteredMoves: MutableList<Position> = mutableListOf<Position>()

) {//can change to chesspice. ... if together
    fun CheckAvailablePositions(
    pieceTypeComponent: PieceTypeComponent,
    playerColorComponent: PlayerColorComponent,
    positionComponent: PositionComponent,
    movementRuleComponent: MovementRuleComponent,
    boardSizeComponent: BoardSizeComponent //hvorfor er ikke boradsize en singelton egt?
    ): List<Position> {
        FindAllTheMoves(
            movementRuleComponent,
            positionComponent,
            boardSizeComponent
        )
        FindAllTheAvailableMoves(playerColorComponent)
        return availableFilteredMoves
        //return emptyList()
    }



    fun FindAllTheMoves(
        movementRuleComponent: MovementRuleComponent,
        positionComponent: PositionComponent,
        boardSizeComponent: BoardSizeComponent
    )//: Map<Vector2, List<Position>>
    {
        for (movementRule in movementRuleComponent.getMovementRules()) {
            for (direction in movementRule.directions) {
                val availablePositionsInDirection = mutableListOf<Position>()

                val maxSteps = if (movementRule.maxSteps == 0) boardSizeComponent.boardSize else movementRule.maxSteps

                for (step in 1..maxSteps) {
                    val newX = positionComponent.position.x + (direction.x * step).toInt()
                    val newY = positionComponent.position.y + (direction.y * step).toInt()

                    // Skip if outside the board
                    if (newX < 0 || newX >= boardSizeComponent.boardSize ||
                        newY < 0 || newY >= boardSizeComponent.boardSize) {
                        break
                    }

                    availablePositionsInDirection.add(Position(x = newX, y = newY))
                }

                // Only add to map if we actually have moves in that direction
                if (availablePositionsInDirection.isNotEmpty()) {
                    moves[direction] = availablePositionsInDirection
                }
            }
        }
       // return moves
    }


    fun FindAllTheAvailableMoves(
        playerColorComponent: PlayerColorComponent,
    ){
        //val filteredMoves = mutableMapOf<Vector2, List<Position>>()

        for(direction in moves.keys) {
            val availablePositions = moves[direction]
            //val availableFilteredMoves = mutableListOf<Position>()

            for(position in availablePositions ?: emptyList()) {
                val piece = gameState.pieces.find { it.position == position }

                if (piece != null) {
                    if (piece.color == playerColorComponent.color) {
                        break
                    }
                    else {
                        //TODO if kill logic here
                        availableFilteredMoves.add(position)
                        break // If the piece is of the opposite color, we can stop checking further in this direction
                    }
                } else availableFilteredMoves.add(position)

            }

            /*if (availableFilteredMoves.isNotEmpty()) {
                filteredMoves[direction] = availableFilteredMoves
            }*/

        }
        //moves = filteredMoves
    }



    //finn alle mulige posisjoner
    //finn alle brikke via gamestate som eksisterer på diss posisjonene
    //finn dem med samme farge og fjern posisjonen og de som er bak om det er
    //så motsatt farge, fjern de bak den om det er
    // returner


        // Get the piece that was clicked
        // Get the movement rule component
        // Get the board size
        // Calculate the available positions
        // Return the available positions


    // hmmm
    // Must have a reference to how big the board is. CHECK use family
    // Can get in a movementrulecomponent
    // Using the board and movementrulecompnent it calculates the move

    // Click on piece.
    // View sends information to presenter on which tile was clicked x og y.
    // Presenter tells AvailablePositionSystem that piece "#" was clicked and gives x and y and movementrulecomponent.
}
