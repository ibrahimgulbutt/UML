package org.example.scdpro2.ui.views;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.example.scdpro2.business.models.PackageComponent;
import org.example.scdpro2.business.models.PackageDiagram;
import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.controllers.MainController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageDiagramPane extends Pane {
    private final MainController controller;
    private final DiagramService diagramService;
    private final MainView mainView;

    private final List<RelationshipLine> relationshipLines = new ArrayList<>();

    private PackageBox selectedPackageBox; // To track the selected package box
    private final Map<PackageComponent, Node> packageToUIMap = new HashMap<>();
    private PackageDiagram activePackageDiagram; // Current package diagram

    public PackageDiagramPane(MainView mainView, MainController controller, DiagramService diagramService) {
        System.out.println("PackageDiagramPane is initialized.");
        this.mainView = mainView;
        this.controller = controller;
        this.diagramService = diagramService;

        this.activePackageDiagram = initializePackageDiagram();
        loadPackagesFromDiagram();
    }

    private PackageDiagram initializePackageDiagram() {
        if (diagramService.getPackageDiagrams().isEmpty()) {
            PackageDiagram defaultDiagram = new PackageDiagram("Default Package Diagram");
            diagramService.addPackageDiagram(defaultDiagram);
            return defaultDiagram;
        }
        return diagramService.getPackageDiagrams().get(0);
    }

    public void addPackageBox(PackageBox packageBox) {
        if (!getChildren().contains(packageBox)) {
            registerPackageBox(packageBox);
            getChildren().add(packageBox);
            packageToUIMap.put(packageBox.getPackageComponent(), packageBox);
        } else {
            System.out.println("Warning: PackageBox already exists in the pane.");
        }
    }

    public void removePackageBox(PackageBox packageBox) {
        getChildren().remove(packageBox);
        packageToUIMap.remove(packageBox.getPackageComponent());
        activePackageDiagram.removePackage(packageBox.getPackageComponent());
        System.out.println("Removed package: " + packageBox.getPackageComponent().getName());
    }

    public void registerPackageBox(PackageBox packageBox) {
        packageBox.setOnMouseClicked(event -> {
            System.out.println("PackageBox clicked: " + packageBox.getPackageComponent().getName());
            if (selectedPackageBox == null) {
                selectedPackageBox = packageBox;
                packageBox.setStyle("-fx-border-color: blue;");
            } else {
                selectedPackageBox.setStyle("-fx-border-color: black;");
                selectedPackageBox = null;
            }
        });
    }

    public void clearSelectedPackage() {
        if (selectedPackageBox != null) {
            selectedPackageBox.setStyle("-fx-border-color: black;");
            selectedPackageBox = null;
        }
    }

    public void loadPackagesFromDiagram() {
        getChildren().clear();
        for (PackageComponent pkg : activePackageDiagram.getPackages()) {
            PackageBox packageBox = new PackageBox(pkg, controller, this);
            addPackageBox(packageBox);
        }
    }

    public void addNewPackage(String name) {
        PackageComponent newPackage = new PackageComponent(name);
        activePackageDiagram.addPackage(newPackage);
        PackageBox packageBox = new PackageBox(newPackage, controller, this);
        addPackageBox(packageBox);
    }

    public void loadPackagesFromProject(Project project) {
        getChildren().clear();
        //for (PackageDiagram diagram : project.getPackageDiagrams()) {
        //    this.activePackageDiagram = diagram; // Load one diagram at a time
        //    loadPackagesFromDiagram();
        //    break; // Load only the first diagram for now
        // }
    }

    // public Map<PackageComponent, Node> getPackageToUIMap() {
    //     return packageToUIMap;
    //}


    public void addRelationshipLine(PackageComponent source, PackageComponent target, String title) {
        // Create the relationship line
        PackageBox sourceBox = (PackageBox) lookup("#" + source.getId());
        PackageBox targetBox = (PackageBox) lookup("#" + target.getId());

        if (sourceBox != null && targetBox != null) {
            double startX = sourceBox.getLayoutX() + sourceBox.getWidth() / 2;
            double startY = sourceBox.getLayoutY() + sourceBox.getHeight() / 2;
            double endX = targetBox.getLayoutX() + targetBox.getWidth() / 2;
            double endY = targetBox.getLayoutY() + targetBox.getHeight() / 2;

            RelationshipLine relationship = new RelationshipLine(source, target, startX, startY, endX, endY, title);

            relationshipLines.add(relationship);
            getChildren().addAll(relationship.getLine(), relationship.getRelationshipLabel());
        }
    }

    public void removeRelationshipLine(RelationshipLine relationship) {
        getChildren().removeAll(relationship.getLine(), relationship.getRelationshipLabel());
        relationshipLines.remove(relationship);
    }

    public void updateAllRelationships() {
        for (RelationshipLine relationship : relationshipLines) {
            PackageBox sourceBox = (PackageBox) lookup("#" + relationship.getSourcePackage().getId());
            PackageBox targetBox = (PackageBox) lookup("#" + relationship.getTargetPackage().getId());

            if (sourceBox != null && targetBox != null) {
                double startX = sourceBox.getLayoutX() + sourceBox.getWidth() / 2;
                double startY = sourceBox.getLayoutY() + sourceBox.getHeight() / 2;
                double endX = targetBox.getLayoutX() + targetBox.getWidth() / 2;
                double endY = targetBox.getLayoutY() + targetBox.getHeight() / 2;

                relationship.updatePosition(startX, startY, endX, endY);
            }
        }
    }
}
