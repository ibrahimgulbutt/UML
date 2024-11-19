package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.ModelComponent;

import java.io.Serializable;

public class AttributeComponent extends ModelComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String visibility;
    private String name;

    public AttributeComponent(String name, String visibility) {
        super(name);
        this.name=name;
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    @Override
    public void validate() {
        // Add attribute-specific validation here
    }
    public String getName() {
        return name;
    }

    @Override
    public String generateCode() {
        return visibility + " " + name + ";";
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setName(String newVal) {
        this.name=newVal;
    }
}
