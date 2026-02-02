package com.abuzar.contactbook.actBookApp;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactService {

    // 1. Save Contact
    public void saveContact(Contact contact) {
        String query = "INSERT INTO contacts(name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getPhone());
            stmt.setString(3, contact.getEmail());
            stmt.setString(4, contact.getAddress());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println(" Contact saved: " + contact.getName());
            }
        } catch (SQLException e) {
            System.err.println("Save failed: " + e.getMessage());
            // Do NOT throw — handle gracefully
        }
    }

    // 2. Search Contacts (partial match on name or phone)
    public List<Contact> searchContacts(String keyword) {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT id, name, phone, email, address FROM contacts WHERE name LIKE ? OR phone LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Contact c = new Contact();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setAddress(rs.getString("address"));
                contacts.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
        }
        return contacts;
    }

    // 3. Get All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT id, name, phone, email, address FROM contacts";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Contact c = new Contact();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setAddress(rs.getString("address"));
                contacts.add(c);
            }
        } catch (SQLException e) {
            System.err.println(" Fetch all failed: " + e.getMessage());
        }
        return contacts;
    }

    // 4. Get Contact By ID
    public Contact getContactById(int id) {
        String query = "SELECT id, name, phone, email, address FROM contacts WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Contact c = new Contact();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setAddress(rs.getString("address"));
                return c;
            }
        } catch (SQLException e) {
            System.err.println(" Fetch by ID failed: " + e.getMessage());
        }
        return null;
    }

    // 5. Update Contact
    public void updateContact(Contact contact) {
        String query = "UPDATE contacts SET name = ?, phone = ?, email = ?, address = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getPhone());
            stmt.setString(3, contact.getEmail());
            stmt.setString(4, contact.getAddress());
            stmt.setInt(5, contact.getId());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println(" Contact updated: ID=" + contact.getId());
            } else {
                System.out.println(" No contact found with ID: " + contact.getId());
            }
        } catch (SQLException e) {
            System.err.println(" Update failed: " + e.getMessage());
        }
    }

    // 6. Delete Contact
    public void deleteContact(int id) {
        String query = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println(" Contact deleted: ID=" + id);
            } else {
                System.out.println(" No contact found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println(" Delete failed: " + e.getMessage());
        }
    }

    // 7. Export to CSV
    public void exportAllContactsToCSV(String filename) {
        String query = "SELECT id, name, phone, email, address FROM contacts";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             FileWriter writer = new FileWriter(filename)) {

            writer.write("ID,Name,Phone,Email,Address\n");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = escapeCSV(rs.getString("name"));
                String phone = escapeCSV(rs.getString("phone"));
                String email = escapeCSV(rs.getString("email"));
                String address = escapeCSV(rs.getString("address"));

                writer.write(String.format("%d,%s,%s,%s,%s\n", id, name, phone, email, address));
            }
            System.out.println(" Exported to: " + filename);

        } catch (SQLException | IOException e) {
            System.err.println(" Export failed: " + e.getMessage());
        }
    }

    // Helper: Escape CSV fields
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // 8. Display All Contacts (for console testing)
    public void displayAllContacts() {
        List<Contact> contacts = getAllContacts();
        if (contacts.isEmpty()) {
            System.out.println(" No contacts found.");
            return;
        }
        System.out.println("\n ALL CONTACTS:");
        System.out.println("------------------------------------------------------------");
        for (Contact c : contacts) {
            System.out.printf("ID: %d%n", c.getId());
            System.out.printf("Name: %s%n", c.getName());
            System.out.printf("Phone: %s%n", c.getPhone());
            System.out.printf("Email: %s%n", c.getEmail());
            System.out.printf("Address: %s%n", c.getAddress());
            System.out.println("------------------------------------------------------------");
        }
    }
}