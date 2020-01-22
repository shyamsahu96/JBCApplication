package citzen.jbc.myapplication.firebase;

/**
 * Created by shyam on 18-Jul-17.
 */

public class Report {

    public String name, report;

    public Report() {

    }

    public Report(String name, String report) {
        this.name = name;
        this.report = report;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
