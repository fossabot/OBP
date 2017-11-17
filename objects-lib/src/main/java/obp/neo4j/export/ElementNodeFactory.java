package obp.neo4j.export;

import obp.object.OBPBaseClass;

public class ElementNodeFactory {
    private static ElementNodeFactory instance = new ElementNodeFactory();
    public static ElementNodeFactory getInstance() {
        return instance;
    }
    public ElementNode createElementNode(OBPBaseClass obpBaseClass) {
        return null;//new ElementNode(obpBaseClass);
    }
}
