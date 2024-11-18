package org.example.scdpro2.business.models;

public abstract class Diagram {
    protected String title;

    public Diagram(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract void render(); // For UI purposes

    public abstract String toCode(); // For code generation purposes
}
