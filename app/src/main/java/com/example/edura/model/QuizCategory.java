package com.example.edura.model;

public class QuizCategory {
    private String name;
    private int gradientResId;

    public QuizCategory(String name, int gradientResId) {
        this.name = name;
        this.gradientResId = gradientResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGradientResId() {
        return gradientResId;
    }

    public void setGradientResId(int gradientResId) {
        this.gradientResId = gradientResId;
    }
}

