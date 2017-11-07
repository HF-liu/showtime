package edu.cmu.sv.app17.models;

public class Episode {

        String episodeId = null;
        String showId, showName, channel, cast, summary, showTime, reviews;
        Number season;
        Double epiRating;

        public Episode(String showId, String showName, String channel, String cast,
                       String summary, String showTime, String reviews, Number season,
                       Double epiRating) {
            this.showId = showId;
            this.showName = showName;
            this.channel = channel;
            this.cast = cast;
            this.summary = summary;
            this.showTime = showTime;
            this.reviews = reviews;
            this.season = season;
            this.epiRating = epiRating;
        }

        public void setId(String id) {
            this.episodeId = id;
        }
    }


