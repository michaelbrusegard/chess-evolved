package io.github.chessevolved.singletons

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.AbilityTriggerComponent
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.WeatherEventComponent
import io.github.chessevolved.data.Position
import io.github.chessevolved.dtos.BoardSquareDto
import io.github.chessevolved.dtos.PieceDto
import io.github.chessevolved.entities.PieceFactory
import io.github.chessevolved.enums.PieceType

object EcsEntityMapper {
    private val pieceFamily: Family =
        Family
            .all(
                PositionComponent::class.java,
                PieceTypeComponent::class.java,
                PlayerColorComponent::class.java,
            ).get()

    private val boardSquareFamily: Family =
        Family
            .all(
                PositionComponent::class.java,
                WeatherEventComponent::class.java,
            ).exclude(PieceTypeComponent::class.java)
            .get()

    fun extractStateFromEngine(engine: Engine): Pair<List<PieceDto>, List<BoardSquareDto>> {
        val pieces: ArrayList<PieceDto> = ArrayList()

        for ((_, value) in Game.getPieceDTOS()) {
            val pieceDto =
                PieceDto(
                    position =
                        Position(
                            value.position.x,
                            GameSettings.getBoardSize() - 1 - value.position.y,
                        ),
                    previousPosition =
                        Position(
                            value.previousPosition.x,
                            GameSettings.getBoardSize() - 1 - value.previousPosition.y,
                        ),
                    type = value.type,
                    color = value.color,
                    abilityType = value.abilityType,
                    abilityCurrentCooldown = value.abilityCurrentCooldown,
                )

            pieces.add(pieceDto)
        }
        var amount = 0

        val boardSquares =
            engine.getEntitiesFor(boardSquareFamily).map { entity ->
                BoardSquareDto(
                    position = PositionComponent.mapper.get(entity).position,
                    weather = WeatherEventComponent.mapper.get(entity).event,
                )
            }

        return Pair(pieces, boardSquares)
    }

    fun applyStateToEngine(
        engine: Engine,
        pieceFactory: PieceFactory,
        stage: Stage,
        receivedPieces: List<PieceDto>,
        receivedBoardSquares: List<BoardSquareDto>,
    ) {
        Gdx.app.log("ECSEntityMapper", "Applying received state to engine...")

        try {
            val existingPieceEntities = engine.getEntitiesFor(pieceFamily)
            val existingPiecesMap =
                existingPieceEntities.associateBy {
                    PositionComponent.mapper.get(it).position
                }
            val currentPositions = mutableSetOf<Position>()

            for (receivedPiece in receivedPieces) {
                currentPositions.add(receivedPiece.position)
                val existingEntity = existingPiecesMap[receivedPiece.position]

                if (existingEntity != null) {
                    val typeComp = PieceTypeComponent.mapper.get(existingEntity)
                    val colorComp = PlayerColorComponent.mapper.get(existingEntity)

                    if (typeComp.type != receivedPiece.type || colorComp.color != receivedPiece.color) {
                        Gdx.app.debug("ECSEntityMapper", "Replacing piece at ${receivedPiece.position} due to type/color mismatch.")
                        engine.removeEntity(existingEntity)
                        Game.removeEntityFromPieceDTOS(existingEntity)
                        createPieceFromDto(pieceFactory, stage, receivedPiece)
                    }

                    if (receivedPiece.abilityType != null) {
                        existingEntity.add(
                            AbilityComponent(
                                receivedPiece.abilityType!!,
                                receivedPiece.abilityType!!.cooldownTime,
                                receivedPiece.abilityCurrentCooldown,
                            ),
                        )
                        existingEntity.add(AbilityTriggerComponent(receivedPiece.position, receivedPiece.position, false))
                    }
                } else {
                    Gdx.app.debug("ECSEntityMapper", "Creating new piece: ${receivedPiece.type} at ${receivedPiece.position}")
                    createPieceFromDto(pieceFactory, stage, receivedPiece)
                }
            }

            for (existingEntity in existingPieceEntities) {
                val pos = PositionComponent.mapper.get(existingEntity).position
                if (pos !in currentPositions) {
                    // Most likely it was this that moved. We can trigger the new entity through movement system.
                    Gdx.app.debug("ECSEntityMapper", "Removing piece no longer in state at $pos")
                    val actorComponent = ActorComponent.mapper.get(existingEntity)
                    actorComponent.actor.remove()

                    Game.removeEntityFromPieceDTOS(existingEntity)

                    engine.removeEntity(existingEntity)
                }
            }

            val existingSquareEntities = engine.getEntitiesFor(boardSquareFamily)
            val receivedSquaresMap = receivedBoardSquares.associateBy { it.position }

            existingSquareEntities.forEach { squareEntity ->
                val posComp = PositionComponent.mapper.get(squareEntity)
                val receivedSquare = receivedSquaresMap[posComp.position]

                if (receivedSquare != null) {
                    val weatherComp = WeatherEventComponent.mapper.get(squareEntity)
                    if (weatherComp.event != receivedSquare.weather) {
                        Gdx.app.debug(
                            "ECSEntityMapper",
                            "Updating weather at ${posComp.position} from ${weatherComp.event} to ${receivedSquare.weather}",
                        )
                        weatherComp.event = receivedSquare.weather
                    }
                } else {
                    Gdx.app.error(
                        "ECSEntityMapper",
                        "Board square at ${posComp.position} exists locally but not in received state. Ignoring.",
                    )
                }
            }
            Gdx.app.log("ECSEntityMapper", "Finished applying state.")
        } catch (e: Exception) {
            Gdx.app.error("ECSEntityMapper", "Error during state application: ${e.message}", e)
        }
    }

    private fun createPieceFromDto(
        pieceFactory: PieceFactory,
        stage: Stage,
        pieceData: PieceDto,
    ) {
        val pieceEntity: Entity

        when (pieceData.type) {
            PieceType.PAWN -> pieceEntity = pieceFactory.createPawn(pieceData.position, pieceData.color, stage)
            PieceType.ROOK -> pieceEntity = pieceFactory.createRook(pieceData.position, pieceData.color, stage)
            PieceType.KNIGHT -> pieceEntity = pieceFactory.createKnight(pieceData.position, pieceData.color, stage)
            PieceType.BISHOP -> pieceEntity = pieceFactory.createBishop(pieceData.position, pieceData.color, stage)
            PieceType.QUEEN -> pieceEntity = pieceFactory.createQueen(pieceData.position, pieceData.color, stage)
            PieceType.KING -> pieceEntity = pieceFactory.createKing(pieceData.position, pieceData.color, stage)
        }

        pieceEntity.apply {
            if (pieceData.abilityType != null) {
                add(AbilityComponent(pieceData.abilityType!!, pieceData.abilityType!!.cooldownTime, pieceData.abilityCurrentCooldown))
                println("CREATEPIECEFROMDTO: THIS ONE GETS TRIGGERED")
                add(AbilityTriggerComponent(pieceData.position, pieceData.previousPosition, true))
            }
        }

        println("Ability: ${pieceData.abilityType}")
        println("AbilityCD: ${pieceData.abilityCurrentCooldown}")

        Game.addPieceDTOS(pieceEntity)
    }
}
