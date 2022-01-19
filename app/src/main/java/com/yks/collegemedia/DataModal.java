package com.yks.collegemedia;


public class DataModal {

    // variables for storing our image and name.
    private String name;
    private String description;
    private String number;
    private String branch;
    private String year;
    private String verified;

    public DataModal() {
        // empty constructor required for firebase.
    }

    // constructor for our object class.
    public DataModal( String name,String description,String number , String branch,String year,String verified) {
        this.name = name;
        this.description = description;
        this.number=number;
        this.branch=branch;
        this.year=year;
        this.verified=verified;
    }

    // getter and setter methods
    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public String getNumber() {
        return number;
    }


    public String getBranch(){return branch;}

    public  String getYear(){return year;}


    public  String getverified(){return verified;}
}
//
//public void setDescription(String description) {
//    this.description = description;
//}
//public void setName(String name) {
//    this.name = name;
//}

////