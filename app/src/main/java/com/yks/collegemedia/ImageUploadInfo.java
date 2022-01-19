package com.yks.collegemedia;

import java.util.Comparator;

/**
 * Created by AndroidJSon.com on 6/10/2017.
 */


public class ImageUploadInfo {

    private String imageName;

    private String imageURL;

    private   String profilename;

    private String phonenumber;

    private  String likes;

    private  String imageid;

    private String postdate;

    private String type;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url,String profilename2,String phonenumber2,String likes,String imageid,String postdate,String type) {

        this.imageName = name;
        this.imageURL= url;
        this.profilename= profilename2;
        this.phonenumber=phonenumber2;
        this.likes=likes;
        this.imageid=imageid;
        this.postdate=postdate;
        this.type=type;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getprofilename() {
        return  profilename;
    }

    public  String getphonenumber(){ return  phonenumber;}

    public String getlikes(){return  likes;}

    public String getimageid(){return  imageid;}

    public String getpostdate(){return  postdate;}

    public String gettype(){return  type;}

}

