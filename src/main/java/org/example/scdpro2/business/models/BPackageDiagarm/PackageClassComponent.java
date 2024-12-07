package org.example.scdpro2.business.models.BPackageDiagarm;

import java.util.ArrayList;

public class PackageClassComponent {
    private PackageComponent parent; // Parent package component reference
    private String name; // Class name
    private String visibility; // Visibility (+, -, #)
    private int xCoordinates; // X-coordinate position
    private int yCoordinates; // Y-coordinate position
    private ArrayList<BPackageRelationShip> bPackageRelationShips;


    // Constructor
    public PackageClassComponent(PackageComponent parent, String name, String visibility) {
        this.parent = parent;
        this.name = validateName(name);
        this.visibility = validateVisibility(visibility);
        this.xCoordinates = 0; // Default initial position
        this.yCoordinates = 0; // Default initial position
        this.bPackageRelationShips=new ArrayList<>();
    }

    // Name getter and setter with validation
    public String getName() {
        return name;
    }

    public void setName(String newValue) {
        this.name = validateName(newValue);
    }

    // Visibility getter and setter with validation
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String newValue) {
        this.visibility = validateVisibility(newValue);
    }

    // Coordinates getters and setters
    public int getXCoordinates() {
        return xCoordinates;
    }

    public void setXCoordinates(int xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public int getYCoordinates() {
        return yCoordinates;
    }

    public void setYCoordinates(int yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    // Parent package getter and setter
    public PackageComponent getParent() {
        return parent;
    }

    public void setParent(PackageComponent parent) {
        this.parent = parent;
    }

    // Validation methods for input
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be null or empty.");
        }
        return name.trim();
    }

    private String validateVisibility(String visibility) {
        if (!"+".equals(visibility) && !"-".equals(visibility) && !"#".equals(visibility)) {
            throw new IllegalArgumentException("Invalid visibility. Allowed values are: +, -, #.");
        }
        return visibility;
    }

    // Debugging and logging support
    @Override
    public String toString() {
        return "PackageClassComponent{" +
                "name='" + name + '\'' +
                ", visibility='" + visibility + '\'' +
                ", xCoordinates=" + xCoordinates +
                ", yCoordinates=" + yCoordinates +
                '}';
    }
}
