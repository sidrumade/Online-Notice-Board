package com.example.noticeboard10;

public class SaveUrl {

    String date=null,url=null,discription=null,user=null,mKey=null;
    public SaveUrl()
    {

    }
    public SaveUrl(String date, String url, String disc, String user,String mKey)
    {
        this.date=date;
        this.url=url;
        this.user=user;
        this.mKey=mKey;
        if(disc.trim().equals(""))
        {
            this.discription="No Discription";
        }
        else{
            this.discription=disc;
        }


    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }
    public String getUser()
    {
        return user;
    }

    public String getDiscription() {
        return discription;
    }

    public String getmKey() {
        return mKey;
    }
}

