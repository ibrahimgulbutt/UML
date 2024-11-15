package org.example.scdpro2.business.models;

public class OperationComponent extends ModelComponent {
    private String visibility;

    public OperationComponent(String name, String visibility) {
        super(name);
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    @Override
    public void validate() {
        // Add operation-specific validation here
    }

    @Override
    public String generateCode() {
        return visibility + " " + name + "() {}";
    }
}
