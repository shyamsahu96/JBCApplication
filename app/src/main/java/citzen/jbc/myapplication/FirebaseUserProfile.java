package citzen.jbc.myapplication;

/**
 * Created by shyam on 22-May-17.
 */

public class FirebaseUserProfile {

    private String name;
    private String email;
    private String college;
    private String password;
    private String referKey;

    public FirebaseUserProfile(){

    }

    FirebaseUserProfile(String name, String email, String college, String password,String referKey) {
        this.name = name;
        this.college=college;
        this.email=email;
        this.password=password;
        this.referKey=referKey;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getCollege(){
        return college;
    }

    public String getPassword(){
        return password;
    }

    public String getReferKey(){
        return referKey;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public void setCollege(String college){
        this.college=college;
    }

    public void setPassword(String password){
        this.password=password;
    }
    public void setReferKey(String referKey){
        this.referKey=referKey;
    }
}
