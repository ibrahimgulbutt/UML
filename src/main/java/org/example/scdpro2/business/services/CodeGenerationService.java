package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.AttributeComponent;
import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.OperationComponent;
import org.example.scdpro2.business.models.Project;

import java.util.List;

public class CodeGenerationService {

    public String generateCode(Project project) {
        StringBuilder codeBuilder = new StringBuilder();

        // Iterate through all diagrams in the project
        for (Diagram diagram : project.getDiagrams()) {
            if (diagram instanceof ClassDiagram classDiagram) {
                codeBuilder.append(generateClassCode(classDiagram)).append("\n\n");
            }
        }

        return codeBuilder.toString();
    }

    private String generateClassCode(ClassDiagram classDiagram) {
        StringBuilder classCode = new StringBuilder();

        // Class declaration
        classCode.append("public class ").append(classDiagram.getTitle()).append(" {\n");

        // Add attributes
        for (AttributeComponent attribute : classDiagram.getAttributes()) {
            classCode.append("    ").append(generateAttributeCode(attribute)).append("\n");
        }

        // Add operations
        for (OperationComponent operation : classDiagram.getOperations()) {
            classCode.append("    ").append(generateOperationCode(operation)).append("\n");
        }

        classCode.append("}");
        return classCode.toString();
    }

    private String generateAttributeCode(AttributeComponent attribute) {
        String visibility = switch (attribute.getVisibility()) {
            case "+" -> "public";
            case "-" -> "private";
            case "#" -> "protected";
            default -> "private";
        };

        return visibility + " " + attribute.getName() + ";";
    }

    private String generateOperationCode(OperationComponent operation) {
        String visibility = switch (operation.getVisibility()) {
            case "+" -> "public";
            case "-" -> "private";
            case "#" -> "protected";
            default -> "private";
        };

        return visibility + " " + operation.getName() + " { }";
    }

}
