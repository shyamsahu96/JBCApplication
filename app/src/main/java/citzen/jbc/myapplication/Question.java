package citzen.jbc.myapplication;

/**
 * Created by chinmoydash on 26/05/17.
 */

public class Question {

    public String name,question,date;
    public Question(){

    }

    public Question(String name, String question, String date) {
        this.name = name;
        this.question = question;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
