package com.example.taskmanagement.FireeBase;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.taskmanagement.FireeBase.User;

public class Note implements Parcelable {
    private String title;
    private  String description;//כוח סוס
    private  String importance; //בעלים
    private String photo;
    private boolean task=false;
    private boolean ischecked=false;
    public Note() {
    }

    public Note(String title,String  description,String importance, String photo,boolean task) {
        this.title = title;
        this.description = description;
        this.importance =importance ;
        this.photo = photo;
        this.task=task;
    }

    protected Note(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.importance = in.readString();
        this.photo = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.importance);
        dest.writeString(this.photo);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", importance='" + importance + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    public boolean isTask() {
        return task;
    }

    public void setTask(boolean task) {
        this.task = task;
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
