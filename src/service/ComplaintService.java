package service;

import exception.ComplaintException;
import model.Complaint;
import repository.ComplaintRepository;
import java.util.List;

public class ComplaintService {
    private final ComplaintRepository repository;

    public ComplaintService(ComplaintRepository repository) {
        this.repository = repository;
    }

    public void registerComplaint(Complaint complaint) throws ComplaintException {
        validateComplaint(complaint);
        repository.save(complaint);
    }

    public void updateComplaint(Complaint complaint) throws ComplaintException {
        validateComplaint(complaint);
        repository.update(complaint);
    }

    public Complaint getComplaintById(int id) throws ComplaintException {
        Complaint complaint = repository.findById(id);
        if (complaint == null) {
            throw new ComplaintException("Complaint not found with ID: " + id);
        }
        return complaint;
    }

    public List<Complaint> getAllComplaints() throws ComplaintException {
        return repository.findAll();
    }

    public List<Complaint> searchComplaints(String query, String category, String priority, String status) throws ComplaintException {
        return repository.searchAndFilter(query, category, priority, status);
    }

    private void validateComplaint(Complaint complaint) throws ComplaintException {
        if (complaint == null) {
            throw new ComplaintException("Complaint details cannot be null.");
        }
        if (complaint.getTitle() == null || complaint.getTitle().trim().isEmpty()) {
            throw new ComplaintException("Complaint title is required.");
        }
        if (complaint.getTitle().length() > 100) {
            throw new ComplaintException("Complaint title cannot exceed 100 characters.");
        }
        if (complaint.getDescription() == null || complaint.getDescription().trim().isEmpty()) {
            throw new ComplaintException("Complaint description is required.");
        }
        if (complaint.getCategory() == null) {
            throw new ComplaintException("Complaint category is required.");
        }
        if (complaint.getPriority() == null) {
            throw new ComplaintException("Complaint priority is required.");
        }
        if (complaint.getStatus() == null) {
            throw new ComplaintException("Complaint status is required.");
        }
        
        // Basic Email validation
        if (complaint.getContactEmail() != null && !complaint.getContactEmail().trim().isEmpty()) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!complaint.getContactEmail().matches(emailRegex)) {
                throw new ComplaintException("Invalid contact email format.");
            }
        }
        
        // Phone validation (numeric check)
        if (complaint.getContactPhone() != null && !complaint.getContactPhone().trim().isEmpty()) {
            String phoneRegex = "^[0-9+\\-\\s()]{7,15}$";
            if (!complaint.getContactPhone().matches(phoneRegex)) {
                throw new ComplaintException("Invalid phone number format.");
            }
        }
    }
}
