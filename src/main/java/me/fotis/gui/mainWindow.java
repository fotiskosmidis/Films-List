package me.fotis.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import me.fotis.App;
import me.fotis.api.Film;

public class mainWindow
{
	private static final String BACKGROUND_COLOR = "#18212b";
	private static final String PANEL_COLOR = "#202c38";
	private static final String BORDER_COLOR = "#344454";

	public Stage createStage()
	{
		double dpiScale = Math.max(1.0, Screen.getPrimary().getDpi() / 96.0);
		double initialWidth = 980 * dpiScale;
		double initialHeight = 640 * dpiScale;

		Button newFilmButton = new Button("+ New film");
		Button sortButton = new Button("Sort");
		ComboBox<String> stateFilter = new ComboBox<>(FXCollections.observableArrayList(
				"ALL",
				Film.State.WATCHING.name(),
				Film.State.COMPLETED.name(),
				Film.State.DROPPED.name(),
				Film.State.PLAN_TO_WATCH.name()));
		stateFilter.setValue("ALL");

		TextField searchField = new TextField();
		searchField.setPromptText("Search films...");
		searchField.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 100 ? change : null));

		double controlHeight = 46 * dpiScale;
		newFilmButton.setPrefHeight(controlHeight);
		sortButton.setPrefHeight(controlHeight);
		stateFilter.setPrefHeight(controlHeight);
		searchField.setPrefHeight(controlHeight);
		newFilmButton.setMinWidth(112 * dpiScale);
		sortButton.setMinWidth(78 * dpiScale);
		stateFilter.setMinWidth(118 * dpiScale);
		searchField.setMinWidth(280 * dpiScale);

		HBox toolbar = new HBox(12 * dpiScale, newFilmButton, sortButton, stateFilter, searchField);
		toolbar.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(searchField, Priority.ALWAYS);

		TilePane filmTiles = new TilePane();
		filmTiles.setPrefColumns(6);
		filmTiles.setHgap(12 * dpiScale);
		filmTiles.setVgap(12 * dpiScale);
		filmTiles.setPadding(new Insets(16 * dpiScale));
		filmTiles.setTileAlignment(Pos.TOP_LEFT);
		filmTiles.setPrefTileHeight(120 * dpiScale);

		ScrollPane filmArea = new ScrollPane(filmTiles);
		filmArea.setFitToWidth(true);
		filmArea.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		filmArea.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		filmArea.setMinHeight(0);
		filmArea.setStyle("-fx-background-color: " + PANEL_COLOR + ";"
				+ "-fx-border-color: " + BORDER_COLOR + ";"
				+ "-fx-border-width: 1px;"
				+ "-fx-background-radius: 6px;"
				+ "-fx-border-radius: 6px;");
		filmTiles.prefTileWidthProperty().bind(filmArea.widthProperty()
				.subtract(32 * dpiScale + 60 * dpiScale).divide(6));

		BorderPane root = new BorderPane();
		root.setTop(toolbar);
		root.setCenter(filmArea);
		root.setPadding(new Insets(24 * dpiScale));
		root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
		BorderPane.setMargin(toolbar, new Insets(0, 0, 18 * dpiScale, 0));

		MainWindowHandlers.install(newFilmButton, sortButton, stateFilter, searchField,
				filmTiles, filmArea, App.getFilmList(), dpiScale);

		Scene scene = new Scene(root, initialWidth, initialHeight);
		scene.getStylesheets().add(MainWindowHandlers.createControlStylesheet());

		Stage stage = new Stage();
		stage.setTitle("Films List");
		stage.setScene(scene);
		stage.setMinWidth(700 * dpiScale);
		stage.setMinHeight(450 * dpiScale);
		stage.setWidth(initialWidth);
		stage.setHeight(initialHeight);
		MainWindowHandlers.installMainStageCloseHandler(stage, App.getFilmList());

		return stage;
	}
}
