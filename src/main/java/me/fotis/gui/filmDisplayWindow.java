package me.fotis.gui;

import java.nio.file.Path;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import me.fotis.api.Film;

public class filmDisplayWindow
{
	private filmDisplayWindow()
	{
	}

	static Form create(Film film, double dpiScale)
	{
		ImageView image = new ImageView(loadImage(film));
		image.setFitWidth(150 * dpiScale);
		image.setFitHeight(135 * dpiScale);
		image.setPreserveRatio(true);

		Label title = createValue("Title: " + film.getTitle(), dpiScale);
		Label rating = createValue("Rating: " + film.getRating(), dpiScale);
		Label state = createValue("State: " + film.getState(), dpiScale);
		String commentsText = film.getComments() == null || film.getComments().isBlank()
				? "Comments: None" : "Comments: " + film.getComments();
		Label comments = createValue(commentsText, dpiScale);
		comments.setWrapText(true);
		String imageText = film.getImagePath() == null
				? "Image: Default image" : "Image: " + film.getImagePath();
		Label imagePath = createValue(imageText, dpiScale);
		imagePath.setWrapText(true);

		Button edit = new Button("Edit");
		Button delete = new Button("Delete film");
		edit.setPrefHeight(38 * dpiScale);
		edit.setPrefWidth(150 * dpiScale);
		delete.setPrefHeight(38 * dpiScale);
		delete.setPrefWidth(150 * dpiScale);
		String buttonStyle = "-fx-font-size: " + (15 * dpiScale) + "px;";
		edit.setStyle(buttonStyle);
		delete.setStyle(buttonStyle + "-fx-background-color: #b33a3a;"
			+ "-fx-text-fill: #ffffff;");

		VBox details = new VBox(7 * dpiScale, title, rating, state, comments, imagePath);
		details.setAlignment(Pos.TOP_LEFT);
		details.setMaxWidth(320 * dpiScale);

		HBox content = new HBox(14 * dpiScale, image, details);
		content.setAlignment(Pos.TOP_CENTER);

		HBox buttons = new HBox(8 * dpiScale, edit, delete);
		buttons.setAlignment(Pos.CENTER);
		VBox root = new VBox(12 * dpiScale, content, buttons);
		root.setAlignment(Pos.TOP_CENTER);
		root.setPadding(new Insets(16 * dpiScale));
		root.setStyle("-fx-background-color: #18212b;");

		Stage stage = new Stage();
		stage.setTitle(film.getTitle());
		stage.setScene(new Scene(root, 560 * dpiScale, 330 * dpiScale));
		stage.setMinWidth(520 * dpiScale);
		stage.setMinHeight(300 * dpiScale);
		stage.getScene().getStylesheets().add(MainWindowHandlers.createControlStylesheet());

		return new Form(stage, edit, delete);
	}

	private static Label createValue(String value, double dpiScale)
	{
		Label label = new Label(value);
		label.setStyle("-fx-font-size: " + (20 * dpiScale) + "px;");
		return label;
	}

	private static Image loadImage(Film film)
	{
		Path imagePath = film.getImagePath();
		if (imagePath != null && imagePath.toFile().isFile())
		{
			return new Image(imagePath.toUri().toString());
		}

		return new Image(filmDisplayWindow.class.getResource("/default.png").toExternalForm());
	}

	static final class Form
	{
		final Stage stage;
		final Button edit;
		final Button delete;

		private Form(Stage stage, Button edit, Button delete)
		{
			this.stage = stage;
			this.edit = edit;
			this.delete = delete;
		}
	}
}
