package com.example.edura.model;

public class RecentQuiz {
    private String title;
    private String progress;
    private String author;
    private int iconResId;

    public RecentQuiz(String title, String progress, String author, int iconResId) {
        this.title = title;
        this.progress = progress;
        this.author = author;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}

