package com.example.project_basic;

public class pointHistory {
    String type;
    String point;
    String day;



    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public pointHistory(String day, String point, String type) {
        this.day = day;
        this.point = point;
        this.type = type;
    }
}
