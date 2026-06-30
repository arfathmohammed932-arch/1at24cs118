package model;

import java.sql.Timestamp;

public class Complaint {
    private int id;
    private String title;
    private String description;
    private ComplaintCategory category;
    private ComplaintPriority priority;
    private ComplaintStatus status;
    private String contactEmail;
    private String contactPhone;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String resolutionNotes;

    // Default Constructor
    public Complaint() {
        this.status = ComplaintStatus.OPEN;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.resolutionNotes = "";
    }

    // Parametrized Constructor
    public Complaint(String title, String description, ComplaintCategory category, 
                     ComplaintPriority priority, String contactEmail, String contactPhone) {
        this();
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public void setPriority(ComplaintPriority priority) {
        this.priority = priority;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }
}
