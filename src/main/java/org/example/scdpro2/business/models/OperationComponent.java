package org.example.scdpro2.business.models;

public class OperationComponent extends ModelComponent {
    private String visibility;
    private final String name;

    public OperationComponent(String name, String visibility) {
        super(name);
        this.name=name;
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    @Override
    public void validate() {
        // Add operation-specific validation here
    }

    public String getName() {
        return name;
    }

    @Override
    public String generateCode() {
        return visibility + " " + name + "() {}";
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

}
