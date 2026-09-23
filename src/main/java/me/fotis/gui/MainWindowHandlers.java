package me.fotis.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.UUID;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import me.fotis.api.Database;
import me.fotis.api.DataDir;
import me.fotis.api.Film;
import me.fotis.api.FilmList;

final class MainWindowHandlers
{
    private static newFilmWindow.Form openNewFilmForm;
    private static filmDisplayWindow.Form openFilmDisplay;

    private MainWindowHandlers()
    {
    }

    static void install(Button newFilmButton, Button sortButton, ComboBox<String> stateFilter,
                        TextField searchField, TilePane filmTiles, ScrollPane filmArea,
                        FilmList filmList, double dpiScale)
    {
        newFilmButton.setOnAction(event ->
            openNewFilmWindow(newFilmButton, filmTiles, filmArea, stateFilter,
                filmList, dpiScale));
        sortButton.setOnAction(event ->
        {
            filmList.sortFilmListByRating();
                refreshFilms(filmTiles, filmArea, filmList, stateFilter, stateFilter.getValue(),
                    searchField.getText(), dpiScale);
        });
        stateFilter.setOnAction(event ->
                refreshFilms(filmTiles, filmArea, filmList, stateFilter, stateFilter.getValue(),
                    searchField.getText(), dpiScale));
            searchField.textProperty().addListener((observable, oldValue, newValue) ->
                refreshFilms(filmTiles, filmArea, filmList, stateFilter, stateFilter.getValue(),
                    newValue, dpiScale));

            refreshFilms(filmTiles, filmArea, filmList, stateFilter, stateFilter.getValue(),
                searchField.getText(), dpiScale);
    }

    private static void openNewFilmWindow(Button newFilmButton, TilePane filmTiles,
                                          ScrollPane filmArea,
                                          ComboBox<String> stateFilter, FilmList filmList,
                                          double dpiScale)
    {
        if (openNewFilmForm != null && openNewFilmForm.stage.isShowing())
        {
            openNewFilmForm.stage.toFront();
            openNewFilmForm.stage.requestFocus();
            return;
        }

        newFilmWindow.Form form = newFilmWindow.create(dpiScale);
        openNewFilmForm = form;
        Window owner = newFilmButton.getScene().getWindow();
        form.stage.initOwner(owner);
        form.browseImage.setOnAction(event -> chooseImage(form));
        form.cancel.setOnAction(event -> form.stage.close());
        form.save.setOnAction(event -> saveNewFilm(form, filmTiles, filmArea,
                stateFilter, filmList, dpiScale));
        form.stage.setOnHidden(event -> openNewFilmForm = null);
        form.stage.show();
    }

    private static void chooseImage(newFilmWindow.Form form)
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose film image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Image files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"));
        File selectedFile = chooser.showOpenDialog(form.stage);
        if (selectedFile != null)
        {
            form.imagePath.setText(selectedFile.toPath().toString());
        }
    }

    private static void saveNewFilm(newFilmWindow.Form form, TilePane filmTiles,
                                    ScrollPane filmArea, ComboBox<String> stateFilter,
                                    FilmList filmList, double dpiScale)
    {
        String title = form.title.getText().trim();
        if (title.isEmpty())
        {
            form.title.requestFocus();
            return;
        }

        int rating;
        try
        {
            rating = Integer.parseInt(form.rating.getText());
        }
        catch (NumberFormatException exception)
        {
            form.rating.requestFocus();
            return;
        }
        if (rating < 0 || rating > 10)
        {
            form.rating.requestFocus();
            return;
        }

        Film film = new Film();
        film.setTitle(title);
		film.setRating(rating);
        film.setState(form.state.getValue());
        film.setComments(form.comments.getText().trim());
        film.setImagePath(storeImage(form.imagePath.getText(), null));

        filmList.addFilm(film);
        try
        {
            new Database().saveFilmList(filmList);
        }
        catch (Exception exception)
        {
            filmList.removeFilm(film);
            throw new IllegalStateException("Could not save film", exception);
        }

        refreshFilms(filmTiles, filmArea, filmList, stateFilter, stateFilter.getValue(),
            "", dpiScale);
        form.stage.close();
    }

    private static void refreshFilms(TilePane filmTiles, ScrollPane filmArea,
                                     FilmList filmList, ComboBox<String> stateFilter,
                                     String selectedState,
                                     String searchText, double dpiScale)
    {
        filmTiles.getChildren().clear();
        ArrayList<Film> filmsToDisplay = new ArrayList<>();
        String normalizedSearch = searchText.trim().toLowerCase();
        for (Film film : filmList.getFilms())
        {
            boolean matchesState = "ALL".equals(selectedState)
                || film.getState().name().equals(selectedState);
            boolean matchesSearch = normalizedSearch.isEmpty()
                || film.getTitle().toLowerCase().contains(normalizedSearch);
            if (matchesState && matchesSearch)
            {
                filmsToDisplay.add(film);
            }
        }

        for (Film film : filmsToDisplay)
        {
            filmTiles.getChildren().add(createFilmTile(film, filmTiles, filmArea,
                    stateFilter, filmList, dpiScale));
        }

        if (filmsToDisplay.isEmpty())
        {
            Label emptyState = new Label("Your saved films will appear here");
            emptyState.setStyle("-fx-font-size: " + (16 * dpiScale) + "px;");
            emptyState.setTextFill(javafx.scene.paint.Color.web("#b8c4d1"));
            filmArea.setContent(emptyState);
        }
        else
        {
            filmArea.setContent(filmTiles);
        }
    }

    private static VBox createFilmTile(Film film, TilePane filmTiles, ScrollPane filmArea,
                                       ComboBox<String> stateFilter, FilmList filmList,
                                       double dpiScale)
    {
        ImageView image = new ImageView(loadFilmImage(film));
        image.setFitWidth(120 * dpiScale);
        image.setFitHeight(100 * dpiScale);
        image.setPreserveRatio(true);

        Label title = new Label(film.getTitle());
        title.setWrapText(true);
        title.setStyle("-fx-font-size: " + (16 * dpiScale) + "px; -fx-font-weight: bold;");

        Label rating = new Label("Rating: " + film.getRating());
        Label state = new Label(film.getState().name());
        VBox tile = new VBox(8 * dpiScale, image, title, rating, state);
        tile.setAlignment(Pos.TOP_LEFT);
        tile.setPadding(new Insets(12 * dpiScale));
        tile.setStyle("-fx-background-color: #2a3948; -fx-background-radius: 6px;"
                + "-fx-border-color: #344454; -fx-border-radius: 6px;"
                + "-fx-text-fill: white;");
        tile.setOnMouseClicked(event -> openFilmDisplayWindow(film, filmTiles, filmArea,
            stateFilter, filmList, dpiScale));
        return tile;
    }

        private static void openFilmDisplayWindow(Film film, TilePane filmTiles, ScrollPane filmArea,
                              ComboBox<String> stateFilter, FilmList filmList,
                              double dpiScale)
    {
        if (openFilmDisplay != null)
        {
            openFilmDisplay.stage.close();
        }

        filmDisplayWindow.Form display = filmDisplayWindow.create(film, dpiScale);
        openFilmDisplay = display;
        display.stage.initOwner(filmTiles.getScene().getWindow());
        display.edit.setOnAction(event -> openEditFilmWindow(film, display, filmTiles,
            filmArea, stateFilter, filmList, dpiScale));
        display.delete.setOnAction(event -> deleteFilm(film, display, filmTiles,
            filmArea, stateFilter, filmList, dpiScale));
        display.stage.setOnHidden(event ->
        {
            if (openFilmDisplay == display)
            {
                openFilmDisplay = null;
            }
        });
        display.stage.show();
    }

    private static void openEditFilmWindow(Film film, filmDisplayWindow.Form display,
                                           TilePane filmTiles, ScrollPane filmArea,
                                           ComboBox<String> stateFilter, FilmList filmList,
                                           double dpiScale)
    {
        if (openNewFilmForm != null && openNewFilmForm.stage.isShowing())
        {
            openNewFilmForm.stage.toFront();
            openNewFilmForm.stage.requestFocus();
            return;
        }

        newFilmWindow.Form form = newFilmWindow.create(dpiScale);
        openNewFilmForm = form;
        form.stage.initOwner(filmTiles.getScene().getWindow());
        form.title.setText(film.getTitle());
        form.rating.setText(Integer.toString(film.getRating()));
        form.state.setValue(film.getState());
        form.comments.setText(film.getComments() == null ? "" : film.getComments());
        form.imagePath.setText(film.getImagePath() == null ? "" : film.getImagePath().toString());
        form.browseImage.setOnAction(event -> chooseImage(form));
        form.cancel.setOnAction(event -> form.stage.close());
        form.save.setOnAction(event -> updateFilm(film, form, display, filmTiles,
                filmArea, stateFilter, filmList, dpiScale));
        form.stage.setOnHidden(event -> openNewFilmForm = null);
        display.stage.close();
        form.stage.show();
    }

    private static void updateFilm(Film film, newFilmWindow.Form form,
                                   filmDisplayWindow.Form display, TilePane filmTiles,
                                   ScrollPane filmArea, ComboBox<String> stateFilter,
                                   FilmList filmList, double dpiScale)
    {
        String title = form.title.getText().trim();
        if (title.isEmpty())
        {
            form.title.requestFocus();
            return;
        }

        int rating;
        try
        {
            rating = Integer.parseInt(form.rating.getText());
        }
        catch (NumberFormatException exception)
        {
            form.rating.requestFocus();
            return;
        }
        if (rating < 0 || rating > 10)
        {
            form.rating.requestFocus();
            return;
        }

        film.setTitle(title);
        film.setRating(rating);
        film.setState(form.state.getValue());
        film.setComments(form.comments.getText().trim());
        Path oldImagePath = film.getImagePath();
        Path newImagePath = storeImage(form.imagePath.getText(), oldImagePath);
        film.setImagePath(newImagePath);
        saveFilmChanges(filmList);
        if (oldImagePath != null && !oldImagePath.equals(newImagePath))
        {
            deleteImagePath(oldImagePath);
        }
        refreshFilms(filmTiles, filmArea, filmList, stateFilter, stateFilter.getValue(),
            "", dpiScale);
        form.stage.close();
        openFilmDisplayWindow(film, filmTiles, filmArea, stateFilter, filmList, dpiScale);
    }

    private static Path storeImage(String imageText, Path existingImagePath)
    {
        String trimmedPath = imageText.trim();
        if (trimmedPath.isEmpty() || "default.png".equalsIgnoreCase(
                Path.of(trimmedPath).getFileName().toString()))
        {
            return null;
        }

        Path source = Path.of(trimmedPath).toAbsolutePath().normalize();
        if (existingImagePath != null
                && source.equals(existingImagePath.toAbsolutePath().normalize()))
        {
            return existingImagePath;
        }

        if (!Files.isRegularFile(source))
        {
            throw new IllegalArgumentException("Image file does not exist: " + source);
        }

        try
        {
            String fileName = source.getFileName().toString();
            String extension = "";
            int extensionStart = fileName.lastIndexOf('.');
            if (extensionStart >= 0)
            {
                extension = fileName.substring(extensionStart);
            }
            Path target = DataDir.getImagesDirectory().resolve(UUID.randomUUID() + extension);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target;
        }
        catch (Exception exception)
        {
            throw new IllegalStateException("Could not copy image into the app images directory",
                    exception);
        }
    }

    private static void deleteFilm(Film film, filmDisplayWindow.Form display,
                                   TilePane filmTiles, ScrollPane filmArea,
                                   ComboBox<String> stateFilter, FilmList filmList,
                                   double dpiScale)
    {
        filmList.removeFilm(film);
        try
        {
            saveFilmChanges(filmList);
            deleteFilmImage(film);
        }
        catch (RuntimeException exception)
        {
            filmList.addFilm(film);
            throw exception;
        }
        refreshFilms(filmTiles, filmArea, filmList, stateFilter, stateFilter.getValue(),
            "", dpiScale);
        display.stage.close();
    }

    private static void deleteFilmImage(Film film)
    {
        deleteImagePath(film.getImagePath());
    }

    private static void deleteImagePath(Path imagePath)
    {
        if (imagePath == null || "default.png".equalsIgnoreCase(
                imagePath.getFileName().toString()))
        {
            return;
        }

        try
        {
            Files.deleteIfExists(imagePath);
        }
        catch (Exception exception)
        {
            throw new IllegalStateException("Could not delete film image", exception);
        }
    }

    private static void saveFilmChanges(FilmList filmList)
    {
        try
        {
            new Database().saveFilmList(filmList);
        }
        catch (Exception exception)
        {
            throw new IllegalStateException("Could not save film changes", exception);
        }
    }

    private static Image loadFilmImage(Film film)
    {
        if (film.getImagePath() != null && film.getImagePath().toFile().isFile())
        {
            return new Image(film.getImagePath().toUri().toString());
        }

        return new Image(MainWindowHandlers.class.getResource("/default.png").toExternalForm());
    }

    static void installMainStageCloseHandler(Stage stage, FilmList filmList)
    {
        stage.setOnCloseRequest(event ->
        {
            if (openNewFilmForm != null)
            {
                openNewFilmForm.stage.close();
                openNewFilmForm = null;
            }
            if (openFilmDisplay != null)
            {
                openFilmDisplay.stage.close();
                openFilmDisplay = null;
            }

            try
            {
                new Database().saveFilmList(filmList);
            }
            catch (Exception exception)
            {
                event.consume();
                showSaveError(exception);
            }
        });
    }

    private static void showSaveError(Exception exception)
    {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Could not save films");
        alert.setHeaderText("The film list could not be saved.");
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    static String createControlStylesheet()
    {
        return "data:text/css," +
                ".button,.combo-box,.text-field,.text-area,.spinner {" +
                "-fx-background-color: #202c38;" +
                "-fx-border-color: #344454;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 5px;" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 24px;" +
                "-fx-padding: 0 12px;" +
                "}" +
                ".button:hover { -fx-background-color: #2d3d4c; }" +
                ".button:focused,.combo-box:focused,.text-field:focused {" +
                "-fx-border-color: #4f9ee8;" +
                "}" +
                ".text-field .text { -fx-fill: #ffffff; }" +
                ".text-field .prompt-text { -fx-fill: #b8c4d1; }" +
                ".text-area .content { -fx-background-color: #202c38; }" +
                ".text-area .text { -fx-fill: #ffffff; }" +
                ".label { -fx-text-fill: #ffffff; }" +
                ".combo-box .list-cell { -fx-text-fill: #ffffff; -fx-background-color: #202c38; }" +
                ".combo-box .arrow { -fx-background-color: #b8c4d1; }" +
                ".scroll-pane, .scroll-pane .viewport, .scroll-pane .corner, .tile-pane {" +
                "-fx-background-color: #202c38;" +
                "}" +
                ".scroll-bar:vertical { -fx-opacity: 0; -fx-pref-width: 0; }" +
                ".scroll-bar:horizontal { -fx-opacity: 0; -fx-pref-height: 0; }";
    }
}