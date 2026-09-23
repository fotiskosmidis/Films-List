package me.fotis.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import me.fotis.api.Film;

public class newFilmWindow
{
	private newFilmWindow()
	{
	}

	static Form create(double dpiScale)
	{
		TextField title = new TextField();
		title.setPromptText("Film title");
		title.setTextFormatter(new TextFormatter<String>(change ->
		{
			String value = change.getControlNewText();
			if (value.length() > 100 || !value.matches("[\\p{L}\\p{N} ]*"))
			{
				return null;
			}
			return change;
		}));

		TextField rating = new TextField("0");
		rating.setTextFormatter(new TextFormatter<String>(change ->
		{
			String value = change.getControlNewText();
			if (value.isEmpty())
			{
				return change;
			}
			try
			{
				int number = Integer.parseInt(value);
				return number <= 10 && value.length() <= 2 ? change : null;
			}
			catch (NumberFormatException exception)
			{
				return null;
			}
		}));

		ComboBox<Film.State> state = new ComboBox<>();
		state.getItems().addAll(Film.State.values());
		state.setValue(Film.State.PLAN_TO_WATCH);

		TextArea comments = new TextArea();
		comments.setPromptText("Comments");
		comments.setWrapText(true);
		comments.setPrefRowCount(4);
		comments.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 150 ? change : null));

		TextField imagePath = new TextField();
		imagePath.setPromptText("Optional image path");
		Button browseImage = new Button("Browse");

		Button save = new Button("Save film");
		Button cancel = new Button("Cancel");
		double controlHeight = 52 * dpiScale;
			String labelStyle = "-fx-font-size: " + (20 * dpiScale) + "px;";
			Label titleLabel = new Label("Title");
			Label ratingLabel = new Label("Rating");
			Label stateLabel = new Label("State");
			Label imageLabel = new Label("Image");
			Label commentsLabel = new Label("Comments");
			titleLabel.setStyle(labelStyle);
			ratingLabel.setStyle(labelStyle);
			stateLabel.setStyle(labelStyle);
			imageLabel.setStyle(labelStyle);
			commentsLabel.setStyle(labelStyle);
		title.setPrefHeight(controlHeight);
		rating.setPrefHeight(controlHeight);
		state.setPrefHeight(controlHeight);
		imagePath.setPrefHeight(controlHeight);
		browseImage.setPrefHeight(controlHeight);
		save.setPrefHeight(controlHeight);
		cancel.setPrefHeight(controlHeight);
		save.setPrefWidth(220 * dpiScale);
		cancel.setPrefWidth(220 * dpiScale);
		comments.setPrefHeight(150 * dpiScale);
		String controlStyle = "-fx-font-size: " + (18 * dpiScale) + "px;";
		title.setStyle(controlStyle);
		rating.setStyle(controlStyle);
		state.setStyle(controlStyle);
		imagePath.setStyle(controlStyle);
		browseImage.setStyle(controlStyle);
		save.setStyle(controlStyle);
		cancel.setStyle(controlStyle + "-fx-background-color: #b33a3a;"
				+ "-fx-text-fill: #ffffff;");
		comments.setStyle(controlStyle);

		GridPane fields = new GridPane();
		fields.setHgap(12 * dpiScale);
		fields.setVgap(12 * dpiScale);
			fields.add(titleLabel, 0, 0);
		fields.add(title, 1, 0);
			fields.add(ratingLabel, 0, 1);
		fields.add(rating, 1, 1);
			fields.add(stateLabel, 0, 2);
		fields.add(state, 1, 2);
			fields.add(imageLabel, 0, 3);
		fields.add(imagePath, 1, 3);
		fields.add(browseImage, 2, 3);
			fields.add(commentsLabel, 0, 4);
		fields.add(comments, 1, 4, 2, 1);

		GridPane.setHgrow(title, javafx.scene.layout.Priority.ALWAYS);
		GridPane.setHgrow(imagePath, javafx.scene.layout.Priority.ALWAYS);

		HBox buttons = new HBox(12 * dpiScale, save, cancel);
			buttons.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(save, javafx.scene.layout.Priority.ALWAYS);
			HBox.setHgrow(cancel, javafx.scene.layout.Priority.ALWAYS);
			save.setMaxWidth(Double.MAX_VALUE);
			cancel.setMaxWidth(Double.MAX_VALUE);
		VBox content = new VBox(24 * dpiScale, fields, buttons);
		content.setPadding(new Insets(24 * dpiScale));
		content.setStyle("-fx-background-color: #18212b;");

		Stage stage = new Stage();
		stage.setTitle("New film");
		stage.setScene(new Scene(content, 720 * dpiScale, 600 * dpiScale));
		stage.setMinWidth(640 * dpiScale);
		stage.setMinHeight(520 * dpiScale);
		stage.getScene().getStylesheets().add(MainWindowHandlers.createControlStylesheet());

		return new Form(stage, title, rating, state, comments, imagePath, browseImage, save, cancel);
	}

	static final class Form
	{
		final Stage stage;
		final TextField title;
		final TextField rating;
		final ComboBox<Film.State> state;
		final TextArea comments;
		final TextField imagePath;
		final Button browseImage;
		final Button save;
		final Button cancel;

		private Form(Stage stage, TextField title, TextField rating, ComboBox<Film.State> state,
					 TextArea comments, TextField imagePath, Button browseImage, Button save,
					 Button cancel)
		{
			this.stage = stage;
			this.title = title;
			this.rating = rating;
			this.state = state;
			this.comments = comments;
			this.imagePath = imagePath;
			this.browseImage = browseImage;
			this.save = save;
			this.cancel = cancel;
		}
	}
}
