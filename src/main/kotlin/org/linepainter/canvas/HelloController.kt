package org.linepainter.canvas

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.*
import kotlin.math.abs
import kotlin.math.hypot

class HelloController {
    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var lineButton: Button

    @FXML
    private lateinit var rectangleButton: Button

    @FXML
    private lateinit var squareButton: Button

    @FXML
    private lateinit var circleButton: Button

    @FXML
    private lateinit var ellipseButton: Button

    @FXML
    private lateinit var colorButton: Button

    private var selectedColor: Color = Color.BLACK
    private var selectedShape: String = "Line"

    private var startX: Double = 0.0
    private var startY: Double = 0.0
    private var tempShape: Shape? = null

    @FXML
    fun initialize() {
        lineButton.setOnAction { selectedShape = "Line" }
        rectangleButton.setOnAction { selectedShape = "Rectangle" }
        squareButton.setOnAction { selectedShape = "Square" }
        circleButton.setOnAction { selectedShape = "Circle" }
        ellipseButton.setOnAction { selectedShape = "Ellipse" }

        colorButton.setOnAction {
            val colorPicker = javafx.scene.control.ColorPicker(selectedColor)
            val dialog = javafx.scene.control.Dialog<Color>().apply {
                title = "Choose Color"
                dialogPane.content = colorPicker
                dialogPane.buttonTypes.addAll(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL)

                setResultConverter { buttonType ->
                    if (buttonType == javafx.scene.control.ButtonType.OK) {
                        colorPicker.value
                    } else {
                        null
                    }
                }
            }
            val result = dialog.showAndWait()
            result.ifPresent { selectedColor = it }
        }

        anchorPane.setOnMousePressed { event ->
            startX = event.x
            startY = event.y
            tempShape = when (selectedShape) {
                "Line" -> Line(startX, startY, startX, startY).apply { stroke = selectedColor }
                "Rectangle" -> Rectangle(startX, startY, 0.0, 0.0).apply {
                    stroke = selectedColor
                    fill = Color.TRANSPARENT
                }
                "Square" -> Rectangle(startX, startY, 0.0, 0.0).apply {
                    stroke = selectedColor
                    fill = Color.TRANSPARENT
                }
                "Circle" -> Circle(startX, startY, 0.0).apply {
                    stroke = selectedColor
                    fill = Color.TRANSPARENT
                }
                "Ellipse" -> Ellipse(startX, startY, 0.0, 0.0).apply {
                    stroke = selectedColor
                    fill = Color.TRANSPARENT
                }
                else -> null
            }
            tempShape?.let { anchorPane.children.add(it) }
        }

        anchorPane.setOnMouseDragged { event ->
            when (tempShape) {
                is Line -> {
                    (tempShape as Line).endX = event.x
                    (tempShape as Line).endY = event.y
                }
                is Rectangle -> {
                    val rect = tempShape as Rectangle
                    rect.width = abs(event.x - startX)
                    rect.height = if (selectedShape == "Square") rect.width else abs(event.y - startY)
                    rect.x = startX.coerceAtMost(event.x)
                    rect.y = startY.coerceAtMost(event.y)
                }
                is Circle -> {
                    val circle = tempShape as Circle
                    val radius = hypot(event.x - startX, event.y - startY)
                    circle.radius = radius
                }
                is Ellipse -> {
                    val ellipse = tempShape as Ellipse
                    ellipse.radiusX = abs(event.x - startX)
                    ellipse.radiusY = abs(event.y - startY)
                }
            }
        }

        anchorPane.setOnMouseReleased {
            tempShape = null
        }
    }
}