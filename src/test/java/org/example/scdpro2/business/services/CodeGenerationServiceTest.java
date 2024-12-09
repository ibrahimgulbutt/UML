package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.AttributeComponent;
import org.example.scdpro2.business.models.BClassDiagarm.OperationComponent;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CodeGenerationServiceTest {

    @Mock
    private Project mockProject;

    @Mock
    private BClassBox mockBClassBox;

    @Mock
    private AttributeComponent mockAttribute;

    @Mock
    private OperationComponent mockOperation;

    @Mock
    private Relationship mockRelationship;

    private CodeGenerationService codeGenerationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        codeGenerationService = new CodeGenerationService();
    }

    // Test case for generating code for a project
    @Test
    void testGenerateCode() {
        List<Diagram> diagrams = new ArrayList<>();
        diagrams.add(mockBClassBox);

        when(mockProject.getDiagrams()).thenReturn(diagrams);
        when(mockBClassBox.getTitle()).thenReturn("MyClass");

        String code = codeGenerationService.generateCode(mockProject);

        assertTrue(code.contains("public class MyClass"));
    }

    // Test case for generating class code with no relationships
    @Test
    void testGenerateClassCode_NoRelationships() {
        List<Relationship> relationships = new ArrayList<>();
        when(mockBClassBox.getRelationships()).thenReturn(relationships);
        when(mockBClassBox.getTitle()).thenReturn("MyClass");

        List<AttributeComponent> attributes = new ArrayList<>();
        when(mockBClassBox.getAttributes()).thenReturn(attributes);

        List<OperationComponent> operations = new ArrayList<>();
        when(mockBClassBox.getOperations()).thenReturn(operations);

        String classCode = codeGenerationService.generateClassCode(mockBClassBox);

        assertTrue(classCode.contains("public class MyClass"));
        assertTrue(classCode.contains("{"));
        assertTrue(classCode.contains("}"));
    }

    // Test case for generating class code with inheritance
    @Test
    void testGenerateClassCode_WithInheritance() {
        List<Relationship> relationships = new ArrayList<>();
        Relationship inheritanceRel = mock(Relationship.class);
        BClassBox targetClass = mock(BClassBox.class);
        when(targetClass.getTitle()).thenReturn("ParentClass");
        when(inheritanceRel.getType()).thenReturn(RelationshipLine.RelationshipType.INHERITANCE);
        when(inheritanceRel.getTarget()).thenReturn(targetClass);
        relationships.add(inheritanceRel);

        when(mockBClassBox.getRelationships()).thenReturn(relationships);
        when(mockBClassBox.getTitle()).thenReturn("MyClass");

        List<AttributeComponent> attributes = new ArrayList<>();
        when(mockBClassBox.getAttributes()).thenReturn(attributes);

        List<OperationComponent> operations = new ArrayList<>();
        when(mockBClassBox.getOperations()).thenReturn(operations);

        String classCode = codeGenerationService.generateClassCode(mockBClassBox);

        assertTrue(classCode.contains("public class MyClass extends ParentClass"));
    }

    // Test case for generating attribute code with public visibility
    @Test
    void testGenerateAttributeCode_PublicVisibility() {
        when(mockAttribute.getVisibility()).thenReturn("+");
        when(mockAttribute.getDataType()).thenReturn("String");
        when(mockAttribute.getName()).thenReturn("name");

        String attributeCode = codeGenerationService.generateAttributeCode(mockAttribute);

        assertEquals("public String name;", attributeCode);
    }

    // Test case for generating attribute code with private visibility
    @Test
    void testGenerateAttributeCode_PrivateVisibility() {
        when(mockAttribute.getVisibility()).thenReturn("-");
        when(mockAttribute.getDataType()).thenReturn("int");
        when(mockAttribute.getName()).thenReturn("age");

        String attributeCode = codeGenerationService.generateAttributeCode(mockAttribute);

        assertEquals("private int age;", attributeCode);
    }

    // Test case for generating attribute code with protected visibility
    @Test
    void testGenerateAttributeCode_ProtectedVisibility() {
        when(mockAttribute.getVisibility()).thenReturn("#");
        when(mockAttribute.getDataType()).thenReturn("double");
        when(mockAttribute.getName()).thenReturn("salary");

        String attributeCode = codeGenerationService.generateAttributeCode(mockAttribute);

        assertEquals("protected double salary;", attributeCode);
    }

    // Test case for generating operation code with public visibility
    @Test
    void testGenerateOperationCode_PublicVisibility() {
        when(mockOperation.getVisibility()).thenReturn("+");
        when(mockOperation.getReturnType()).thenReturn("void");
        when(mockOperation.getName()).thenReturn("setName");

        String operationCode = codeGenerationService.generateOperationCode(mockOperation);

        assertEquals("public void setName { }", operationCode);
    }

    // Test case for generating operation code with private visibility
    @Test
    void testGenerateOperationCode_PrivateVisibility() {
        when(mockOperation.getVisibility()).thenReturn("-");
        when(mockOperation.getReturnType()).thenReturn("int");
        when(mockOperation.getName()).thenReturn("getAge");

        String operationCode = codeGenerationService.generateOperationCode(mockOperation);

        assertEquals("private int getAge { }", operationCode);
    }

    // Test case for generating operation code with protected visibility
    @Test
    void testGenerateOperationCode_ProtectedVisibility() {
        when(mockOperation.getVisibility()).thenReturn("#");
        when(mockOperation.getReturnType()).thenReturn("String");
        when(mockOperation.getName()).thenReturn("getName");

        String operationCode = codeGenerationService.generateOperationCode(mockOperation);

        assertEquals("protected String getName { }", operationCode);
    }
}
