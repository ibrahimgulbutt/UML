package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.ModelComponent;

public class AttributeComponent extends ModelComponent {
    private String visibility;
    private final String name;

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

}
