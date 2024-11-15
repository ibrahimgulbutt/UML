package org.example.scdpro2.business.services;

import org.example.scdpro2.business.models.ClassDiagram;
import org.example.scdpro2.business.models.Diagram;
import org.example.scdpro2.business.models.Relationship;

public class CodeGenerationService {
    public String generateCode(Diagram diagram) {
        if (diagram instanceof ClassDiagram classDiagram) {
            StringBuilder code = new StringBuilder("class " + classDiagram.getTitle() + " {\n");
            classDiagram.getAttributes().forEach(attr -> code.append("  ").append(attr.generateCode()).append("\n"));
            classDiagram.getOperations().forEach(op -> code.append("  ").append(op.generateCode()).append("\n"));
            code.append("}\n");

            // Add relationships
            for (Relationship relationship : classDiagram.getRelationships()) {
                code.append("// ").append(relationship.getType()).append(": ")
                        .append(relationship.getSource().getTitle()).append(" -> ")
                        .append(relationship.getTarget().getTitle()).append("\n");
            }

            return code.toString();
        }
        return "";
    }

}
