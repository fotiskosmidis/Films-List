package me.fotis;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import me.fotis.api.Database;
import me.fotis.api.FilmList;
import me.fotis.gui.LoadingWindow;

public class App
{
	private static FilmList filmList;

	public static void main(String[] args)
	{
		loadFilms();
		Application.launch(LoadingWindow.class, args);
	}

	public static FilmList getFilmList()
	{
		return filmList;
	}

	private static void loadFilms()
	{
		try
		{
			Database database = new Database();
			filmList = database.loadFilmList();
		}
		catch (IOException | SQLException exception)
		{
			throw new IllegalStateException("Could not load films from the database", exception);
		}
	}

}
