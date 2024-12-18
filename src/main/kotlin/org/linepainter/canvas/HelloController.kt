package org.linepainter.canvas

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.event.EventHandler
import javafx.stage.FileChooser
import javax.imageio.ImageIO
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.*
import kotlin.math.abs
import kotlin.math.hypot
import javafx.embed.swing.SwingFXUtils

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

    @FXML
    private lateinit var textButton: Button

    @FXML
    private lateinit var saveButton: Button

    @FXML
    private lateinit var loadImageButton: Button

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
        textButton.setOnAction { selectedShape = "Text" }
        saveButton.setOnAction { saveToFile() }
        loadImageButton.setOnAction { loadImage() }

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
            when (selectedShape) {
                "Line" -> {
                    startX = event.x
                    startY = event.y
                    tempShape = Line(startX, startY, startX, startY).apply { stroke = selectedColor }
                    tempShape?.let { anchorPane.children.add(it) }
                }
                "Rectangle", "Square" -> {
                    startX = event.x
                    startY = event.y
                    tempShape = Rectangle(startX, startY, 0.0, 0.0).apply {
                        stroke = selectedColor
                        fill = Color.TRANSPARENT
                    }
                    tempShape?.let { anchorPane.children.add(it) }
                }
                "Circle" -> {
                    startX = event.x
                    startY = event.y
                    tempShape = Circle(startX, startY, 0.0).apply {
                        stroke = selectedColor
                        fill = Color.TRANSPARENT
                    }
                    tempShape?.let { anchorPane.children.add(it) }
                }
                "Ellipse" -> {
                    startX = event.x
                    startY = event.y
                    tempShape = Ellipse(startX, startY, 0.0, 0.0).apply {
                        stroke = selectedColor
                        fill = Color.TRANSPARENT
                    }
                    tempShape?.let { anchorPane.children.add(it) }
                }
                "Text" -> {
                    startX = event.x
                    startY = event.y
                    val textField = TextField().apply {
                        layoutX = startX
                        layoutY = startY
                        promptText = "Enter text"
                    }
                    textField.onAction = EventHandler {
                        val textLabel = javafx.scene.control.Label(textField.text).apply {
                            layoutX = textField.layoutX
                            layoutY = textField.layoutY
                        }
                        anchorPane.children.remove(textField)
                        anchorPane.children.add(textLabel)
                    }
                    textField.setOnKeyPressed { keyEvent ->
                        if (keyEvent.code == javafx.scene.input.KeyCode.ESCAPE) {
                            anchorPane.children.remove(textField)
                        }
                    }


                    anchorPane.children.add(textField)
                    textField.requestFocus()
                }
            }
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
    private fun saveToFile() {
        val writableImage = javafx.scene.image.WritableImage(
            anchorPane.width.toInt(),
            anchorPane.height.toInt()
        )
        anchorPane.snapshot(null, writableImage)

        // Wybieramy miejsce zapisu pliku
        val fileChooser = FileChooser().apply {
            title = "Save Image"
            extensionFilters.add(FileChooser.ExtensionFilter("PNG Files", "*.png"))
        }
        val file = fileChooser.showSaveDialog(anchorPane.scene.window)
        if (file != null) {
            // Zapisujemy obraz do pliku PNG
            val bufferedImage = SwingFXUtils.fromFXImage(writableImage, null)
            ImageIO.write(bufferedImage, "PNG", file)
        }
    }

    private fun loadImage() {
        val fileChooser = FileChooser().apply {
            title = "Load Image"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            )
        }

        val file = fileChooser.showOpenDialog(loadImageButton.scene.window)
        if (file != null) {
            try {
                val image = javafx.scene.image.Image(file.toURI().toString())
                val imageView = javafx.scene.image.ImageView(image).apply {
                    isPreserveRatio = true
                    fitWidth = anchorPane.width
                    fitHeight = anchorPane.height
                }
                anchorPane.children.add(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}