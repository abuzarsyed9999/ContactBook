package com.abuzar.contactbook.actBookApp;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ContactGUI {
    private JFrame frame;
    private ContactService service;

    // Form fields
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField searchField;

    // Table components
    private JTable contactTable;
    private DefaultTableModel tableModel;

    // Buttons
    private JButton updateBtn;
    private JButton deleteBtn;

    public ContactGUI() {
        service = new ContactService();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("📞 Professional Contact Book Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        ((JComponent) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("📝 Contact Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        JButton saveBtn = new JButton("💾 Save Contact");
        updateBtn = new JButton("✏️ Update Contact");
        deleteBtn = new JButton("🗑️ Delete Contact");
        JButton exportBtn = new JButton("📤 Export to CSV");
        JButton refreshBtn = new JButton("🔄 Refresh");

        styleButton(saveBtn, new Color(46, 204, 113));
        styleButton(updateBtn, new Color(52, 152, 219));
        styleButton(deleteBtn, new Color(231, 76, 60));
        styleButton(exportBtn, new Color(155, 89, 182));
        styleButton(refreshBtn, new Color(243, 156, 18));

        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        saveBtn.addActionListener(e -> handleSave());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        exportBtn.addActionListener(e -> handleExport());
        refreshBtn.addActionListener(e -> loadAllContacts());

        buttonPanel.add(saveBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(refreshBtn);

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== SEARCH BAR =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("🔍 Search");
        styleButton(searchBtn, new Color(241, 196, 15));
        searchBtn.setForeground(Color.BLACK);
        searchBtn.addActionListener(e -> handleSearch());
        searchField.addActionListener(e -> handleSearch());

        searchPanel.add(new JLabel("Search by Name or Phone:"));
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(searchBtn);

        // ===== TABLE =====
        String[] columnNames = {"ID", "Name", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        contactTable = new JTable(tableModel);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.setFillsViewportHeight(true);
        contactTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contactTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        contactTable.setRowHeight(25);

        // ✅ FIXED: Proper row selection listener
        contactTable.getSelectionModel().addListSelectionListener(this::onRowSelected);

        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 All Contacts"));

        loadAllContacts();

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(searchPanel, BorderLayout.WEST);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // ✅ FIXED: Selection handler
    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        int viewRow = contactTable.getSelectedRow();
        if (viewRow >= 0) {
            // Convert to model row (safe even if table is sorted)
            int modelRow = contactTable.convertRowIndexToModel(viewRow);
            loadSelectedContactIntoForm(modelRow);
            updateBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
        } else {
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }
    }

    // ===== ACTION HANDLERS =====

    private void handleSave() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showError("Name and Phone are required!");
            return;
        }

        Contact contact = new Contact(name, phone, email, address);
        service.saveContact(contact);
        clearForm();
        loadAllContacts();
        showInfo("Contact saved successfully!");
    }

    private void handleUpdate() {
        int viewRow = contactTable.getSelectedRow();
        if (viewRow < 0) {
            showError("Please select a contact to update.");
            return;
        }

        int modelRow = contactTable.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showError("Name and Phone are required!");
            return;
        }

        Contact contact = new Contact(name, phone, email, address);
        contact.setId(id);
        service.updateContact(contact);
        clearForm();
        loadAllContacts();
        showInfo("Contact updated successfully!");
    }

    private void handleDelete() {
        int viewRow = contactTable.getSelectedRow();
        if (viewRow < 0) {
            showError("Please select a contact to delete.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to delete this contact?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            int modelRow = contactTable.convertRowIndexToModel(viewRow);
            int id = (int) tableModel.getValueAt(modelRow, 0);
            service.deleteContact(id);
            clearForm();
            loadAllContacts();
            showInfo("Contact deleted successfully!");
        }
    }

    private void handleExport() {
        service.exportAllContactsToCSV("contacts.csv");
        showInfo("Contacts exported to contacts.csv");
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllContacts();
            return;
        }

        List<Contact> results = service.searchContacts(keyword);
        tableModel.setRowCount(0);
        for (Contact c : results) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getName(), c.getPhone(), c.getEmail(), c.getAddress()
            });
        }
    }

    // ===== HELPER METHODS =====

    private void loadAllContacts() {
        searchField.setText("");
        List<Contact> contacts = service.getAllContacts();
        tableModel.setRowCount(0);
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getName(), c.getPhone(), c.getEmail(), c.getAddress()
            });
        }
    }

    private void loadSelectedContactIntoForm(int modelRow) {
        nameField.setText((String) tableModel.getValueAt(modelRow, 1));
        phoneField.setText((String) tableModel.getValueAt(modelRow, 2));
        emailField.setText((String) tableModel.getValueAt(modelRow, 3));
        addressField.setText((String) tableModel.getValueAt(modelRow, 4));
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        // ✅ DO NOT call clearSelection() here — it interferes with button state
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Font baseFont = new Font("Segoe UI", Font.PLAIN, 13);
                UIManager.put("Label.font", baseFont);
                UIManager.put("TextField.font", baseFont);
                UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ContactGUI();
        });
    }
}