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
/**
 * Represents the pane that holds and manages the package diagram components.
 * This class provides methods for adding, removing, and managing the relationships
 * between package and class boxes within the diagram.
 */
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



    /**
     * Initializes a new instance of the PackageDiagramPane class.
     *
     * @param mainView The main view of the application.
     * @param controller The controller that manages the application's logic.
     * @param diagramService The service responsible for managing diagrams.
     */
    public PackageDiagramPane(MainView mainView, MainController controller, DiagramService diagramService) {
        System.out.println("PackageDiagramPane is initialized.");
        this.mainView = mainView;
        this.controller = controller;
        this.diagramService = diagramService;
        initializeRelationshipModeButton();
        loadPackagesFromDiagram();
    }
    /**
     * Initializes the relationship mode button that toggles relationship creation between diagram elements.
     */
    private void initializeRelationshipModeButton() {
        relationshipModeButton.setOnAction(event -> {
            if (!relationshipModeButton.isSelected()) {
                relationshipSourceBox = null; // Reset source box on disabling the mode
            }
        });
        // Add the button to the UI, e.g., a toolbar or control area
    }
    /**
     * Removes the specified package component from the diagram.
     *
     * @param packageComponent The package component to remove.
     */
    public void removePackageComponent(PackageComponent packageComponent) {
        if (packageToUIMap.containsKey(packageComponent)) {
            packageToUIMap.remove(packageComponent);
            System.out.println("Package " + packageComponent.getName() + " removed from diagram.");
        }
    }
    /**
     * Finds a node in the diagram by its ID.
     *
     * @param Id The ID of the node to find.
     * @return The node with the specified ID, or null if no node is found.
     */
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

    /**
     * Adds a package box to the diagram.
     *
     * @param packageBox The package box to add.
     */
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
    /**
     * Adds a class box to the diagram.
     *
     * @param classBox The class box to add.
     * @param packageBox The package box that contains the class box.
     */
    public void addClassBox(PackageClassBox classBox,PackageBox packageBox) {
        if (!getChildren().contains(classBox)) {
            registerPackageClassBox(classBox);
            getChildren().add(classBox);
            classToUIMap.put(packageBox, classBox);
        } else {
            System.out.println("Warning: PackageBox already exists in the pane.");
        }
    }
    /**
     * Retrieves the package box for the specified diagram component.
     *
     * @param diagram The package component to get the associated package box for.
     * @return The package box associated with the diagram, or null if not found.
     */
    public PackageBox getPackageBoxForDiagram(PackageComponent diagram) {
        for (Node node : getChildren()) {
            if (node instanceof PackageBox packageBox && packageBox.getPackageDiagram().equals(diagram)) {
                return packageBox;
            }
        }
        return null;
    }
    /**
     * Clears the selection of the currently selected package.
     */
    public void clearSelectedPackage() {
        if (selectedPackageBox != null) {
            selectedPackageBox.setStyle("-fx-border-color: black;");
            selectedPackageBox = null;
        }
    }
    /**
     * Registers a package box to handle mouse click events for relationship mode.
     *
     * @param packageBox The package box to register.
     */
    public void registerPackageBox(PackageBox packageBox) {
        packageBox.setOnMouseClicked(event -> handleRelationshipMode(packageBox));
    }
    /**
     * Registers a class box to handle mouse click events for relationship mode.
     *
     * @param packageClassBox The class box to register.
     */
    public void registerPackageClassBox(PackageClassBox packageClassBox) {
        packageClassBox.setOnMouseClicked(event -> handleRelationshipMode(packageClassBox));
    }
    /**
     * Creates a package box for the specified diagram component and adds it to the diagram.
     *
     * @param packageComponent The diagram component to create the package box for.
     * @return The created package box.
     */
    public PackageBox createPackageBoxForDiagram(PackageComponent packageComponent) {
        PackageBox packageBox = new PackageBox(packageComponent, controller, this);
        addPackageBox(packageBox); // Add to the UI (pane)
        return packageBox;
    }

    // Relationship functions
    /**
     * Handles the logic for relationship mode when a package or class box is selected.
     *
     * @param selectedNode The selected node (either a package or class box).
     */
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
    /**
     * Creates a relationship between two diagram components (package or class boxes).
     *
     * @param source The source node of the relationship.
     * @param target The target node of the relationship.
     */
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
    /**
     * Creates a relationship between two package boxes.
     *
     * @param source The source package box.
     * @param target The target package box.
     */
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
    /**
     * Creates a relationship between a package box and a class box.
     *
     * @param packageBox The package box.
     * @param classBox The class box.
     */
    private void createPackageToClassRelationship(PackageBox packageBox, PackageClassBox classBox) {
        PackageRelationship relationship = new PackageRelationship(this, packageBox, classBox);
        this.addRelationship(relationship);
        controller.addPackageRelationship(relationship);
        System.out.println("Relationship created between Package " + packageBox.getPackageComponent().getName() +
                " and Class " + classBox.getNameField().getText());
    }
    /**
     * Creates a relationship between two class boxes.
     *
     * @param sourceClass The source class box.
     * @param targetClass The target class box.
     */
    private void createClassToClassRelationship(PackageClassBox sourceClass, PackageClassBox targetClass) {

        PackageRelationship relationship = new PackageRelationship(this, sourceClass, targetClass);
        this.addRelationship(relationship);
        controller.addPackageRelationship(relationship);
        System.out.println("Relationship created between Class " + sourceClass.getNameField().getText() +
                " and Class " + targetClass.getNameField().getText());
    }
    /**
     * Adds a relationship to the list of relationships.
     *
     * @param relationship The relationship to add.
     */
    public void addRelationship(PackageRelationship relationship) {
        relationships.add(relationship);
        bPackageRelationShips.add(relationship.getBPackageRelationShip());
    }
    /**
     * Removes a relationship from the diagram and the relationship list.
     *
     * @param relationship The relationship to remove.
     */
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
    /**
     * Loads packages from the diagram and displays them on the UI.
     */
    public void loadPackagesFromDiagram() {
        getChildren().clear();
        //for (PackageComponent pkg : activePackageDiagram.getPackages()) {
        //    PackageBox packageBox = new PackageBox(pkg, controller, this);
        //    addPackageBox(packageBox);
        //}
    }
    /**
     * Clears all elements in the diagram.
     */
    public void clearDiagrams() {
        getChildren().clear();
    }
    // Setter Getters
    /**
     * Sets whether the relationship mode is enabled or not.
     *
     * @param isActive True to enable relationship mode, false to disable.
     */
    public void setPackageModeEnabled(boolean isActive) {
        relationshipModeButton.setSelected(isActive);
        relationshipSourceBox = null; // Reset source when disabling
    }
    /**
     * Retrieves the list of relationships in the diagram.
     *
     * @return A list of all relationships.
     */
    public List<PackageRelationship> getRelationships() {
        return relationships;
    }
}