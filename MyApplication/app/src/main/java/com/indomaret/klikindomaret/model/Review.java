package com.indomaret.klikindomaret.model;

/**
 * Created by USER on 4/14/2016.
 */
public class Review {

    String name;
    String date;
    String content;
    float point;

    public Review (String name, String date, String content, float point){
        this.name = name;
        this.date = date;
        this.content = content;
        this.point = point;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getPoint() {
        return point;
    }

    public void setPoint(float point) {
        this.point = point;
    }
}
