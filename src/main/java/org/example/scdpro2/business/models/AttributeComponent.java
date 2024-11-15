package org.example.scdpro2.business.models;

import org.example.scdpro2.business.models.ModelComponent;

public class AttributeComponent extends ModelComponent {
    private String visibility;

    public AttributeComponent(String name, String visibility) {
        super(name);
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    @Override
    public void validate() {
        // Add attribute-specific validation here
    }

    @Override
    public String generateCode() {
        return visibility + " " + name + ";";
    }
}
