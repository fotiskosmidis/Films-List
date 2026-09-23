package me.fotis.api;

import java.util.ArrayList;
import java.util.Comparator;

public class FilmList
{
        private static final ArrayList<Film> films = new ArrayList<>();

        public void addFilm(Film film)
        {
                films.add(film);
        }

        public void removeFilm(Film film)
        {
                films.remove(film);
        }

        public ArrayList<Film> searchFilms(String title)
        {
                ArrayList<Film> results = new ArrayList<>();
                for (Film film : films)
                {
                        if (film.getTitle().toLowerCase().contains(title.toLowerCase()))
                        {
                                results.add(film);
                        }
                }

                return results;
        }

        public void setFilm(Film film, String newTitle, int newRating, Film.State newState, String newComments)
        {
                film.setTitle(newTitle);
                film.setRating(newRating);
                film.setState(newState);
                film.setComments(newComments);
        }

        public ArrayList<Film> getFilms()
        {
                return films;
        }

        public int size()
        {
                return films.size();
        }

        public void sortFilmListByRating()
        {
                films.sort(Comparator.comparingInt(Film::getRating).reversed());
        }
}
