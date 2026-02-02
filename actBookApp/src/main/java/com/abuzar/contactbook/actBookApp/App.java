package com.abuzar.contactbook.actBookApp;

import java.util.List;

public class App {
    public static void main(String[] args) {
        ContactService service = new ContactService();

        // 1. Add a contact (UNCOMMENT ONLY ONCE to avoid duplicates)
        // Contact c = new Contact("Abu", "6301372060", "abuzarcda@gmail.com", "Madanapalle");
        // try {
        //     service.saveContact(c);
        // } catch (Exception e) {
        //     System.out.println("Save failed: " + e.getMessage());
        // }

        // 2. Search contacts
        try {
            List<Contact> results = service.searchContacts("Abu");
            System.out.println("\n🔍 Found " + results.size() + " contact(s) for 'Abu':");
            for (Contact contact : results) {
                System.out.println("ID: " + contact.getId() +
                        ", Name: " + contact.getName() +
                        ", Phone: " + contact.getPhone());
            }
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
        }

        // 3. Export to CSV
        service.exportAllContactsToCSV("contacts.csv");
    }
}