package edu.cmu.sv.app17.models;

import java.util.Date;

public class Review {
    String reviewId = null;
    String userId;
    String showId;
    String createDate;
    String reviewTopic;
    String reviewContent;

    public Review(String showId,String userId,
                  String createDate,String reviewTopic, String reviewContent) {
        this.showId = showId;
        this.userId = userId;
        this.createDate = createDate;
        this.reviewTopic = reviewTopic;
        this.reviewContent = reviewContent;
    }

    public void setId(String id) {
        this.reviewId = id;
    }

}
