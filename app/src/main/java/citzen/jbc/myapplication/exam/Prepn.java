package citzen.jbc.myapplication.exam;

/**
 * Created by shyam on 10-Jun-17.
 */

public class Prepn {

    private String question,opa,opb,opc,opd,answer;

    public Prepn() {
    }

    public Prepn(String question, String opa, String opb, String opc, String opd, String answer) {
        this.question = question;
        this.opa = opa;
        this.opb = opb;
        this.opc = opc;
        this.opd = opd;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOpa() {
        return opa;
    }

    public void setOpa(String opa) {
        this.opa = opa;
    }

    public String getOpb() {
        return opb;
    }

    public void setOpb(String opb) {
        this.opb = opb;
    }

    public String getOpc() {
        return opc;
    }

    public void setOpc(String opc) {
        this.opc = opc;
    }

    public String getOpd() {
        return opd;
    }

    public void setOpd(String opd) {
        this.opd = opd;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
