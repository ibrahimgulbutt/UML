package org.example.scdpro2.ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.AttributeComponent;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.OperationComponent;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageComponent;
import org.example.scdpro2.business.models.BPackageDiagarm.PackageDiagram;
import org.example.scdpro2.business.services.CodeGenerationService;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.business.services.ProjectService;
import org.example.scdpro2.ui.views.ClassDiagram.ClassBox;
import org.example.scdpro2.ui.views.ClassDiagram.ClassDiagramPane;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine;
import org.example.scdpro2.ui.views.MainView;
import org.example.scdpro2.ui.views.PackageDiagram.PackageBox;
import org.example.scdpro2.ui.views.PackageDiagram.PackageDiagramPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

class MainControllerTest {

    private MainController mainController;
    private MainView mockMainView;
    private DiagramService mockDiagramService;
    private ClassDiagramPane mockClassDiagramPane;
    private ProjectService mockProjectService;
    private CodeGenerationService mockCodeGenerationService;

    @BeforeEach
    void setup() {
        // Mock necessary components
        mockMainView = mock(MainView.class);
        mockDiagramService = mock(DiagramService.class);
        mockClassDiagramPane = mock(ClassDiagramPane.class);
        mockProjectService = mock(ProjectService.class);
        mockCodeGenerationService = mock(CodeGenerationService.class);

        // Mock the behavior of mainView.getClassDiagramPane()
        when(mockMainView.getClassDiagramPane()).thenReturn(mockClassDiagramPane);

        // Create a new instance of MainController for each test
        mainController = new MainController(mockDiagramService);
        mainController.setMainView(mockMainView);

        MockitoAnnotations.openMocks(this); // Initialize all mocks first
    }

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await(); // Wait for JavaFX to initialize
    }

    @Test
    void testCreateNewProject1() {
        mainController.createNewProject();

        Project currentProject = mainController.getCurrentProject();
        assertNotNull(currentProject, "New project should be created");
        assertEquals("New Project", currentProject.getName(), "Project name should be 'New Project'");
    }

    @Test
    void testSaveProjectToFile() throws IOException {
        // Simulate file handling with a temporary file
        File tempFile = File.createTempFile("testProject", ".ser");
        tempFile.deleteOnExit(); // Ensure the file is deleted after the test

        // Create a mock project and set it in the diagramService
        Project mockProject = new Project("Test Project");
        when(mockDiagramService.getCurrentProject()).thenReturn(mockProject);

        // Call the method
        mainController.saveProjectToFile(tempFile);

        // Verify that the file is being written to (this would normally be more specific with assertions)
        assertTrue(tempFile.exists(), "The project file should exist after saving");
    }

    @Test
    void testLoadProjectFromFile() throws IOException, ClassNotFoundException {
        // Prepare mock project and file
        Project mockProject = new Project("Test Project");
        File tempFile = File.createTempFile("testLoadProject", ".ser");
        tempFile.deleteOnExit();

        // Serialize mock project to the file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
            oos.writeObject(mockProject);
        }

        // Reset mock behaviors
        reset(mockClassDiagramPane, mockDiagramService);

        // Mock behaviors
        when(mockClassDiagramPane.getClassBoxForDiagram(any())).thenReturn(null);
        doNothing().when(mockClassDiagramPane).clearDiagrams();
        doNothing().when(mockDiagramService).setCurrentProject(any(Project.class));

        // Call the method
        mainController.loadProjectFromFile(tempFile);

        // Verify interactions and deserialized project
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(mockDiagramService).setCurrentProject(projectCaptor.capture());
        Project capturedProject = projectCaptor.getValue();

        assertNotNull(capturedProject, "The project should be loaded successfully");
        assertEquals("Test Project", capturedProject.getName(), "The loaded project should have the correct name");

        verify(mockClassDiagramPane).clearDiagrams();
    }

    @Test
    void testSaveProjectToFileWhenNoProject() {
        File tempFile = new File("temp.ser");

        when(mockDiagramService.getCurrentProject()).thenReturn(null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mainController.saveProjectToFile(tempFile);
        });

        assertEquals("No project to save", exception.getMessage(),
                "Should throw an appropriate exception when no project is set");
        verifyNoInteractions(mockClassDiagramPane);
    }

    @Test
    void testLoadInvalidProjectFile() throws IOException {
        File invalidFile = File.createTempFile("invalidProject", ".ser");
        invalidFile.deleteOnExit();

        // Write invalid binary content to the file
        try (FileOutputStream fos = new FileOutputStream(invalidFile)) {
            fos.write("Invalid content".getBytes());
        }

        Exception exception = assertThrows(IOException.class, () -> {
            mainController.loadProjectFromFile(invalidFile);
        });

        assertTrue(exception.getMessage().contains("invalid stream header"),
                "Should throw an appropriate exception for invalid file");
    }

    @Test
    void testAddClassBoxWithNullPane() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> mainController.addClassBox(null),
                "Should throw NullPointerException when ClassDiagramPane is null");
    }

    @Test
    void testAddInterfaceBoxWithNullPane() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> mainController.addInterfaceBox(null),
                "Should throw NullPointerException when ClassDiagramPane is null");
    }

    @Test
    void testCountRelationshipsBetween() {
        // Mock ClassBoxes
        ClassBox mockBox1 = mock(ClassBox.class);
        ClassBox mockBox2 = mock(ClassBox.class);

        // Mock RelationshipLine
        RelationshipLine mockLine1 = mock(RelationshipLine.class);
        RelationshipLine mockLine2 = mock(RelationshipLine.class);

        // Set up relationship mock behavior
        when(mockLine1.isConnectedTo(mockBox1)).thenReturn(true);
        when(mockLine1.isConnectedTo(mockBox2)).thenReturn(true);

        when(mockLine2.isConnectedTo(mockBox1)).thenReturn(true);
        when(mockLine2.isConnectedTo(mockBox2)).thenReturn(false);

        // Add relationships to pane
        ClassDiagramPane pane = new ClassDiagramPane(mock(MainView.class), mock(MainController.class), mock(DiagramService.class));
        pane.getRelationships().add(mockLine1);
        pane.getRelationships().add(mockLine2);

        // Call the hypothetical method
        List<RelationshipLine> connections = pane.relationshipBetween(mockBox1, mockBox2);

        // Verify
        assertEquals(1, connections.size(), "There should be one relationship between mockBox1 and mockBox2.");
        assertTrue(connections.contains(mockLine1), "The connection should include mockLine1.");
    }

    @Test
    void testAddClassBox() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Simulate the addition of a ClassBox
                mainController.addClassBox(mockClassDiagramPane);

                // Verify that the class box was added to the diagram pane
                verify(mockClassDiagramPane).addClassBox(any(ClassBox.class));

                // Ensure the diagram service was notified of the new class diagram
                verify(mockDiagramService).addDiagram(any(BClassBox.class));
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the JavaFX thread to complete the test
    }

    @Test
    void testAddInterfaceBox() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Simulate the addition of an InterfaceBox
                mainController.addInterfaceBox(mockClassDiagramPane);

                // Verify that the interface box was added to the diagram pane
                verify(mockClassDiagramPane).addClassBox(any(ClassBox.class));

                // Ensure the diagram service was notified of the new interface diagram
                verify(mockDiagramService).addDiagram(any(BClassBox.class));
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // Wait for the JavaFX thread to complete the test
    }

    @Test
    void testDeleteClassBoxWithNull() {
        mainController.deleteClassBox(mockClassDiagramPane, null);

        // Verify that no interactions occur with the pane or service if classBox is null
        verifyNoInteractions(mockClassDiagramPane, mockDiagramService);
    }

    @Test
    void testAddPackageBoxWithNoProject() {
        // Arrange
        PackageDiagramPane mockDiagramPane = mock(PackageDiagramPane.class);

        when(mockDiagramService.getCurrentProject()).thenReturn(null);
        PackageDiagram mockPackageDiagram = mock(PackageDiagram.class);
        when(mockDiagramService.getOrCreateActivePackageDiagram()).thenReturn(mockPackageDiagram);

        // Act
        mainController.addPackageBox(mockDiagramPane);

        // Assert
        verify(mockDiagramService).getOrCreateActivePackageDiagram();
        verify(mockDiagramPane).addPackageBox(any(PackageBox.class));
    }

    @Test
    void testAddPackageBox() {
        PackageDiagramPane mockDiagramPane = mock(PackageDiagramPane.class);
        PackageDiagram mockPackageDiagram = mock(PackageDiagram.class);
        when(mockDiagramService.getOrCreateActivePackageDiagram()).thenReturn(mockPackageDiagram);

        mainController.addPackageBox(mockDiagramPane);

        // Verify the package diagram is updated
        verify(mockPackageDiagram).addPackage(any(PackageComponent.class));

        // Verify the package box is added to the diagram pane
        verify(mockDiagramPane).addPackageBox(any(PackageBox.class));
    }

    @Test
    void testAddPackageDiagramWithNoProject() {
        when(mockDiagramService.getCurrentProject()).thenReturn(null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            mainController.addPackageDiagram();
        });

        assertEquals("No project loaded.", exception.getMessage(),
                "Should throw an exception when no project is loaded.");
    }

    @Test
    void testAddPackageDiagram() {
        // Mock a project and set it as the current project
        Project mockProject = mock(Project.class);
        when(mockDiagramService.getCurrentProject()).thenReturn(mockProject);

        // Call the method
        mainController.addPackageDiagram();

        // Verify the package diagram is added
        verify(mockDiagramService).addPackageDiagram(any(PackageDiagram.class));
    }

}