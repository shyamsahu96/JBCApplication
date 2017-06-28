package citzen.jbc.myapplication.firebase;

/**
 * Created by shyam on 25-Jun-17.
 */

public class ExamResult {

    String date, score;

    public ExamResult(String date, String score) {
        this.date = date;
        this.score = score;
    }

    public ExamResult() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
