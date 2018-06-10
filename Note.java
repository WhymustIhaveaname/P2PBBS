package com.example.administrator.myapplication;

public class Note {
    private int hash;
    private String note;
    //private String notetime;

    public Note(int hash, String note){
        this.hash = hash;
        this.note = note;
        //this.notetime = notetime;
    }

    public int getHash(){
        return hash;
    }

    public String getNote(){
        return note;
    }
}
