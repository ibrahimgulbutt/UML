package org.example.scdpro2.business.models.BPackageDiagarm;

import java.io.Serializable;

public class BPackageRelationShip<T extends javafx.scene.layout.BorderPane> implements Serializable {
    private static final long serialVersionUID = 1L;
    public String startPackagename;
    public String endPackagename;
    public String relationshipType;
    public double startX, startY, endX, endY;
    public BPackageRelationShip(String startPackagename, String endPackagename) {
        this.startPackagename = startPackagename;
        this.endPackagename = endPackagename;
    }

    public String getStartPackageid() {
        return startPackagename;
    }

    public void setStartPackagename(String startPackagename) {
        this.startPackagename = startPackagename;
    }

    public String getEndPackageid() {
        return endPackagename;
    }

    public void setEndPackagename(String endPackagename) {
        this.endPackagename = endPackagename;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }
}
