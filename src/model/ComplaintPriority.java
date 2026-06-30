package model;

public enum ComplaintPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent");

    private final String displayName;

    ComplaintPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ComplaintPriority fromString(String text) {
        for (ComplaintPriority priority : ComplaintPriority.values()) {
            if (priority.displayName.equalsIgnoreCase(text) || priority.name().equalsIgnoreCase(text)) {
                return priority;
            }
        }
        return MEDIUM;
    }
}
