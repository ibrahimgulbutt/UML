package org.example.scdpro2.ui.views.PackageDiagram;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.Pane;
import org.example.scdpro2.business.models.BPackageDiagarm.BPackageRelationShip;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageDiagram;
import org.example.scdpro2.business.models.Project;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.ui.controllers.MainController;
import org.example.scdpro2.ui.views.MainView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageDiagramPane extends Pane {
    private final MainController controller;
    private final DiagramService diagramService;
    private final MainView mainView;

    private PackageBox selectedPackageBox; // To track the selected package box

    private final Map<PackageComponent, Node> packageToUIMap = new HashMap<>();
    private final Map<PackageBox, Node> classToUIMap = new HashMap<>();
    private PackageDiagram activePackageDiagram; // Current package diagram

    private ToggleButton relationshipModeButton = new ToggleButton("Relationship Mode");
    private final List<PackageRelationship> relationships = new ArrayList<>();

    private PackageBox relationshipSourceBox = null;
    private Node relationshipSourceNode = null; // Source node for relationshipsregisterPackageBox
    private PackageDiagram packageDiagram;

    private List<BPackageRelationShip> bPackageRelationShips = new ArrayList<>();




    public PackageDiagramPane(MainView mainView, MainController controller, DiagramService diagramService) {
        System.out.println("PackageDiagramPane is initialized.");
        this.mainView = mainView;
        this.controller = controller;
        this.diagramService = diagramService;

        this.activePackageDiagram = initializePackageDiagram();
        initializeRelationshipModeButton();
        loadPackagesFromDiagram();
    }

    public PackageComponent getPackageComponentById(String id) {
        // Iterate over the packages in the active package diagram
        for (PackageComponent packageComponent : activePackageDiagram.getPackages()) {
            // Check if the ID of the package component matches the provided ID
            if (packageComponent.getId().equals(id)) {
                return packageComponent; // Return the matching package component
            }
        }
        // If no match is found, return null
        return null;
    }


    // UI Functions
    private PackageDiagram initializePackageDiagram() {
        if (diagramService.getPackageDiagrams().isEmpty()) {
            PackageDiagram defaultDiagram = new PackageDiagram("Default Package Diagram");
            diagramService.addPackageDiagram(defaultDiagram);
            return defaultDiagram;
        }
        return diagramService.getPackageDiagrams().get(0);
    }

    private void initializeRelationshipModeButton() {
        relationshipModeButton.setOnAction(event -> {
            if (!relationshipModeButton.isSelected()) {
                relationshipSourceBox = null; // Reset source box on disabling the mode
            }
        });
        // Add the button to the UI, e.g., a toolbar or control area
    }

    public void removePackageComponent(PackageComponent packageComponent) {
        if (packageToUIMap.containsKey(packageComponent)) {
            packageToUIMap.remove(packageComponent);
            packageDiagram=controller.getPackagediagram();
            packageDiagram.removePackage(packageComponent);
            System.out.println("Package " + packageComponent.getName() + " removed from diagram.");
        }
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

    public void addClassBox(PackageClassBox classBox,PackageBox packageBox) {
        if (!getChildren().contains(classBox)) {
            registerPackageClassBox(classBox);
            getChildren().add(classBox);
            classToUIMap.put(packageBox, classBox);
        } else {
            System.out.println("Warning: PackageBox already exists in the pane.");
        }
    }

    public void clearSelectedPackage() {
        if (selectedPackageBox != null) {
            selectedPackageBox.setStyle("-fx-border-color: black;");
            selectedPackageBox = null;
        }
    }

    public void registerPackageBox(PackageBox packageBox) {
        packageBox.setOnMouseClicked(event -> handleRelationshipMode(packageBox));
    }

    public void registerPackageClassBox(PackageClassBox packageClassBox) {
        packageClassBox.setOnMouseClicked(event -> handleRelationshipMode(packageClassBox));
    }


    // Relationship functions
    private void handleRelationshipMode(Node selectedNode) {
        if (relationshipModeButton.isSelected()) {
            if (relationshipSourceNode == null) {
                relationshipSourceNode = selectedNode;
                selectedNode.setStyle("-fx-border-color: green;");
            } else if (relationshipSourceNode != selectedNode) {
                createRelationship(relationshipSourceNode, selectedNode);
                relationshipSourceNode.setStyle("-fx-border-color: black;");
                relationshipSourceNode = null;
            }
        }
    }

    private void createRelationship(Node source, Node target) {
        if (source == target) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Cannot create a relationship with the same component.");
            alert.show();
            return;
        }

        if (source instanceof PackageBox && target instanceof PackageBox) {
            createPackageRelationship((PackageBox) source, (PackageBox) target);
        } else if (source instanceof PackageBox && target instanceof PackageClassBox) {
            createPackageToClassRelationship((PackageBox) source, (PackageClassBox) target);
        } else if (source instanceof PackageClassBox && target instanceof PackageClassBox) {
            createClassToClassRelationship((PackageClassBox) source, (PackageClassBox) target);
        }
    }

    private void createPackageRelationship(PackageBox source, PackageBox target) {
        if (source == target) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Cannot create a relationship with the same package.");
            alert.show();
            return;
        }

        PackageRelationship relationship = new PackageRelationship(this, source, target);
        this.addRelationship(relationship);
        System.out.println("Relationship created between " + source.getPackageComponent().getName() + " and " + target.getPackageComponent().getName());
    }

    private void createPackageToClassRelationship(PackageBox packageBox, PackageClassBox classBox) {
        PackageRelationship relationship = new PackageRelationship(this, packageBox, classBox);
        this.addRelationship(relationship);
        System.out.println("Relationship created between Package " + packageBox.getPackageComponent().getName() +
                " and Class " + classBox.getNameField().getText());
    }

    private void createClassToClassRelationship(PackageClassBox sourceClass, PackageClassBox targetClass) {

        PackageRelationship relationship = new PackageRelationship(this, sourceClass, targetClass);
        this.addRelationship(relationship);
        System.out.println("Relationship created between Class " + sourceClass.getNameField().getText() +
                " and Class " + targetClass.getNameField().getText());
    }

    public void addRelationship(PackageRelationship relationship) {
        relationships.add(relationship);
        bPackageRelationShips.add(relationship.getBPackageRelationShip());
    }

    public void removeRelationship(PackageRelationship relationship) {
        relationships.remove(relationship);
        // Remove visual elements from the diagram pane (excluding Point2D objects)
        getChildren().removeAll(
                relationship.getHorizontalLine(),
                relationship.getVerticalLine(),
                relationship.getArrow(),
                relationship.getRelationshipLabel(),
                relationship.getStartAnchor(),
                relationship.getEndAnchor(),
                relationship.getIntersectionAnchor()
        );
    }

    // Business layer functions
    public void loadPackagesFromDiagram() {
        getChildren().clear();
        for (PackageComponent pkg : activePackageDiagram.getPackages()) {
            PackageBox packageBox = new PackageBox(pkg, controller, this);
            addPackageBox(packageBox);
        }
    }
    public void clearDiagrams() {
        getChildren().clear();
    }
    // Setter Getters
    public void setPackageModeEnabled(boolean isActive) {
        relationshipModeButton.setSelected(isActive);
        relationshipSourceBox = null; // Reset source when disabling
    }

    public List<PackageRelationship> getRelationships() {
        return relationships;
    }
}