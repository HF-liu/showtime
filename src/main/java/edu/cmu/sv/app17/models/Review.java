package edu.cmu.sv.app17.models;

public class Review {
    String reviewId = null;
    String showId;
    String episodeId;
    String userId;
    int rate;
    String createDate;
    String editDate;
    String reviewTopic;
    String reviewContent;
    int likes;

    public Review(String showId, String episodeId, String userId,
                  int rate, String createDate, String editDate,
                  String reviewTopic, String reviewContent, int likes) {
        this.showId = showId;
        this.episodeId = episodeId;
        this.userId = userId;
        this.rate = rate;
        this.createDate = createDate;
        this.editDate = editDate;
        this.reviewTopic = reviewTopic;
        this.reviewContent = reviewContent;
        this.likes = likes;
    }

    public void setId(String id) {
        this.reviewId = id;
    }

}
