package citzen.jbc.myapplication.firebase;

/**
 * Created by shyam on 22-May-17.
 */

public class FirebaseUserProfile {

    public String name;
    public String email;
    public String college;
    public String password;
    public String referKey;
    public int refPoints;

    public FirebaseUserProfile() {

    }

    public FirebaseUserProfile(String name, String email, String college, String password, String referKey) {
        this.name = name;
        this.college = college;
        this.email = email;
        this.password = password;
        this.referKey = referKey;
    }


    public FirebaseUserProfile(String name, String email, String college, String pass, String refKey, int refPoints) {
        this.name = name;
        this.college = college;
        this.email = email;
        this.password = pass;
        this.referKey = refKey;
        this.refPoints = refPoints;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCollege() {
        return college;
    }

    public String getPassword() {
        return password;
    }

    public String getReferKey() {
        return referKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setReferKey(String referKey) {
        this.referKey = referKey;
    }

    public int getRefPoints() {
        return refPoints;
    }

    public void setRefPoints(int refPoints) {
        this.refPoints = refPoints;
    }
}
