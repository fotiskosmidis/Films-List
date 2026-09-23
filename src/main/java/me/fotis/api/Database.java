package me.fotis.api;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;

public final class Database
{
	private static final String CREATE_FILMS_TABLE = """
			CREATE TABLE IF NOT EXISTS films (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT NOT NULL,
				rating INTEGER NOT NULL,
				image_path TEXT,
				state TEXT NOT NULL,
				comments TEXT
			)
			""";

	private final Path databasePath;

	public Database() throws IOException
	{
		this.databasePath = DataDir.getDatabasePath();
	}

	public void initialize() throws SQLException
	{
		try (Connection connection = openConnection();
			 Statement statement = connection.createStatement())
		{
			statement.executeUpdate(CREATE_FILMS_TABLE);
		}
	}

	public FilmList loadFilmList() throws SQLException
	{
		initialize();
		FilmList filmList = new FilmList();
		String query = "SELECT title, rating, image_path, state, comments "
				+ "FROM films ORDER BY id";

		try (Connection connection = openConnection();
			 Statement statement = connection.createStatement();
			 ResultSet results = statement.executeQuery(query))
		{
			while (results.next())
			{
				Film film = new Film();
				film.setTitle(results.getString("title"));
				film.setRating(results.getInt("rating"));
				String imagePath = results.getString("image_path");
				if (imagePath != null)
				{
					film.setImagePath(Path.of(imagePath));
				}
				film.setState(Film.State.valueOf(results.getString("state")));
				film.setComments(results.getString("comments"));
				filmList.addFilm(film);
			}
		}

		return filmList;
	}

	public void saveFilmList(FilmList filmList) throws SQLException
	{
		initialize();
		String delete = "DELETE FROM films";
		String insert = "INSERT INTO films (title, rating, image_path, state, comments) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = openConnection())
		{
			connection.setAutoCommit(false);
			try (Statement statement = connection.createStatement();
				 PreparedStatement filmStatement = connection.prepareStatement(insert))
			{
				statement.executeUpdate(delete);
				for (Film film : filmList.getFilms())
				{
					filmStatement.setString(1, film.getTitle());
					filmStatement.setInt(2, film.getRating());
					filmStatement.setString(3, film.getImagePath() == null
							? null : film.getImagePath().toString());
					filmStatement.setString(4, film.getState().name());
					filmStatement.setString(5, film.getComments());
					filmStatement.addBatch();
				}
				filmStatement.executeBatch();
				connection.commit();
			}
			catch (SQLException exception)
			{
				connection.rollback();
				throw exception;
			}
		}
	}

	private Connection openConnection() throws SQLException
	{
		return DriverManager.getConnection("jdbc:sqlite:" + databasePath);
	}
}
