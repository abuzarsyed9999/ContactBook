# 📞 Contact Book Manager

A desktop contact management application built with **Java Swing** and **JDBC**.

## ✨ Features
- Add, Edit, Delete contacts
- Search by name or phone
- Export contacts to CSV
- Professional UI with real-time table

## 🛠️ Tech Stack
- Java 11+
- Maven
- MySQL
- Swing GUI

## ▶️ How to Run
1. Create MySQL database `contact_book_db` with `contacts` table
2. Update DB credentials in `DBConnection.java`
3. Run:
   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass="com.abuzar.contactbook.actBookApp.ContactGUI"
