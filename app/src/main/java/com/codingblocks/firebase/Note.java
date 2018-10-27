package com.codingblocks.firebase;

public class Note {
    String title;//it must be primitive because the firebase database only saves primitive data and not non primitive data
    String subtitle;

    public Note() {
    }

    public Note(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
