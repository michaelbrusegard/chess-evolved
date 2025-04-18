package io.github.chessevolved.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import io.github.chessevolved.components.AbilityComponent
import io.github.chessevolved.components.ActorComponent
import io.github.chessevolved.components.HighlightComponent
import io.github.chessevolved.components.MovementRuleComponent
import io.github.chessevolved.components.PieceType
import io.github.chessevolved.components.PieceTypeComponent
import io.github.chessevolved.components.PlayerColor
import io.github.chessevolved.components.PlayerColorComponent
import io.github.chessevolved.components.Position
import io.github.chessevolved.components.PositionComponent
import io.github.chessevolved.components.TextureRegionComponent
import io.github.chessevolved.systems.InputService
import ktx.actors.onClick

class PieceFactory(
    private val engine: Engine,
    private val assetManager: AssetManager,
) {
    private val inputService: InputService = InputService()

    private val diagonalDirections =
        listOf(
            Vector2(1f, 1f),
            Vector2(1f, -1f),
            Vector2(-1f, 1f),
            Vector2(-1f, -1f),
        )

    private val straightDirections =
        listOf(
            Vector2(0f, 1f),
            Vector2(0f, -1f),
            Vector2(1f, 0f),
            Vector2(-1f, 0f),
        )

    private val knightDirections =
        listOf(
            Vector2(1f, 2f),
            Vector2(-1f, 2f),
            Vector2(1f, -2f),
            Vector2(-1f, -2f),
            Vector2(2f, 1f),
            Vector2(2f, -1f),
            Vector2(-2f, 1f),
            Vector2(-2f, -1f),
        )

    private fun getPieceTextureRegion(
        pieceType: PieceType,
        playerColor: PlayerColor,
    ): TextureRegion {
        val colorStr = playerColor.name.lowercase()
        val typeStr = pieceType.name.lowercase()
        val filename = "pieces/$typeStr-$colorStr.png"

        val texture = assetManager.get(filename, Texture::class.java)
        return TextureRegion(texture)
    }

    private fun getPieceActor(
        positionProvider: () -> Position,
        stage: Stage,
        onClick: (Position) -> Unit,
    ): Image {
        val image =
            Image().apply {
                setSize(1f, 1f)
                setPosition(positionProvider().x.toFloat(), positionProvider().y.toFloat())

                onClick {
                    onClick(positionProvider())
                }
            }
        stage.addActor(image)

        return image
    }

    private fun createPiece(
        position: Position,
        pieceType: PieceType,
        playerColor: PlayerColor,
        stage: Stage,
    ): Entity =
        Entity().apply {
            add(PositionComponent(position))
            add(PieceTypeComponent(pieceType))
            add(PlayerColorComponent(playerColor))
            add(MovementRuleComponent())
            add(AbilityComponent(emptyList()))
            add(TextureRegionComponent(getPieceTextureRegion(pieceType, playerColor)))
            add(HighlightComponent(Color.WHITE))
            add(
                ActorComponent(
                    getPieceActor(
                        positionProvider = { PositionComponent.mapper.get(this).position },
                        stage,
                    ) { clickedPosition -> inputService.clickPieceAtPosition(clickedPosition) },
                ),
            )
            engine.addEntity(this)
        }

    fun createPawn(
        isPlayerOne: Boolean,
        position: Position,
        color: PlayerColor,
        stage: Stage,
    ) = createPiece(position, PieceType.PAWN, color, stage).apply {
        getComponent(MovementRuleComponent::class.java).apply {
            val pawnDirections: List<Vector2>
            val pawnCaptureDirections: List<Vector2>
            val pawnStartDirections: List<Vector2>
            if (isPlayerOne) {
                pawnDirections = listOf(Vector2(0f, 1f))
                pawnCaptureDirections = listOf(Vector2(1f, 1f), Vector2(-1f, 1f))
                pawnStartDirections = listOf(Vector2(0f, 2f))
            } else {
                pawnDirections = listOf(Vector2(0f, -1f))
                pawnCaptureDirections = listOf(Vector2(1f, -1f), Vector2(-1f, -1f))
                pawnStartDirections = listOf(Vector2(0f, -2f))
            }

            addPattern(
                MovementRuleComponent.MovementPattern(
                    moveName = "pawnMove",
                    directions = pawnDirections,
                    maxSteps = 1,
                    moveType = MovementRuleComponent.MoveType.MOVE_ONLY,
                ),
            )

            addPattern(
                MovementRuleComponent.MovementPattern(
                    moveName = "pawnCapture",
                    directions = pawnCaptureDirections,
                    maxSteps = 1,
                    moveType = MovementRuleComponent.MoveType.CAPTURE_ONLY,
                ),
            )

            addPattern(
                MovementRuleComponent.MovementPattern(
                    moveName = "pawnStart",
                    directions = pawnStartDirections,
                    maxSteps = 1,
                    isFirstMove = true,
                    moveType = MovementRuleComponent.MoveType.MOVE_ONLY,
                ),
            )
        }
    }

    fun createKnight(
        position: Position,
        color: PlayerColor,
        stage: Stage,
    ): Entity =
        createPiece(position, PieceType.KNIGHT, color, stage).apply {
            getComponent(MovementRuleComponent::class.java).apply {
                addPattern(
                    MovementRuleComponent.MovementPattern(
                        moveName = "knightNormal",
                        directions = knightDirections,
                        maxSteps = 1,
                        canJump = true,
                    ),
                )
            }
        }

    fun createBishop(
        position: Position,
        color: PlayerColor,
        stage: Stage,
    ): Entity =
        createPiece(position, PieceType.BISHOP, color, stage).apply {
            getComponent(MovementRuleComponent::class.java).apply {
                addPattern(
                    MovementRuleComponent.MovementPattern(
                        moveName = "bishopNormal",
                        directions = diagonalDirections,
                    ),
                )
            }
        }

    fun createRook(
        position: Position,
        color: PlayerColor,
        stage: Stage,
    ): Entity =
        createPiece(position, PieceType.ROOK, color, stage).apply {
            getComponent(MovementRuleComponent::class.java).apply {
                addPattern(
                    MovementRuleComponent.MovementPattern(
                        moveName = "rookNormal",
                        directions = straightDirections,
                    ),
                )
            }
        }

    fun createQueen(
        position: Position,
        color: PlayerColor,
        stage: Stage,
    ): Entity =
        createPiece(position, PieceType.QUEEN, color, stage).apply {
            getComponent(MovementRuleComponent::class.java).apply {
                addPattern(
                    MovementRuleComponent.MovementPattern(
                        moveName = "queenNormal",
                        directions = straightDirections + diagonalDirections,
                    ),
                )
            }
        }

    fun createKing(
        position: Position,
        color: PlayerColor,
        stage: Stage,
    ): Entity =
        createPiece(position, PieceType.KING, color, stage).apply {
            getComponent(MovementRuleComponent::class.java).apply {
                addPattern(
                    MovementRuleComponent.MovementPattern(
                        moveName = "kingNormal",
                        directions = straightDirections + diagonalDirections,
                        maxSteps = 1,
                    ),
                )
            }
        }
}
