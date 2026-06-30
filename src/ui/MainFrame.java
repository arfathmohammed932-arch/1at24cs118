package ui;

import exception.ComplaintException;
import model.Complaint;
import model.ComplaintCategory;
import model.ComplaintPriority;
import model.ComplaintStatus;
import service.ComplaintService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainFrame extends JFrame {
    private final ComplaintService service;
    
    // UI Theme Colors
    private static final Color COLOR_BG = new Color(18, 18, 24);         // Deep dark background
    private static final Color COLOR_SIDEBAR = new Color(25, 25, 35);    // Slightly lighter sidebar
    private static final Color COLOR_CARD = new Color(28, 28, 40);       // Card background
    private static final Color COLOR_ACCENT = new Color(124, 77, 255);    // Modern Purple/Violet
    private static final Color COLOR_ACCENT_HOVER = new Color(145, 105, 255);
    private static final Color COLOR_TEXT_PRIMARY = new Color(240, 240, 245);
    private static final Color COLOR_TEXT_SECONDARY = new Color(160, 160, 180);
    private static final Color COLOR_BORDER = new Color(45, 45, 60);

    // Status Colors for badges
    private static final Color COLOR_STATUS_OPEN = new Color(255, 143, 0);       // Orange
    private static final Color COLOR_STATUS_PROGRESS = new Color(33, 150, 243);  // Blue
    private static final Color COLOR_STATUS_RESOLVED = new Color(76, 175, 80);   // Green
    private static final Color COLOR_STATUS_CLOSED = new Color(158, 158, 158);   // Grey

    // Fonts
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    // Cards and Layout
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Dashboard Components
    private JLabel lblTotalCount;
    private JLabel lblOpenCount;
    private JLabel lblProgressCount;
    private JLabel lblResolvedCount;
    private JPanel pnlRecentList;

    // View Panel JTable
    private JTable tblComplaints;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbFilterCategory;
    private JComboBox<String> cbFilterPriority;
    private JComboBox<String> cbFilterStatus;

    // Navigation buttons
    private SidebarButton btnNavDashboard;
    private SidebarButton btnNavRegister;
    private SidebarButton btnNavDirectory;

    public MainFrame(ComplaintService service) {
        this.service = service;
        
        setTitle("Complaint Management System - Staff Portal");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        // Main Layout
        setLayout(new BorderLayout());

        // Sidebar Panel
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_BG);

        // Add views
        contentPanel.add(createDashboardPanel(), "DASHBOARD");
        contentPanel.add(createRegisterPanel(), "REGISTER");
        contentPanel.add(createDirectoryPanel(), "DIRECTORY");

        add(contentPanel, BorderLayout.CENTER);

        // Show Dashboard first
        showCard("DASHBOARD");
        refreshAllData();
    }

    private void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
        
        // Reset active sidebar buttons
        btnNavDashboard.setActive(cardName.equals("DASHBOARD"));
        btnNavRegister.setActive(cardName.equals("REGISTER"));
        btnNavDirectory.setActive(cardName.equals("DIRECTORY"));
    }

    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBackground(COLOR_SIDEBAR);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDER));

        // Sidebar Header (Logo/App Title)
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 0));
        headerPanel.setBackground(COLOR_SIDEBAR);
        headerPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("RESOLVE IT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Complaint Portal v1.0");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(COLOR_ACCENT);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Navigation Panel
        JPanel navPanel = new JPanel();
        navPanel.setBackground(COLOR_SIDEBAR);
        navPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        btnNavDashboard = new SidebarButton("Dashboard", "DASHBOARD");
        btnNavRegister = new SidebarButton("Register Complaint", "REGISTER");
        btnNavDirectory = new SidebarButton("Complaint Directory", "DIRECTORY");

        navPanel.add(btnNavDashboard);
        navPanel.add(btnNavRegister);
        navPanel.add(btnNavDirectory);

        panel.add(navPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 20, 15));
        footerPanel.setBackground(COLOR_SIDEBAR);
        JLabel lblFooter = new JLabel("© 2026 OOP Hackathon Team");
        lblFooter.setFont(FONT_SMALL);
        lblFooter.setForeground(COLOR_TEXT_SECONDARY);
        footerPanel.add(lblFooter);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        headerPanel.setBackground(COLOR_BG);
        JLabel title = new JLabel("System Overview");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT_PRIMARY);
        headerPanel.add(title);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Dashboard Center (Stats Grid & Recent Complaints)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 0, 10, 0);

        // Cards Row (Grid of 4 Metric Cards)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(COLOR_BG);

        lblTotalCount = new JLabel("0", SwingConstants.CENTER);
        lblOpenCount = new JLabel("0", SwingConstants.CENTER);
        lblProgressCount = new JLabel("0", SwingConstants.CENTER);
        lblResolvedCount = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("Total Cases", lblTotalCount, COLOR_ACCENT));
        statsPanel.add(createStatCard("Open Complaints", lblOpenCount, COLOR_STATUS_OPEN));
        statsPanel.add(createStatCard("In Progress", lblProgressCount, COLOR_STATUS_PROGRESS));
        statsPanel.add(createStatCard("Resolved Cases", lblResolvedCount, COLOR_STATUS_RESOLVED));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.25;
        centerPanel.add(statsPanel, gbc);

        // Recent Complaints Panel
        JPanel recentPanel = new RoundedPanel(15);
        recentPanel.setLayout(new BorderLayout());
        recentPanel.setBackground(COLOR_CARD);
        recentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel recentTitle = new JLabel("Recent Complaints");
        recentTitle.setFont(FONT_HEADER);
        recentTitle.setForeground(COLOR_TEXT_PRIMARY);
        recentPanel.add(recentTitle, BorderLayout.NORTH);

        pnlRecentList = new JPanel();
        pnlRecentList.setBackground(COLOR_CARD);
        pnlRecentList.setLayout(new BoxLayout(pnlRecentList, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(pnlRecentList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_CARD);
        recentPanel.add(scrollPane, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.75;
        centerPanel.add(recentPanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createStatCard(String title, JLabel countLabel, Color accentColor) {
        JPanel card = new RoundedPanel(15);
        card.setLayout(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Colored top border accent bar
        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(0, 4));
        accentBar.setBackground(accentColor);
        card.add(accentBar, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 5));
        content.setBackground(COLOR_CARD);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_BODY);
        lblTitle.setForeground(COLOR_TEXT_SECONDARY);

        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        countLabel.setForeground(COLOR_TEXT_PRIMARY);

        content.add(lblTitle);
        content.add(countLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRegisterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        headerPanel.setBackground(COLOR_BG);
        JLabel title = new JLabel("Register New Complaint");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT_PRIMARY);
        headerPanel.add(title);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Card Panel
        JPanel formCard = new RoundedPanel(15);
        formCard.setBackground(COLOR_CARD);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        JTextField txtTitle = createStyledTextField();
        JComboBox<ComplaintCategory> cbCategory = new JComboBox<>(ComplaintCategory.values());
        JComboBox<ComplaintPriority> cbPriority = new JComboBox<>(ComplaintPriority.values());
        JTextArea txtDescription = createStyledTextArea();
        JTextField txtEmail = createStyledTextField();
        JTextField txtPhone = createStyledTextField();

        styleComboBox(cbCategory);
        styleComboBox(cbPriority);

        // Row 1: Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        formCard.add(createFormLabel("Complaint Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.9; gbc.gridwidth = 3;
        formCard.add(txtTitle, gbc);

        // Row 2: Category and Priority
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        formCard.add(createFormLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.4;
        formCard.add(cbCategory, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.1;
        formCard.add(createFormLabel("Priority:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.4;
        formCard.add(cbPriority, gbc);

        // Row 3: Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        formCard.add(createFormLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.9; gbc.gridwidth = 3;
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBorder(new LineBorder(COLOR_BORDER, 1));
        descScroll.setPreferredSize(new Dimension(0, 120));
        formCard.add(descScroll, gbc);

        // Row 4: Email and Phone
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1;
        formCard.add(createFormLabel("Contact Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.4;
        formCard.add(txtEmail, gbc);

        gbc.gridx = 2; gbc.weightx = 0.1;
        formCard.add(createFormLabel("Contact Phone:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.4;
        formCard.add(txtPhone, gbc);

        // Row 5: Action Button
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JButton btnSubmit = new JButton("Submit Complaint");
        styleActionButton(btnSubmit);
        btnSubmit.addActionListener(e -> {
            try {
                Complaint comp = new Complaint(
                    txtTitle.getText().trim(),
                    txtDescription.getText().trim(),
                    (ComplaintCategory) cbCategory.getSelectedItem(),
                    (ComplaintPriority) cbPriority.getSelectedItem(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim()
                );
                
                service.registerComplaint(comp);
                
                JOptionPane.showMessageDialog(this, 
                    "Complaint registered successfully!\nGenerated Case ID: CMP-" + comp.getId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear fields
                txtTitle.setText("");
                txtDescription.setText("");
                txtEmail.setText("");
                txtPhone.setText("");
                cbCategory.setSelectedIndex(0);
                cbPriority.setSelectedIndex(1); // default medium
                
                refreshAllData();
                showCard("DASHBOARD");
            } catch (ComplaintException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formCard.add(btnSubmit, gbc);

        // Add some wrapping spacing
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(COLOR_BG);
        container.setBorder(new EmptyBorder(20, 0, 0, 0));
        container.add(formCard, BorderLayout.CENTER);

        mainPanel.add(container, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createDirectoryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        headerPanel.setBackground(COLOR_BG);
        JLabel title = new JLabel("Complaint Directory");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT_PRIMARY);
        headerPanel.add(title);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Central Area Container
        JPanel center = new JPanel(new BorderLayout(0, 15));
        center.setBackground(COLOR_BG);
        center.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Filter / Search Bar Panel
        JPanel filterPanel = new RoundedPanel(12);
        filterPanel.setBackground(COLOR_CARD);
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 15, 10));

        // Search Input
        txtSearch = createStyledTextField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.addActionListener(e -> performSearch());

        JButton btnSearch = new JButton("Search");
        styleSecondaryButton(btnSearch);
        btnSearch.addActionListener(e -> performSearch());

        // Category Filter
        cbFilterCategory = new JComboBox<>();
        cbFilterCategory.addItem("All Categories");
        for (ComplaintCategory cat : ComplaintCategory.values()) {
            cbFilterCategory.addItem(cat.name());
        }
        styleComboBox(cbFilterCategory);
        cbFilterCategory.addActionListener(e -> performSearch());

        // Priority Filter
        cbFilterPriority = new JComboBox<>();
        cbFilterPriority.addItem("All Priorities");
        for (ComplaintPriority p : ComplaintPriority.values()) {
            cbFilterPriority.addItem(p.name());
        }
        styleComboBox(cbFilterPriority);
        cbFilterPriority.addActionListener(e -> performSearch());

        // Status Filter
        cbFilterStatus = new JComboBox<>();
        cbFilterStatus.addItem("All Statuses");
        for (ComplaintStatus s : ComplaintStatus.values()) {
            cbFilterStatus.addItem(s.name());
        }
        styleComboBox(cbFilterStatus);
        cbFilterStatus.addActionListener(e -> performSearch());

        filterPanel.add(new JLabel("Find:"));
        filterPanel.getComponent(filterPanel.getComponentCount() - 1).setForeground(COLOR_TEXT_SECONDARY);
        filterPanel.add(txtSearch);
        filterPanel.add(btnSearch);
        filterPanel.add(new JSeparator(SwingConstants.VERTICAL));
        filterPanel.add(new JLabel("Category:"));
        filterPanel.getComponent(filterPanel.getComponentCount() - 1).setForeground(COLOR_TEXT_SECONDARY);
        filterPanel.add(cbFilterCategory);
        filterPanel.add(cbFilterPriority);
        filterPanel.add(cbFilterStatus);

        center.add(filterPanel, BorderLayout.NORTH);

        // JTable for complaints
        String[] columns = {"ID", "Complaint Title", "Category", "Priority", "Status", "Date Registered"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblComplaints = new JTable(tableModel);
        styleTable(tblComplaints);

        tblComplaints.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tblComplaints.getSelectedRow();
                    if (selectedRow != -1) {
                        int compId = (Integer) tblComplaints.getValueAt(selectedRow, 0);
                        openDetailDialog(compId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblComplaints);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(COLOR_CARD);
        
        center.add(scrollPane, BorderLayout.CENTER);

        // Double-click tip
        JLabel lblTip = new JLabel("💡 Tip: Double-click any row to view complete details, update status, and add resolution notes.");
        lblTip.setFont(FONT_SMALL);
        lblTip.setForeground(COLOR_TEXT_SECONDARY);
        center.add(lblTip, BorderLayout.SOUTH);

        mainPanel.add(center, BorderLayout.CENTER);
        return mainPanel;
    }

    private void styleTable(JTable table) {
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT_PRIMARY);
        table.setGridColor(COLOR_BORDER);
        table.setRowHeight(38);
        table.setSelectionBackground(new Color(60, 45, 110));
        table.setSelectionForeground(COLOR_TEXT_PRIMARY);
        table.setFont(FONT_BODY);

        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_SIDEBAR);
        header.setForeground(COLOR_TEXT_PRIMARY);
        header.setFont(FONT_BODY_BOLD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        header.setPreferredSize(new Dimension(0, 40));

        // Center render ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(60);

        // Render Status & Priority dynamically with colors
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, row, col);
                l.setFont(FONT_BODY_BOLD);
                l.setHorizontalAlignment(JLabel.CENTER);
                String priorityStr = val != null ? val.toString() : "";
                if (priorityStr.equals("URGENT")) {
                    l.setForeground(new Color(255, 82, 82));
                } else if (priorityStr.equals("HIGH")) {
                    l.setForeground(new Color(255, 171, 0));
                } else if (priorityStr.equals("MEDIUM")) {
                    l.setForeground(new Color(64, 196, 255));
                } else {
                    l.setForeground(COLOR_TEXT_SECONDARY);
                }
                return l;
            }
        });

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, row, col);
                l.setFont(FONT_BODY_BOLD);
                l.setHorizontalAlignment(JLabel.CENTER);
                String statusStr = val != null ? val.toString() : "";
                if (statusStr.equals("OPEN")) {
                    l.setForeground(COLOR_STATUS_OPEN);
                } else if (statusStr.equals("IN_PROGRESS")) {
                    l.setForeground(COLOR_STATUS_PROGRESS);
                } else if (statusStr.equals("RESOLVED")) {
                    l.setForeground(COLOR_STATUS_RESOLVED);
                } else {
                    l.setForeground(COLOR_STATUS_CLOSED);
                }
                return l;
            }
        });
    }

    private void performSearch() {
        try {
            String query = txtSearch.getText();
            String cat = (String) cbFilterCategory.getSelectedItem();
            String pri = (String) cbFilterPriority.getSelectedItem();
            String stat = (String) cbFilterStatus.getSelectedItem();

            if (cat.contains("All")) cat = "ALL";
            if (pri.contains("All")) pri = "ALL";
            if (stat.contains("All")) stat = "ALL";

            List<Complaint> list = service.searchComplaints(query, cat, pri, stat);
            populateTable(list);
        } catch (ComplaintException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Complaint> list) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Complaint c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getTitle(),
                    c.getCategory().getDisplayName(),
                    c.getPriority().name(),
                    c.getStatus().name(),
                    sdf.format(c.getCreatedAt())
            });
        }
    }

    private void refreshAllData() {
        try {
            List<Complaint> all = service.getAllComplaints();
            
            // Stats counts
            int total = all.size();
            int open = 0, progress = 0, resolved = 0;
            for (Complaint c : all) {
                switch (c.getStatus()) {
                    case OPEN -> open++;
                    case IN_PROGRESS -> progress++;
                    case RESOLVED -> resolved++;
                    case CLOSED -> {}
                }
            }

            lblTotalCount.setText(String.valueOf(total));
            lblOpenCount.setText(String.valueOf(open));
            lblProgressCount.setText(String.valueOf(progress));
            lblResolvedCount.setText(String.valueOf(resolved));

            // Load Directory Table
            populateTable(all);

            // Dashboard Recent list (max 5)
            pnlRecentList.removeAll();
            int count = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            for (Complaint c : all) {
                if (count >= 5) break;
                pnlRecentList.add(createRecentRowItem(c, sdf));
                pnlRecentList.add(Box.createVerticalStrut(8));
                count++;
            }
            if (total == 0) {
                JLabel lblNoRecent = new JLabel("No complaints registered yet.", SwingConstants.CENTER);
                lblNoRecent.setFont(FONT_BODY);
                lblNoRecent.setForeground(COLOR_TEXT_SECONDARY);
                lblNoRecent.setBorder(new EmptyBorder(20, 0, 20, 0));
                lblNoRecent.setAlignmentX(Component.CENTER_ALIGNMENT);
                pnlRecentList.add(lblNoRecent);
            }
            
            pnlRecentList.revalidate();
            pnlRecentList.repaint();

        } catch (ComplaintException e) {
            System.err.println("Failed to refresh statistics: " + e.getMessage());
        }
    }

    private JPanel createRecentRowItem(Complaint c, SimpleDateFormat sdf) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(COLOR_SIDEBAR);
        item.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Click to view details
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openDetailDialog(c.getId());
            }
        });

        // Left section (ID & Title)
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
        left.setBackground(COLOR_SIDEBAR);
        
        JLabel lblId = new JLabel("CMP-" + c.getId());
        lblId.setFont(FONT_BODY_BOLD);
        lblId.setForeground(COLOR_ACCENT);

        JLabel lblTitle = new JLabel(" -  " + c.getTitle());
        lblTitle.setFont(FONT_BODY);
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);

        left.add(lblId);
        left.add(lblTitle);
        item.add(left, BorderLayout.WEST);

        // Right section (Category & Date & Status Badge)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.TRAILING, 15, 0));
        right.setBackground(COLOR_SIDEBAR);

        JLabel lblCat = new JLabel(c.getCategory().getDisplayName());
        lblCat.setFont(FONT_SMALL);
        lblCat.setForeground(COLOR_TEXT_SECONDARY);

        JLabel lblDate = new JLabel(sdf.format(c.getCreatedAt()));
        lblDate.setFont(FONT_SMALL);
        lblDate.setForeground(COLOR_TEXT_SECONDARY);

        JLabel lblStatus = new JLabel(c.getStatus().name());
        lblStatus.setFont(FONT_BODY_BOLD);
        switch (c.getStatus()) {
            case OPEN -> lblStatus.setForeground(COLOR_STATUS_OPEN);
            case IN_PROGRESS -> lblStatus.setForeground(COLOR_STATUS_PROGRESS);
            case RESOLVED -> lblStatus.setForeground(COLOR_STATUS_RESOLVED);
            case CLOSED -> lblStatus.setForeground(COLOR_STATUS_CLOSED);
        }

        right.add(lblCat);
        right.add(lblDate);
        right.add(lblStatus);
        item.add(right, BorderLayout.EAST);

        return item;
    }

    private void openDetailDialog(int compId) {
        try {
            Complaint complaint = service.getComplaintById(compId);
            JDialog dialog = new JDialog(this, "Complaint details - CMP-" + complaint.getId(), true);
            dialog.setSize(620, 560);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(COLOR_CARD);
            dialog.setLayout(new BorderLayout());

            JPanel pnl = new JPanel(new GridBagLayout());
            pnl.setBackground(COLOR_CARD);
            pnl.setBorder(new EmptyBorder(25, 25, 25, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.NORTHWEST;

            // Details labels & displays
            int row = 0;
            
            // Row 0: Case Title
            gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.2;
            JLabel lbl1 = new JLabel("Title:");
            lbl1.setForeground(COLOR_TEXT_SECONDARY);
            lbl1.setFont(FONT_BODY_BOLD);
            pnl.add(lbl1, gbc);
            
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 0.8;
            JLabel valTitle = new JLabel(complaint.getTitle());
            valTitle.setForeground(COLOR_TEXT_PRIMARY);
            valTitle.setFont(FONT_HEADER);
            pnl.add(valTitle, gbc);
            row++;

            // Row 1: Category & Priority
            gbc.gridwidth = 1;
            gbc.gridy = row;
            
            gbc.gridx = 0; gbc.weightx = 0.2;
            JLabel lbl2 = new JLabel("Category:");
            lbl2.setForeground(COLOR_TEXT_SECONDARY);
            lbl2.setFont(FONT_BODY_BOLD);
            pnl.add(lbl2, gbc);
            
            gbc.gridx = 1; gbc.weightx = 0.3;
            JLabel valCat = new JLabel(complaint.getCategory().getDisplayName());
            valCat.setForeground(COLOR_TEXT_PRIMARY);
            valCat.setFont(FONT_BODY);
            pnl.add(valCat, gbc);

            gbc.gridx = 2; gbc.weightx = 0.2;
            JLabel lbl3 = new JLabel("Priority:");
            lbl3.setForeground(COLOR_TEXT_SECONDARY);
            lbl3.setFont(FONT_BODY_BOLD);
            pnl.add(lbl3, gbc);
            
            gbc.gridx = 3; gbc.weightx = 0.3;
            JLabel valPri = new JLabel(complaint.getPriority().name());
            valPri.setForeground(COLOR_TEXT_PRIMARY);
            valPri.setFont(FONT_BODY);
            pnl.add(valPri, gbc);
            row++;

            // Row 2: Customer Contact
            gbc.gridy = row;
            gbc.gridx = 0;
            JLabel lbl4 = new JLabel("Contact Email:");
            lbl4.setForeground(COLOR_TEXT_SECONDARY);
            lbl4.setFont(FONT_BODY_BOLD);
            pnl.add(lbl4, gbc);

            gbc.gridx = 1;
            JLabel valEmail = new JLabel(complaint.getContactEmail().isEmpty() ? "N/A" : complaint.getContactEmail());
            valEmail.setForeground(COLOR_TEXT_PRIMARY);
            valEmail.setFont(FONT_BODY);
            pnl.add(valEmail, gbc);

            gbc.gridx = 2;
            JLabel lbl5 = new JLabel("Phone:");
            lbl5.setForeground(COLOR_TEXT_SECONDARY);
            lbl5.setFont(FONT_BODY_BOLD);
            pnl.add(lbl5, gbc);

            gbc.gridx = 3;
            JLabel valPhone = new JLabel(complaint.getContactPhone().isEmpty() ? "N/A" : complaint.getContactPhone());
            valPhone.setForeground(COLOR_TEXT_PRIMARY);
            valPhone.setFont(FONT_BODY);
            pnl.add(valPhone, gbc);
            row++;

            // Row 3: Timestamps
            gbc.gridy = row;
            gbc.gridx = 0;
            JLabel lbl6 = new JLabel("Created:");
            lbl6.setForeground(COLOR_TEXT_SECONDARY);
            lbl6.setFont(FONT_BODY_BOLD);
            pnl.add(lbl6, gbc);

            gbc.gridx = 1;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            JLabel valCreated = new JLabel(sdf.format(complaint.getCreatedAt()));
            valCreated.setForeground(COLOR_TEXT_PRIMARY);
            valCreated.setFont(FONT_BODY);
            pnl.add(valCreated, gbc);

            gbc.gridx = 2;
            JLabel lbl7 = new JLabel("Last Updated:");
            lbl7.setForeground(COLOR_TEXT_SECONDARY);
            lbl7.setFont(FONT_BODY_BOLD);
            pnl.add(lbl7, gbc);

            gbc.gridx = 3;
            JLabel valUpdated = new JLabel(sdf.format(complaint.getUpdatedAt()));
            valUpdated.setForeground(COLOR_TEXT_PRIMARY);
            valUpdated.setFont(FONT_BODY);
            pnl.add(valUpdated, gbc);
            row++;

            // Row 4: Description (JTextArea)
            gbc.gridy = row;
            gbc.gridx = 0;
            JLabel lblDesc = new JLabel("Description:");
            lblDesc.setForeground(COLOR_TEXT_SECONDARY);
            lblDesc.setFont(FONT_BODY_BOLD);
            pnl.add(lblDesc, gbc);

            gbc.gridx = 1; gbc.gridwidth = 3;
            JTextArea areaDesc = new JTextArea(complaint.getDescription());
            areaDesc.setLineWrap(true);
            areaDesc.setWrapStyleWord(true);
            areaDesc.setEditable(false);
            areaDesc.setBackground(COLOR_SIDEBAR);
            areaDesc.setForeground(COLOR_TEXT_PRIMARY);
            areaDesc.setFont(FONT_BODY);
            JScrollPane scrollDesc = new JScrollPane(areaDesc);
            scrollDesc.setPreferredSize(new Dimension(0, 80));
            scrollDesc.setBorder(new LineBorder(COLOR_BORDER));
            pnl.add(scrollDesc, gbc);
            row++;

            // Separation Line
            gbc.gridy = row;
            gbc.gridx = 0; gbc.gridwidth = 4;
            pnl.add(new JSeparator(), gbc);
            row++;

            // Action Fields: Update Status & Resolution Notes
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.gridx = 0;
            JLabel lblStatus = new JLabel("Current Status:");
            lblStatus.setForeground(COLOR_TEXT_SECONDARY);
            lblStatus.setFont(FONT_BODY_BOLD);
            pnl.add(lblStatus, gbc);

            gbc.gridx = 1; gbc.gridwidth = 3;
            JComboBox<ComplaintStatus> cbStatus = new JComboBox<>(ComplaintStatus.values());
            cbStatus.setSelectedItem(complaint.getStatus());
            styleComboBox(cbStatus);
            pnl.add(cbStatus, gbc);
            row++;

            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.gridx = 0;
            JLabel lblNotes = new JLabel("Action Taken / Resolution Notes:");
            lblNotes.setForeground(COLOR_TEXT_SECONDARY);
            lblNotes.setFont(FONT_BODY_BOLD);
            pnl.add(lblNotes, gbc);

            gbc.gridx = 1; gbc.gridwidth = 3;
            JTextArea areaNotes = new JTextArea(complaint.getResolutionNotes());
            areaNotes.setLineWrap(true);
            areaNotes.setWrapStyleWord(true);
            areaNotes.setBackground(COLOR_SIDEBAR);
            areaNotes.setForeground(COLOR_TEXT_PRIMARY);
            areaNotes.setFont(FONT_BODY);
            areaNotes.setCaretColor(COLOR_TEXT_PRIMARY);
            JScrollPane scrollNotes = new JScrollPane(areaNotes);
            scrollNotes.setPreferredSize(new Dimension(0, 80));
            scrollNotes.setBorder(new LineBorder(COLOR_BORDER));
            pnl.add(scrollNotes, gbc);
            row++;

            // Bottom Buttons Panel
            JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 0));
            btnPnl.setBackground(COLOR_CARD);

            JButton btnCancel = new JButton("Close View");
            styleSecondaryButton(btnCancel);
            btnCancel.addActionListener(e -> dialog.dispose());

            JButton btnSave = new JButton("Save Status Updates");
            styleActionButton(btnSave);
            btnSave.addActionListener(e -> {
                try {
                    ComplaintStatus newStatus = (ComplaintStatus) cbStatus.getSelectedItem();
                    String notes = areaNotes.getText().trim();
                    
                    complaint.setStatus(newStatus);
                    complaint.setResolutionNotes(notes);
                    complaint.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                    service.updateComplaint(complaint);
                    JOptionPane.showMessageDialog(dialog, "Complaint updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllData();
                    dialog.dispose();
                } catch (ComplaintException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnPnl.add(btnCancel);
            btnPnl.add(btnSave);

            dialog.add(pnl, BorderLayout.CENTER);
            dialog.add(btnPnl, BorderLayout.SOUTH);
            btnPnl.setBorder(new EmptyBorder(10, 0, 20, 25));

            dialog.setVisible(true);

        } catch (ComplaintException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error loading complaint details", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Styling Helpers ---
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY_BOLD);
        label.setForeground(COLOR_TEXT_SECONDARY);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(COLOR_SIDEBAR);
        field.setForeground(COLOR_TEXT_PRIMARY);
        field.setCaretColor(COLOR_TEXT_PRIMARY);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setBackground(COLOR_SIDEBAR);
        area.setForeground(COLOR_TEXT_PRIMARY);
        area.setCaretColor(COLOR_TEXT_PRIMARY);
        area.setFont(FONT_BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(6, 10, 6, 10));
        return area;
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setBackground(COLOR_SIDEBAR);
        box.setForeground(COLOR_TEXT_PRIMARY);
        box.setFont(FONT_BODY);
        box.setBorder(new LineBorder(COLOR_BORDER, 1));
        // Remove standard borders/renderers to match dark theme
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(isSelected ? COLOR_ACCENT : COLOR_SIDEBAR);
                c.setForeground(COLOR_TEXT_PRIMARY);
                return c;
            }
        });
    }

    private void styleActionButton(JButton btn) {
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(COLOR_ACCENT);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_ACCENT, 1),
                new EmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_ACCENT_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_ACCENT);
            }
        });
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(COLOR_SIDEBAR);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COLOR_CARD);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_SIDEBAR);
            }
        });
    }

    // --- Custom Components ---

    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draws the rounded panel with border
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            graphics.setColor(COLOR_BORDER);
            graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        }
    }

    private class SidebarButton extends JButton {
        private final String targetCard;
        private boolean isActive = false;

        public SidebarButton(String text, String targetCard) {
            super(text);
            this.targetCard = targetCard;
            
            setPreferredSize(new Dimension(210, 48));
            setFont(FONT_BODY_BOLD);
            setForeground(COLOR_TEXT_SECONDARY);
            setBackground(COLOR_SIDEBAR);
            setFocusPainted(false);
            setOpaque(true);
            setContentAreaFilled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Clean alignment and borders
            setHorizontalAlignment(SwingConstants.LEADING);
            setBorder(new EmptyBorder(0, 20, 0, 0));

            addActionListener(e -> showCard(targetCard));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) {
                        setBackground(new Color(35, 35, 48));
                        setForeground(COLOR_TEXT_PRIMARY);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isActive) {
                        setBackground(COLOR_SIDEBAR);
                        setForeground(COLOR_TEXT_SECONDARY);
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                setBackground(COLOR_ACCENT);
                setForeground(COLOR_TEXT_PRIMARY);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(200, 180, 255)),
                    new EmptyBorder(0, 15, 0, 0)
                ));
            } else {
                setBackground(COLOR_SIDEBAR);
                setForeground(COLOR_TEXT_SECONDARY);
                setBorder(new EmptyBorder(0, 20, 0, 0));
            }
        }
    }
}
