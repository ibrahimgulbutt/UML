package org.example.scdpro2.business.models;

import java.io.Serializable;

public abstract class Diagram implements Serializable {
    private static final long serialVersionUID = 1L;
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


    public abstract DiagramType getType();
}
