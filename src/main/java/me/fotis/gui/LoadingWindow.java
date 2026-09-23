package me.fotis.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class LoadingWindow extends Application
{
	@Override
	public void start(Stage stage)
	{
		double dpiScale = Math.max(1.0, Screen.getPrimary().getDpi() / 96.0);

		Label title = new Label("Films List");
				title.setStyle("-fx-font-size: " + (22 * dpiScale)
					+ "px; -fx-font-weight: bold; -fx-text-fill: white;");

		Label message = new Label("Loading your films...");
				message.setStyle("-fx-text-fill: #b8c4d1;");
		ProgressIndicator progress = new ProgressIndicator();
		progress.setPrefSize(36 * dpiScale, 36 * dpiScale);

		VBox content = new VBox(16 * dpiScale, title, progress, message);
		content.setAlignment(Pos.CENTER);
		content.setStyle("-fx-background-color: #18212b; -fx-text-fill: white;");

		Scene scene = new Scene(content, 320 * dpiScale, 180 * dpiScale);
		stage.setTitle("Films List");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(event -> showMainWindow(stage));
		pause.play();
	}

	private void showMainWindow(Stage loadingStage)
	{
		mainWindow mainWindow = new mainWindow();
		Stage applicationStage = mainWindow.createStage();

		applicationStage.show();
		loadingStage.close();
	}
}
