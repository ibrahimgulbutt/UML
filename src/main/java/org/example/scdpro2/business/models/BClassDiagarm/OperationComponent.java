package org.example.scdpro2.business.models.BClassDiagarm;

import java.io.Serializable;

public class OperationComponent extends ModelComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String visibility;
    private String name;
    private String returntype;

    public OperationComponent(String name, String visibility,String returntype) {
        super(name);
        this.name=name;
        this.visibility = visibility;
        this.returntype=returntype;
    }

    @Override
    public String toString() {
        return "visibility='" + visibility + '\'' +
                ", name='" + name + '\'' + returntype+
                '}';
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
        return visibility + " " + returntype + " " + name + "() {}";
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setName(String newName) {
        this.name = newName; // Update the correct field
    }


    public void setReturnType(String text) {
        this.returntype=text;
    }

    public String getReturnType() {
        return returntype;
    }
}
