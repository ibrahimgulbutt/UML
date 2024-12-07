package org.example.scdpro2.business.models.BClassDiagarm;

import java.io.Serializable;

public class AttributeComponent extends ModelComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String visibility;
    private String name;
    private String datatype;

    public AttributeComponent(String name, String visibility,String datatype) {
        super(name);
        this.name=name;
        this.visibility = visibility;
        this.datatype=datatype;
    }


    @Override
    public String toString() {
        return visibility + '\'' +
                " name='" + name + '\'' +
                ':';
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
        return visibility + " " + name + "; "+ datatype;
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setName(String newVal) {
        this.name=newVal;
    }

    public void setDataType(String newVal) {
        this.datatype=newVal;
    }

    public String getDataType() {
        return this.datatype;
    }
}
