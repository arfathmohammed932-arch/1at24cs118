package model;

public enum ComplaintCategory {
    TECHNICAL("Technical Support"),
    BILLING("Billing & Finance"),
    SERVICE("Service Delivery"),
    FEEDBACK("General Feedback"),
    OTHER("Other Queries");

    private final String displayName;

    ComplaintCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ComplaintCategory fromString(String text) {
        for (ComplaintCategory category : ComplaintCategory.values()) {
            if (category.displayName.equalsIgnoreCase(text) || category.name().equalsIgnoreCase(text)) {
                return category;
            }
        }
        return OTHER;
    }
}
