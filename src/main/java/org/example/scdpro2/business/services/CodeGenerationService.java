package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.*;
import org.example.scdpro2.business.models.BClassDiagarm.AttributeComponent;
import org.example.scdpro2.business.models.BClassDiagarm.BClassBox;
import org.example.scdpro2.business.models.BClassDiagarm.OperationComponent;
import org.example.scdpro2.business.models.BClassDiagarm.Relationship;
import org.example.scdpro2.ui.views.ClassDiagram.RelationshipLine;

import java.util.ArrayList;
import java.util.List;
/**
 * The CodeGenerationService class provides functionality for generating Java source code
 * from class diagrams defined in a {@link Project}. The generated code includes class declarations,
 * attributes, operations, and relationships like inheritance.
 */
public class CodeGenerationService {

    /**
     * Generates Java source code for all {@link BClassBox} diagrams in the given {@link Project}.
     *
     * @param project the {@link Project} containing the class diagrams.
     * @return a string representation of the generated Java source code.
     */
    public String generateCode(Project project) {
        StringBuilder codeBuilder = new StringBuilder();

        // Iterate through all diagrams in the project
        for (Diagram diagram : project.getDiagrams()) {
            if (diagram instanceof BClassBox BClassBox) {
                codeBuilder.append(generateClassCode(BClassBox)).append("\n\n");
            }
        }

        return codeBuilder.toString();
    }
    /**
     * Generates the Java source code for a specific {@link BClassBox}.
     *
     * @param bClassBox the {@link BClassBox} for which code is generated.
     * @return a string representation of the Java class code.
     */
    private String generateClassCode(BClassBox bClassBox) {
        StringBuilder classCode = new StringBuilder();

        RelationshipLine.RelationshipType rel = RelationshipLine.RelationshipType.INHERITANCE;

        // Class declaration
        classCode.append("public class ").append(bClassBox.getTitle());

        // Check for inheritance relationships and apply extends clause
        List<String> inheritanceClasses = new ArrayList<>();
        for (Relationship relationship : bClassBox.getRelationships()) {
            if (relationship.getType() == rel ) {
                inheritanceClasses.add(relationship.getTarget().getTitle()); // Add the target class title for inheritance
            }
        }

        // If there are inheritance classes, append the 'extends' clause
        if (!inheritanceClasses.isEmpty()) {
            classCode.append(" extends ");
            classCode.append(String.join(" ,", inheritanceClasses)); // Multiple inheritance, if needed
        }

        classCode.append(" {\n");

        // Add attributes
        for (AttributeComponent attribute : bClassBox.getAttributes()) {
            classCode.append("    ").append(generateAttributeCode(attribute)).append("\n");
        }

        // Add operations
        for (OperationComponent operation : bClassBox.getOperations()) {
            classCode.append("    ").append(generateOperationCode(operation)).append("\n");
        }

        classCode.append("}");
        return classCode.toString();
    }

    /**
     * Generates the Java source code for an {@link AttributeComponent} in a class diagram.
     *
     * @param attribute the {@link AttributeComponent} representing a class attribute.
     * @return a string representation of the attribute declaration in Java.
     */
    private String generateAttributeCode(AttributeComponent attribute) {
        String visibility = switch (attribute.getVisibility()) {
            case "+" -> "public";
            case "-" -> "private";
            case "#" -> "protected";
            default -> "private";
        };

        return visibility + " "+attribute.getDataType()+" " + attribute.getName() + ";";
    }

    /**
     * Generates the Java source code for an {@link OperationComponent} in a class diagram.
     *
     * @param operation the {@link OperationComponent} representing a class operation (method).
     * @return a string representation of the operation declaration in Java.
     */
    private String generateOperationCode(OperationComponent operation) {
        String visibility = switch (operation.getVisibility()) {
            case "+" -> "public";
            case "-" -> "private";
            case "#" -> "protected";
            default -> "private";
        };

        return visibility + " "+operation.getReturnType()+" " + operation.getName() + " { }";
    }

}
