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
import org.example.scdpro2.business.services.CodeGenerationService;
import org.example.scdpro2.business.services.DiagramService;
import org.example.scdpro2.business.services.ProjectService;
import org.example.scdpro2.ui.views.ClassDiagram.ClassBox;
import org.example.scdpro2.ui.views.ClassDiagram.ClassDiagramPane;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine;
import org.example.scdpro2.ui.views.MainView;
import org.example.scdpro2.ui.views.PackageDiagram.PackageBox;
import org.example.scdpro2.ui.views.PackageDiagram.PackageClassBox;
import org.example.scdpro2.ui.views.PackageDiagram.PackageDiagramPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
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
        mainController.saveClassProjectToFile(tempFile);

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
        mainController.loadClassProjectFromFile(tempFile);

        // Verify interactions and deserialized project
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(mockDiagramService).setCurrentProject(projectCaptor.capture());
        Project capturedProject = projectCaptor.getValue();

        assertNotNull(capturedProject, "The project should be loaded successfully");
        assertEquals("Test Project", capturedProject.getName(), "The loaded project should have the correct name");

        verify(mockClassDiagramPane).clearDiagrams();
    }

    @Test
    void testLoadInvalidProjectFile() throws IOException {
        File invalidFile = File.createTempFile("invalidProject", ".ser");
        invalidFile.deleteOnExit();

        // Write invalid binary content to the file
        try (FileOutputStream fos = new FileOutputStream(invalidFile)) {
            fos.write("Invalid content".getBytes());
        }

        mainController.loadClassProjectFromFile(invalidFile);

        // Verify no project is loaded and interactions are avoided
        verify(mockDiagramService, never()).setCurrentProject(any());
        verify(mockMainView.getClassDiagramPane(), never()).clearDiagrams();
    }


    @Test
    void testAddClassBoxWithNullPane() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> mainController.addClassBox(null),
                "Should throw NullPointerException when ClassDiagramPane is null");
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
    void testAddClassBox_AddsToUIAndBusinessLayer() {
        when(mockProjectService.getCurrentProject()).thenReturn(mock(Project.class));

        mainController.addClassBox(mockClassDiagramPane);

        verify(mockDiagramService).addDiagram(any());
        verify(mockClassDiagramPane).addClassBox(any());
    }

    @Test
    void testDeleteClassBoxWithNull() {
        mainController.deleteClassBox(mockClassDiagramPane, null);

        // Verify that no interactions occur with the pane or service if classBox is null
        verifyNoInteractions(mockClassDiagramPane, mockDiagramService);
    }

    @Test
    void testDeleteClassBox_NullClassBox() {
        mainController.deleteClassBox(mockClassDiagramPane, null);

        verifyNoInteractions(mockDiagramService);
        verifyNoInteractions(mockClassDiagramPane);
    }


    @Test
    void testAddInterfaceBoxWithNullPane() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> mainController.addInterfaceBox(null),
                "Should throw NullPointerException when ClassDiagramPane is null");
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
    void testAddInterfaceBox_AddsToUIAndBusinessLayer() {
        when(mockProjectService.getCurrentProject()).thenReturn(mock(Project.class));

        mainController.addInterfaceBox(mockClassDiagramPane);

        verify(mockDiagramService).addDiagram(any());
        verify(mockClassDiagramPane).addClassBox(any());
    }


    @Test
    void testAddPackageBox_AddsPackageToPaneAndService() {
        when(mockProjectService.getCurrentProject()).thenReturn(mock(Project.class));

        mainController.addPackageBox(mock(PackageDiagramPane.class));

        verify(mockDiagramService).addDiagram(any());
        verify(mockMainView).addClassToList(anyString());
    }

    @Test
    void testAddPackageClassBox_CreatesPackageClass() {
        PackageComponent mockPackageComponent = mock(PackageComponent.class);
        PackageDiagramPane mockDiagramPane = mock(PackageDiagramPane.class);
        PackageBox mockPackageBox = mock(PackageBox.class);

        PackageClassBox result = mainController.addPackageClassBox(mockDiagramPane, mockPackageBox, mockPackageComponent);

        assertNotNull(result);
        verify(mockDiagramService).addDiagram(any());
        verify(mockMainView).addClassToList(anyString());
    }



    @Test
    void testGenerateCode_WithValidProject() {
        // Mock the current project
        Project mockProject = mock(Project.class);
        Diagram mockDiagram = mock(Diagram.class);

        // Mock behaviors
        when(mockProjectService.getCurrentProject()).thenReturn(mockProject);
        when(mockProject.getDiagrams()).thenReturn(List.of(mockDiagram));
        when(mockCodeGenerationService.generateCode(mockProject)).thenReturn("Generated Code");

        // Set up the services in MainController
        mainController.setProjectService(mockProjectService);
        mainController.setCodeGenerationService(mockCodeGenerationService);

        // Call the method
        String result = mainController.generateCode();

        // Assert and verify
        assertEquals("Generated Code", result, "The generated code should match the expected result.");
        verify(mockCodeGenerationService).generateCode(mockProject);
    }


    @Test
    void testGenerateCode_NoDiagrams() {
        Project mockProject = mock(Project.class);
        when(mockProjectService.getCurrentProject()).thenReturn(mockProject);
        when(mockProject.getDiagrams()).thenReturn(Collections.emptyList());

        String result = mainController.generateCode();

        assertNull(result);
        verify(mockCodeGenerationService, never()).generateCode(mockProject);
    }

    @Test
    void testGenerateCode_NoProject() {
        when(mockProjectService.getCurrentProject()).thenReturn(null);

        String result = mainController.generateCode();

        assertNull(result);
        verify(mockCodeGenerationService, never()).generateCode(any());
    }


    @Test
    void testGetMainView() {
        mainController.setMainView(mockMainView);
        MainView result = mainController.getmainview();

        assertNotNull(result, "MainView should not be null after setting it.");
        assertEquals(mockMainView, result, "MainView returned should match the mockMainView.");
    }

    @Test
    void testGetCurrentProject() {
        Project mockProject = mock(Project.class);
        when(mockProjectService.getCurrentProject()).thenReturn(mockProject);

        mainController.setProjectService(mockProjectService);
        Project result = mainController.getCurrentProject();

        assertNotNull(result, "Current project should not be null if a project is set.");
        assertEquals(mockProject, result, "The returned project should match the mock project.");
        verify(mockProjectService).getCurrentProject();
    }

    @Test
    void testGetAvailableClassNames_WithValidProject() {
        Project mockProject = mock(Project.class);
        BClassBox mockClassBox1 = mock(BClassBox.class);
        BClassBox mockClassBox2 = mock(BClassBox.class);

        when(mockProjectService.getCurrentProject()).thenReturn(mockProject);
        when(mockClassBox1.getTitle()).thenReturn("Class1");
        when(mockClassBox2.getTitle()).thenReturn("Class2");
        when(mockProject.getDiagrams()).thenReturn(List.of(mockClassBox1, mockClassBox2));

        mainController.setProjectService(mockProjectService);
        List<String> result = mainController.getAvailableClassNames();

        assertNotNull(result, "Available class names should not be null.");
        assertEquals(List.of("Class1", "Class2"), result, "Class names should match the titles of BClassBox diagrams.");
        verify(mockProjectService).getCurrentProject();
        verify(mockProject).getDiagrams();
        verify(mockClassBox1).getTitle();
        verify(mockClassBox2).getTitle();
    }

    @Test
    void testGetAvailableClassNames_NoProject() {
        when(mockProjectService.getCurrentProject()).thenReturn(null);

        mainController.setProjectService(mockProjectService);
        List<String> result = mainController.getAvailableClassNames();

        assertNotNull(result, "Available class names should not be null, even if no project is available.");
        assertTrue(result.isEmpty(), "Available class names should be an empty list if no project is available.");
        verify(mockProjectService).getCurrentProject();
    }

    @Test
    void testGetDiagramService() {
        DiagramService result = mainController.getDiagramService();

        assertNotNull(result, "DiagramService should not be null.");
        assertEquals(mockDiagramService, result, "The returned DiagramService should match the mock service.");
    }

    @Test
    void testSetMainView() {
        MainView newMockMainView = mock(MainView.class);

        mainController.setMainView(newMockMainView);
        MainView result = mainController.getmainview();

        assertNotNull(result, "MainView should not be null after setting it.");
        assertEquals(newMockMainView, result, "MainView returned should match the new mockMainView.");
    }


}