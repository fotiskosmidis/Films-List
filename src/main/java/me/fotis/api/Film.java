package me.fotis.api;

import java.nio.file.Path;

public class Film
{       
        // enumeration for the state of the anime
        public enum State
        {
                WATCHING,
                COMPLETED,
                DROPPED,
                PLAN_TO_WATCH
        }

        private String title;
        private int rating;
        private Path imagePath;
        private State state;
        private String comments;

        // Setters
        public void setTitle(String title)
        {
                this.title = title;
        }

        public void setRating(int rating)
        {
                this.rating = rating;
        }

        public void setImagePath(Path imagePath)
        {
                this.imagePath = imagePath;
        }

        public void setState(State state)
        {
                this.state = state;
        }

        public void setComments(String comments)
        {
                this.comments = comments;
        }

        // Getters
        public String getTitle()
        {
                return title;
        }

        public int getRating()
        {
                return rating;
        }

        public Path getImagePath()
        {
                return imagePath;
        }

        public State getState()
        {
                return state;
        }

        public String getComments()
        {
                return comments;
        }

}