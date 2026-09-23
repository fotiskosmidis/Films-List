package me.fotis.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DataDir
{
	private static final String APP_NAME = "Films-List";

	private DataDir()
	{
	}

    // Its the Path+app name added
	public static Path getAppDataDirectory() throws IOException
	{
		Path appDataDirectory = getBaseDataDirectory().resolve(APP_NAME);
		Files.createDirectories(appDataDirectory);
		return appDataDirectory;
	}

    // Its Path+name+films.db
	public static Path getDatabasePath() throws IOException
	{
		return getAppDataDirectory().resolve("films.db");
	}

    // Its Path+name+images folder
	public static Path getImagesDirectory() throws IOException
	{
		Path imagesDirectory = getAppDataDirectory().resolve("images");
		Files.createDirectories(imagesDirectory);
		return imagesDirectory;
	}

    // Its the Path depending on the os
	private static Path getBaseDataDirectory()
	{
		Path userHome = Paths.get(System.getProperty("user.home"));

		if (System.getProperty("os.name").toLowerCase().contains("win"))
		{
			String appData = System.getenv("APPDATA");
			return appData == null || appData.isBlank()
					? userHome.resolve("AppData").resolve("Roaming")
					: Paths.get(appData);
		}

		String dataHome = System.getenv("XDG_DATA_HOME");
		return dataHome == null || dataHome.isBlank()
				? userHome.resolve(".local").resolve("share")
				: Paths.get(dataHome);
	}
}
