package obp.model;

import lombok.Data;

@Data
public class ObpSlimEntityDeclaration {
    private String name;

    public ObpSlimEntityDeclaration(ObpEntityDeclaration anEntityDeclaration) {
        name = anEntityDeclaration.getName();
    }
}
