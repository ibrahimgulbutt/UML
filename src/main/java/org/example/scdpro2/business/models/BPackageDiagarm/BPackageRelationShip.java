package org.example.scdpro2.business.models.BPackageDiagarm;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BPackageRelationShip<T extends javafx.scene.layout.BorderPane> {
    public T startPackageId;
    public T endPackageId;
    public String relationshipType;
    public double startX, startY, endX, endY;
    public BPackageRelationShip(T startPackageId, T endPackageId, String relationshipType) {
        this.startPackageId = startPackageId;
        this.endPackageId = endPackageId;
        this.relationshipType = relationshipType;
    }

    public T getStartPackageId() {
        return startPackageId;
    }

    public void setStartPackageId(T startPackageId) {
        this.startPackageId = startPackageId;
    }

    public T getEndPackageId() {
        return endPackageId;
    }

    public void setEndPackageId(T endPackageId) {
        this.endPackageId = endPackageId;
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
