package com.example.noticeboard10;

public class Notes {
    String date=null,message=null,mKey=null,user=null;
    public Notes()
    {

    }
    public Notes(String date, String message, String mKey, String user)
    {
        this.date=date;
        this.message=message;
        this.mKey=mKey;
        this.user=user;

    }
    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
    public String getmKey() {
        return mKey;
    }
    public String getUser()
    {
        return user;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
