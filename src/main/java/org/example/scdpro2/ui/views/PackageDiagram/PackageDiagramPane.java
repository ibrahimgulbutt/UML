package org.example.scdpro2.ui.views.PackageDiagram;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import org.example.scdpro2.business.models.BPackageDiagarm.BPackageRelationShip;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
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

    private ToggleButton relationshipModeButton = new ToggleButton("Relationship Mode");
    private final List<PackageRelationship> relationships = new ArrayList<>();

    private PackageBox relationshipSourceBox = null;
    private Node relationshipSourceNode = null; // Source node for relationshipsregisterPackageBox

    private List<BPackageRelationShip> bPackageRelationShips = new ArrayList<>();




    public PackageDiagramPane(MainView mainView, MainController controller, DiagramService diagramService) {
        System.out.println("PackageDiagramPane is initialized.");
        this.mainView = mainView;
        this.controller = controller;
        this.diagramService = diagramService;
        initializeRelationshipModeButton();
        loadPackagesFromDiagram();
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
            System.out.println("Package " + packageComponent.getName() + " removed from diagram.");
        }
    }

    public Node findNodeById(String Id) {
        // Iterate through all the nodes in the package diagram
        for (Node node : getChildren()) {
            // Check if the node is a PackageBox and if its name matches
            if (node instanceof PackageBox) {
                PackageBox packageBox = (PackageBox) node;
                System.out.println(packageBox.getId());
                if (packageBox.getId().equals(Id))
                {
                    return packageBox;
                }
            }
            // Check if the node is a PackageClassBox and if its name matches
            else if (node instanceof PackageClassBox) {
                PackageClassBox classBox = (PackageClassBox) node;
                if (classBox.getId().equals(Id))
                {
                    return classBox;
                }
            }
        }
        return null; // Return null if no node with the given name is found
    }


    public void addPackageBox(PackageBox packageBox) {
        if (!getChildren().contains(packageBox)) {
            System.out.println("I am called bpsssssssss");
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

    public PackageBox getPackageBoxForDiagram(PackageComponent diagram) {
        for (Node node : getChildren()) {
            if (node instanceof PackageBox packageBox && packageBox.getPackageDiagram().equals(diagram)) {
                return packageBox;
            }
        }
        return null;
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

    public PackageBox createPackageBoxForDiagram(PackageComponent packageComponent) {
        PackageBox packageBox = new PackageBox(packageComponent, controller, this);
        addPackageBox(packageBox); // Add to the UI (pane)
        return packageBox;
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

    public void createRelationship(Node source, Node target) {
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
        controller.addPackageRelationship(relationship);
        System.out.println("Relationship created between " + source.getPackageComponent().getName() + " and " + target.getPackageComponent().getName());
    }

    private void createPackageToClassRelationship(PackageBox packageBox, PackageClassBox classBox) {
        PackageRelationship relationship = new PackageRelationship(this, packageBox, classBox);
        this.addRelationship(relationship);
        controller.addPackageRelationship(relationship);
        System.out.println("Relationship created between Package " + packageBox.getPackageComponent().getName() +
                " and Class " + classBox.getNameField().getText());
    }

    private void createClassToClassRelationship(PackageClassBox sourceClass, PackageClassBox targetClass) {

        PackageRelationship relationship = new PackageRelationship(this, sourceClass, targetClass);
        this.addRelationship(relationship);
        controller.addPackageRelationship(relationship);
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
        //for (PackageComponent pkg : activePackageDiagram.getPackages()) {
        //    PackageBox packageBox = new PackageBox(pkg, controller, this);
        //    addPackageBox(packageBox);
        //}
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