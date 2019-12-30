package com.example.noticeboard10;

class Admin {
    String mKey,uid;
    Admin()
    {

    }
    Admin(String mKey,String uid)
    {
        this.mKey=mKey;
        this.uid=uid;
    }
    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
