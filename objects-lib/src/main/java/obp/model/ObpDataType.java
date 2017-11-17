package obp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This fulfills 2 purposes
 * Assists ObpPropertyDeclaration with validation of ObpProperty
 * Assists with creation of ObpDataValue
 */
public enum ObpDataType {
    LOCATION("locationType", "Location", "##.######,##.######") {
        public ObpDataValue newObpDataValue(String anInput) {
            return new ObpLocationValue(anInput);
        }

        public ObpDataValue defaultObpDataValue() {
            return new ObpLocationValue();
        }
    },
    FREE_TEXT("freeTextType", "String", "") {
        public ObpDataValue newObpDataValue(String anInput) {
            return new ObpStringValue(anInput);
        }

        public ObpDataValue defaultObpDataValue() {
            return new ObpStringValue();
        }
    },
    DATE("dateType", "Date", "MM/dd/yyyy") {
        public ObpDataValue newObpDataValue(String anInput) {
            return new ObpDateValue(anInput);
        }

        public ObpDataValue defaultObpDataValue() {
            return new ObpDateValue();
        }
    };

    private String name;
    private String dataType;
    private String format;
    abstract public ObpDataValue newObpDataValue(String anInput);

    ObpDataType(String aName, String aJavaTypeName, String aFormat) {
        this.name = aName;
        this.dataType = aJavaTypeName;
        this.format = aFormat;
    }

    public boolean isValid() {
        return true;
    }

    public static ObpDataType getForName(String aName) {
        for (ObpDataType current : values()) {
            if (current.getName().equals(aName)) {
                return current;
            }
        }
        return null;
    }

    public static List<ObpDataType> getValues() {
        final List<ObpDataType> valuesList = new ArrayList<ObpDataType>();
        for (ObpDataType current : values()) {
            valuesList.add(current);
        }
        return valuesList;
    }

    abstract public ObpDataValue defaultObpDataValue();
    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public String getFormat() {
        return format;
    }

    public String toString() {
        return super.toString() + " name: " + getName() + " dataType: " + getDataType() + " format: " + getFormat();
    }
}
