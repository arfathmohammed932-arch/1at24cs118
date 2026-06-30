package repository;

import exception.ComplaintException;
import model.Complaint;
import java.util.List;

public interface ComplaintRepository {
    void save(Complaint complaint) throws ComplaintException;
    void update(Complaint complaint) throws ComplaintException;
    Complaint findById(int id) throws ComplaintException;
    List<Complaint> findAll() throws ComplaintException;
    List<Complaint> searchAndFilter(String query, String category, String priority, String status) throws ComplaintException;
}
