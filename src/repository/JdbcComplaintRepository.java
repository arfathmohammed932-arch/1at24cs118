package repository;

import exception.ComplaintException;
import model.Complaint;
import model.ComplaintCategory;
import model.ComplaintPriority;
import model.ComplaintStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcComplaintRepository implements ComplaintRepository {
    private static final String DB_URL = "jdbc:sqlite:complaints.db";
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";

    public JdbcComplaintRepository() throws ComplaintException {
        try {
            // Load Driver explicitly for compatibility
            Class.forName(DRIVER_CLASS);
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            throw new ComplaintException("SQLite JDBC Driver not found. Please ensure sqlite-jdbc jar is in the classpath.", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() throws ComplaintException {
        String sql = "CREATE TABLE IF NOT EXISTS complaints (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "category TEXT NOT NULL, " +
                "priority TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "contact_email TEXT, " +
                "contact_phone TEXT, " +
                "created_at TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "resolution_notes TEXT" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new ComplaintException("Error initializing database tables", e);
        }
    }

    @Override
    public void save(Complaint complaint) throws ComplaintException {
        String sql = "INSERT INTO complaints (title, description, category, priority, status, contact_email, " +
                "contact_phone, created_at, updated_at, resolution_notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, complaint.getTitle());
            pstmt.setString(2, complaint.getDescription());
            pstmt.setString(3, complaint.getCategory().name());
            pstmt.setString(4, complaint.getPriority().name());
            pstmt.setString(5, complaint.getStatus().name());
            pstmt.setString(6, complaint.getContactEmail());
            pstmt.setString(7, complaint.getContactPhone());
            pstmt.setTimestamp(8, complaint.getCreatedAt());
            pstmt.setTimestamp(9, complaint.getUpdatedAt());
            pstmt.setString(10, complaint.getResolutionNotes());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating complaint failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    complaint.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating complaint failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new ComplaintException("Error saving complaint: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Complaint complaint) throws ComplaintException {
        String sql = "UPDATE complaints SET title = ?, description = ?, category = ?, priority = ?, status = ?, " +
                "contact_email = ?, contact_phone = ?, updated_at = ?, resolution_notes = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, complaint.getTitle());
            pstmt.setString(2, complaint.getDescription());
            pstmt.setString(3, complaint.getCategory().name());
            pstmt.setString(4, complaint.getPriority().name());
            pstmt.setString(5, complaint.getStatus().name());
            pstmt.setString(6, complaint.getContactEmail());
            pstmt.setString(7, complaint.getContactPhone());
            pstmt.setTimestamp(8, complaint.getUpdatedAt());
            pstmt.setString(9, complaint.getResolutionNotes());
            pstmt.setInt(10, complaint.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new ComplaintException("Updating complaint failed, no complaint found with ID: " + complaint.getId());
            }
        } catch (SQLException e) {
            throw new ComplaintException("Error updating complaint: " + e.getMessage(), e);
        }
    }

    @Override
    public Complaint findById(int id) throws ComplaintException {
        String sql = "SELECT * FROM complaints WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToComplaint(rs);
                }
            }
        } catch (SQLException e) {
            throw new ComplaintException("Error finding complaint by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Complaint> findAll() throws ComplaintException {
        String sql = "SELECT * FROM complaints ORDER BY created_at DESC";
        List<Complaint> list = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapRowToComplaint(rs));
            }
        } catch (SQLException e) {
            throw new ComplaintException("Error retrieving all complaints: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Complaint> searchAndFilter(String query, String category, String priority, String status) throws ComplaintException {
        StringBuilder sql = new StringBuilder("SELECT * FROM complaints WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (id = ? OR title LIKE ? OR description LIKE ? OR contact_email LIKE ?)");
            int searchId = -1;
            try {
                searchId = Integer.parseInt(query.trim());
            } catch (NumberFormatException ignored) {}
            
            params.add(searchId);
            params.add("%" + query + "%");
            params.add("%" + query + "%");
            params.add("%" + query + "%");
        }

        if (category != null && !category.equalsIgnoreCase("ALL")) {
            sql.append(" AND category = ?");
            params.add(category);
        }

        if (priority != null && !priority.equalsIgnoreCase("ALL")) {
            sql.append(" AND priority = ?");
            params.add(priority);
        }

        if (status != null && !status.equalsIgnoreCase("ALL")) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        sql.append(" ORDER BY created_at DESC");

        List<Complaint> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            throw new ComplaintException("Error searching complaints: " + e.getMessage(), e);
        }
        return list;
    }

    private Complaint mapRowToComplaint(ResultSet rs) throws SQLException {
        Complaint complaint = new Complaint();
        complaint.setId(rs.getInt("id"));
        complaint.setTitle(rs.getString("title"));
        complaint.setDescription(rs.getString("description"));
        complaint.setCategory(ComplaintCategory.valueOf(rs.getString("category")));
        complaint.setPriority(ComplaintPriority.valueOf(rs.getString("priority")));
        complaint.setStatus(ComplaintStatus.valueOf(rs.getString("status")));
        complaint.setContactEmail(rs.getString("contact_email"));
        complaint.setContactPhone(rs.getString("contact_phone"));
        complaint.setCreatedAt(rs.getTimestamp("created_at"));
        complaint.setUpdatedAt(rs.getTimestamp("updated_at"));
        complaint.setResolutionNotes(rs.getString("resolution_notes"));
        return complaint;
    }
}
