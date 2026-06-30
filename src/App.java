import exception.ComplaintException;
import model.Complaint;
import model.ComplaintCategory;
import model.ComplaintPriority;
import model.ComplaintStatus;
import repository.ComplaintRepository;
import repository.JdbcComplaintRepository;
import service.ComplaintService;
import ui.MainFrame;

import javax.swing.*;
import java.sql.Timestamp;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Set system Look & Feel to make the app look native/clean before our customizations
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Database Repository & Service
                ComplaintRepository repository = new JdbcComplaintRepository();
                ComplaintService service = new ComplaintService(repository);

                // Add Mock Data if database is completely empty
                populateMockDataIfEmpty(service);

                // Show Main UI Frame
                MainFrame frame = new MainFrame(service);
                frame.setVisible(true);

            } catch (ComplaintException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Application Initialization Error:\n" + e.getMessage() +
                    "\n\nMake sure the SQLite JDBC driver JAR is in your classpath.", 
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    private static void populateMockDataIfEmpty(ComplaintService service) throws ComplaintException {
        List<Complaint> list = service.getAllComplaints();
        if (list.isEmpty()) {
            System.out.println("Database is empty. Populating mock complaints for demonstration...");

            // Complaint 1 - Open Technical (no update needed)
            Complaint c1 = new Complaint(
                "Fiber Broadband Disconnects Frequently",
                "The broadband connection drops every 20-30 minutes, especially during peak evening hours (8 PM - 10 PM).",
                ComplaintCategory.TECHNICAL,
                ComplaintPriority.HIGH,
                "john.doe@gmail.com",
                "+91 9876543210"
            );
            service.registerComplaint(c1);

            // Complaint 2 - Billing, save first then update to IN_PROGRESS
            Complaint c2 = new Complaint(
                "Double Charged for June Subscription Plan",
                "My credit card was charged twice ($49.99 x 2) for the billing cycle starting June 15.",
                ComplaintCategory.BILLING,
                ComplaintPriority.MEDIUM,
                "alice.smith@outlook.com",
                "+91 8765432109"
            );
            service.registerComplaint(c2);
            c2.setStatus(ComplaintStatus.IN_PROGRESS);
            c2.setResolutionNotes("Finance team contacted payment gateway to check duplicate transaction log. Awaiting response.");
            service.updateComplaint(c2);

            // Complaint 3 - Service, save first then update to RESOLVED
            Complaint c3 = new Complaint(
                "Replacement Router Not Delivered Within 48 Hours",
                "I ordered a replacement router on Friday with guaranteed 48-hour delivery, but it hasn't arrived yet.",
                ComplaintCategory.SERVICE,
                ComplaintPriority.MEDIUM,
                "robert.johnson@yahoo.com",
                "+91 7654321098"
            );
            service.registerComplaint(c3);
            c3.setStatus(ComplaintStatus.RESOLVED);
            c3.setResolutionNotes("Dispatched via Priority Courier. Tracking ID: EXP-889912. Delivered on June 29. Customer confirmed setup successful.");
            service.updateComplaint(c3);

            // Complaint 4 - Urgent Technical (no update needed)
            Complaint c4 = new Complaint(
                "Production SQL Server Latency Spikes",
                "The backend database server is reporting extremely high latency (1200ms+) on write queries, causing timeouts.",
                ComplaintCategory.TECHNICAL,
                ComplaintPriority.URGENT,
                "admin.devops@enterprise.com",
                "+91 9988776655"
            );
            service.registerComplaint(c4);

            // Complaint 5 - Feedback, save first then update to CLOSED
            Complaint c5 = new Complaint(
                "Suggestions for Self-Service Portal Layout",
                "The self-service complaint form has too many fields. A multi-step wizard format would be better.",
                ComplaintCategory.FEEDBACK,
                ComplaintPriority.LOW,
                "samantha.k@gmail.com",
                "+91 6543210987"
            );
            service.registerComplaint(c5);
            c5.setStatus(ComplaintStatus.CLOSED);
            c5.setResolutionNotes("Feedback routed to product engineering team. Logged in UI/UX improvements epic.");
            service.updateComplaint(c5);

            System.out.println("Mock data generation completed successfully!");
        }
    }
}
