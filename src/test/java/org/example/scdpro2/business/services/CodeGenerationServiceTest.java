package org.example.scdpro2.business.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.*;
import org.example.scdpro2.business.services.CodeGenerationService;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeGenerationServiceTest {

    private CodeGenerationService codeGenerationService;
    private Project project;
    private BClassBox bClassBox;

    @BeforeEach
    void setUp() {
        codeGenerationService = new CodeGenerationService();

        // Initialize a sample project
        project = new Project("TestProject");

        // Create a BClassBox with attributes, operations, and relationships
        bClassBox = new BClassBox("TestClass");

        // Add attributes
        List<AttributeComponent> attributes = new ArrayList<>();
        attributes.add(new AttributeComponent("attribute1", "+", "int"));
        attributes.add(new AttributeComponent("attribute2", "-", "String"));
        bClassBox.setAttributes(attributes);

        // Add operations
        List<OperationComponent> operations = new ArrayList<>();
        operations.add(new OperationComponent("operation1", "+", "void"));
        operations.add(new OperationComponent("operation2", "-", "int"));
        bClassBox.setOperations(operations);

        // Add relationships
        List<Relationship> relationships = new ArrayList<>();
        BClassBox parentClass = new BClassBox("ParentClass");
        relationships.add(new Relationship(parentClass, bClassBox, RelationshipLine.RelationshipType.INHERITANCE, "1", "1", "inherits"));
        bClassBox.setRelationships(relationships);

        // Add the class diagram to the project
        List<Diagram> diagrams = new ArrayList<>();
        diagrams.add(bClassBox);
        project.setDiagrams(diagrams);
    }

    @Test
    void testGenerateCode() {
        // Generate code for the project
        String generatedCode = codeGenerationService.generateCode(project);

        // Expected code output
        String expectedCode = """
            public class TestClass extends ParentClass {
                public int attribute1;
                private String attribute2;
                public void operation1 { }
                private int operation2 { }
            }
            """;

        // Assert that the generated code matches the expected output
        assertEquals(expectedCode.trim(), generatedCode.trim());
    }

    @Test
    void testGenerateClassCodeWithNoInheritance() {
        // Remove relationships to test a class without inheritance
        bClassBox.setRelationships(new ArrayList<>());

        // Generate code for the project
        String generatedCode = codeGenerationService.generateCode(project);

        // Expected code output
        String expectedCode = """
            public class TestClass {
                public int attribute1;
                private String attribute2;
                public void operation1 { }
                private int operation2 { }
            }
            """;

        // Assert that the generated code matches the expected output
        assertEquals(expectedCode.trim(), generatedCode.trim());
    }

    @Test
    void testGenerateCodeWithEmptyClass() {
        // Create an empty BClassBox
        BClassBox emptyClassBox = new BClassBox("EmptyClass");
        project.setDiagrams(List.of(emptyClassBox));

        // Generate code for the project
        String generatedCode = codeGenerationService.generateCode(project);

        // Expected code output
        String expectedCode = """
            public class EmptyClass {
            }
            """;

        // Assert that the generated code matches the expected output
        assertEquals(expectedCode.trim(), generatedCode.trim());
    }

    @Test
    void testGenerateAttributeCode() {
        AttributeComponent attribute = new AttributeComponent("testAttribute", "+", "boolean");

        // Generate attribute code
        String generatedCode = codeGenerationService.generateAttributeCode(attribute);

        // Expected code output
        String expectedCode = "public boolean testAttribute;";

        // Assert that the generated code matches the expected output
        assertEquals(expectedCode, generatedCode);
    }

    @Test
    void testGenerateOperationCode() {
        OperationComponent operation = new OperationComponent("testOperation", "#", "double");

        // Generate operation code
        String generatedCode = codeGenerationService.generateOperationCode(operation);

        // Expected code output
        String expectedCode = "protected double testOperation { }";

        // Assert that the generated code matches the expected output
        assertEquals(expectedCode, generatedCode);
    }
}


