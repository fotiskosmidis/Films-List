# Films List

A lightweight desktop film tracker built with JavaFX. Keep a personal list of films, add your own ratings and notes, and find something to watch with search and status filters.

## Features

- Add films with a title, rating from 0 to 10, watch status, comments, and an optional image.
- Edit or delete saved films from the film details view.
- Search titles as you type.
- Filter films by status: `WATCHING`, `COMPLETED`, `DROPPED`, or `PLAN_TO_WATCH`.
- Sort the list by rating, highest first.
- Persist data locally in a SQLite database.
- Store selected film images in the application data directory.
- Use a responsive JavaFX interface that scales for high-DPI displays.

## Requirements

- Java Development Kit (JDK) 17 or newer
- Apache Maven 3.9 or newer
- A graphical desktop environment supported by JavaFX

JavaFX and the SQLite JDBC driver are downloaded automatically by Maven.

## Run the application

Clone the repository and start the JavaFX application with Maven:

```bash
git clone https://github.com/<your-username>/Films-List.git
cd Films-List
mvn javafx:run
```

The application creates its local data directory on first launch. No external database server or account is required.

## Download installers

To install the application without Java or Maven, open the repository's [Releases](https://github.com/fotiskosmidis/Films-List/releases) page and download the installer for your operating system:

- **Debian, Ubuntu, and Linux Mint:** download the `.deb` file, then run `sudo apt install ./Films-List_*.deb` from the directory containing it.
- **Windows:** download the `.exe` installer and follow the setup wizard.

New installers are created automatically whenever a version tag such as `v1.0.0` is pushed to GitHub. The source-based Maven instructions below are intended for developers.

## Build and test

Compile the project:

```bash
mvn clean package
```
## Data storage

Films are stored in `films.db`, and copied images are stored in the `images` subdirectory.

| Platform | Application data directory |
| --- | --- |
| Windows | `%APPDATA%/Films-List` |
| Linux and other Unix-like systems | `$XDG_DATA_HOME/Films-List`, or `~/.local/share/Films-List` when `XDG_DATA_HOME` is not set |

The database is intentionally local to the current user. Removing the application data directory removes the saved film list and copied images.

## Project structure

```text
src/main/java/me/fotis/
├── App.java                  # Application entry point
├── api/
│   ├── Database.java         # SQLite persistence
│   ├── DataDir.java          # Platform-aware data paths
│   ├── Film.java             # Film model and watch states
│   └── FilmList.java         # List, search, and sorting operations
└── gui/                      # JavaFX windows and event handlers
```
