package org.linepainter.canvas

import javafx.fxml.FXML
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Line

class HelloController {
    @FXML
    private lateinit var anchorPane: AnchorPane

    private var startX: Double = 0.0
    private var startY: Double = 0.0
    private var tempLine: Line? = null

    @FXML
    fun initialize() {
        anchorPane.setOnMousePressed { event: MouseEvent ->
            startX = event.x
            startY = event.y
            tempLine = Line(startX, startY, startX, startY)
            anchorPane.children.add(tempLine)
        }

        anchorPane.setOnMouseDragged { event: MouseEvent ->
            tempLine?.endX = event.x
            tempLine?.endY = event.y
        }

        anchorPane.setOnMouseReleased { event: MouseEvent ->
            tempLine?.endX = event.x
            tempLine?.endY = event.y
            tempLine = null
        }
    }
}