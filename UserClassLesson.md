# Building User, Staff & Student — From Your Database to Java

This lesson teaches you how to create the `User` class and its subclasses (`Staff`, `Student`) so that they map directly to the tables in your `university_booking (1).db` SQLite database.

---

## Your Database Structure (What You Already Have)

Before writing any Java, let's understand how your data is actually stored. You have **3 tables** that work together:

### Table: `User` (the parent — stores ALL users)
| Column            | Type    | Example                              |
|-------------------|---------|--------------------------------------|
| userID            | TEXT PK | `USR001`                             |
| name              | TEXT    | `Dr. Sarah Mitchell`                 |
| email             | TEXT    | `sarah.mitchell@university.ac.uk`    |
| role              | TEXT    | `Staff`, `Student`, or `Admin`       |
| department        | TEXT    | `Computer Science`                   |
| maxActiveBookings | INTEGER | `5`                                  |
| totalUsers        | INTEGER | `0`                                  |

### Table: `StaffStudent` (extra info for Staff and Students only)
| Column     | Type    | Example            |
|------------|---------|--------------------|
| userID     | TEXT PK | `USR001` (FK → User) |
| department | TEXT    | `Computer Science` |

### Table: `UserCertification` (each user can have many certifications)
| Column        | Type    | Example       |
|---------------|---------|---------------|
| id            | INTEGER | `1`           |
| userID        | TEXT    | `USR001`      |
| certification | TEXT    | `Fire Safety` |

> [!IMPORTANT]
> The `StaffStudent` table has a **Foreign Key** (`userID`) that points back to the `User` table. This is exactly like inheritance in Java — the child row "belongs to" a parent row.

---

## Step 1: The `User` Class (The Parent)

The `User` table holds the columns that are **shared by every type of user** (Staff, Student, and Admin). In Java, this becomes your parent class.

### 1a. Match the Attributes to the Database Columns

Look at the `User` table columns. Each column becomes a `private` field in your class:

```java
package com.project.coursework2.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    // These fields map DIRECTLY to the columns in your "User" table
    private String userID;            // ← User.userID
    private String name;              // ← User.name
    private String email;             // ← User.email
    private String role;              // ← User.role  ("Staff", "Student", or "Admin")
    private int maxActiveBookings;    // ← User.maxActiveBookings

    // This tracks total users created — it's a CLASS-level counter, so use 'static'
    private static int totalUsers = 0;

    // From the UserCertification table: one user can have MANY certifications
    // So we use a List to hold them
    private List<String> certifications;

    // From the UML: a User has 0..* Bookings
    private List<Booking> bookings;
```

> [!TIP]
> **Why no `department` here?** Look at your UML diagram again. The `department` attribute lives in the `Staff/Student` box, NOT in the `User` box. Even though the `User` *table* has a department column, in OOP we move it to the subclass. Your database schema also confirms this — the `StaffStudent` table is where `department` truly belongs.

> [!TIP]
> **Why no `password` or `isAuthorized`?** These don't exist in your database tables or your UML diagram. Only add fields that are in your design documents!

### 1b. The Constructor (Loading Data From the Database)

The constructor's job is to take data that you've read from the database and store it into the object's fields. The parameters should match what you'll get from your SQL query.

```java
    // Constructor — called when you create a User from database data
    public User(String userID, String name, String email, String role, int maxActiveBookings) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.role = role;
        this.maxActiveBookings = maxActiveBookings;
        this.certifications = new ArrayList<>();  // Start empty, load separately
        this.bookings = new ArrayList<>();        // Start empty, load separately
        totalUsers++;
    }
```

> [!NOTE]
> **Why are `certifications` and `bookings` not constructor parameters?** Because they come from *separate tables* (`UserCertification` and `Booking`). You'll load them in a second step after creating the User object. This is normal — databases store related data across multiple tables.

### 1c. The Methods (From Your UML Diagram)

Each method in the `+` section of the UML box becomes a `public` method:

```java
    // --- Methods from UML ---

    public String getRole() {
        return this.role;
    }

    public int getActiveCount() {
        int count = 0;
        for (Booking b : bookings) {
            if ("confirmed".equals(b.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public List<String> getCertifications() {
        return this.certifications;
    }

    // Call this method to add certifications loaded from the UserCertification table
    public void addCertification(String cert) {
        this.certifications.add(cert);
    }

    // Getters you'll need for the GUI and database operations
    public String getUserID()  { return userID; }
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public int getMaxActiveBookings() { return maxActiveBookings; }
    public List<Booking> getBookings() { return bookings; }

} // end of class
```

---

## Step 2: The `Staff` Class (Child of User)

In your database, when a user has `role = 'Staff'`, they ALSO have a row in the `StaffStudent` table. In Java, we model this using **inheritance** (`extends`).

### 2a. Use `extends User`

```java
package com.project.coursework2.model;

import java.util.List;

public class Staff extends User {
    //         ^^^^^^^^^^^^
    // This means Staff INHERITS all fields and methods from User.
    // You do NOT need to re-declare userID, name, email, etc.

    // The only NEW field comes from the StaffStudent table
    private String department;   // ← StaffStudent.department
```

### 2b. The Constructor Uses `super()`

When you create a `Staff` object, you must FIRST build the `User` part of it. You do this by calling `super(...)` which runs the parent's constructor:

```java
    public Staff(String userID, String name, String email, int maxActiveBookings, String department) {
        // Step 1: Build the User portion by calling the parent constructor
        super(userID, name, email, "Staff", maxActiveBookings);
        //    ^^^^^ this calls: new User(userID, name, email, "Staff", maxActiveBookings)

        // Step 2: Set the Staff-specific field
        this.department = department;
    }
```

> [!IMPORTANT]
> `super(...)` MUST be the very first line inside the constructor. Java requires this because the parent object needs to be fully built before you can add child-specific properties on top of it.

### 2c. Staff-Specific Getter

```java
    public String getDepartment() {
        return this.department;
    }

} // end of class
```

> [!NOTE]
> **What about `getCertifications()`?** You do NOT need to override it! Since we put the `certifications` list directly in `User`, the inherited `getCertifications()` method already works. Both Staff and Students can have certifications.

---

## Step 3: The `Student` Class (Also a Child of User)

`Student` follows the **exact same pattern** as `Staff`. In your database, students also have rows in the `StaffStudent` table with their department.

```java
package com.project.coursework2.model;

public class Student extends User {

    private String department;   // ← StaffStudent.department

    public Student(String userID, String name, String email, int maxActiveBookings, String department) {
        super(userID, name, email, "Student", maxActiveBookings);
        this.department = department;
    }

    public String getDepartment() {
        return this.department;
    }
}
```

---

## Step 4: Loading Data From The Database

Once your classes are ready, you'll need a way to actually **read rows from the database** and turn them into Java objects. Here's the pattern using JDBC:

### 4a. The SQL Query

To load a Staff user, you need data from TWO tables. You JOIN them:

```sql
SELECT u.userID, u.name, u.email, u.role, u.maxActiveBookings, ss.department
FROM User u
JOIN StaffStudent ss ON u.userID = ss.userID
WHERE u.role = 'Staff';
```

### 4b. The Java Pattern (How JDBC Works)

```java
// 1. Connect to the database
Connection conn = DriverManager.getConnection("jdbc:sqlite:university_booking (1).db");

// 2. Write your SQL query
String sql = "SELECT u.userID, u.name, u.email, u.maxActiveBookings, ss.department "
           + "FROM User u JOIN StaffStudent ss ON u.userID = ss.userID "
           + "WHERE u.role = 'Staff'";

// 3. Execute the query and get results
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(sql);

// 4. Loop through each row and create a Staff object
List<Staff> staffList = new ArrayList<>();
while (rs.next()) {
    Staff s = new Staff(
        rs.getString("userID"),           // Read the userID column
        rs.getString("name"),             // Read the name column
        rs.getString("email"),            // Read the email column
        rs.getInt("maxActiveBookings"),   // Read the maxActiveBookings column
        rs.getString("department")        // Read the department column
    );
    staffList.add(s);
}

// 5. Now load certifications for each staff member
for (Staff s : staffList) {
    String certSql = "SELECT certification FROM UserCertification WHERE userID = ?";
    PreparedStatement ps = conn.prepareStatement(certSql);
    ps.setString(1, s.getUserID());
    ResultSet certRs = ps.executeQuery();
    while (certRs.next()) {
        s.addCertification(certRs.getString("certification"));
    }
}
```

> [!NOTE]
> **Step 5 is why we have `addCertification()` in the User class.** After creating the object from the main query, we make a second query to the `UserCertification` table and load each certification string into the object's list.

---

## Summary: How Everything Maps Together

```
DATABASE TABLE          →    JAVA CLASS
─────────────────────────────────────────────
User                    →    User (parent class)
  ├─ userID             →      private String userID
  ├─ name               →      private String name
  ├─ email              →      private String email
  ├─ role               →      private String role
  └─ maxActiveBookings  →      private int maxActiveBookings

StaffStudent            →    Staff extends User
  └─ department         →      private String department

StaffStudent            →    Student extends User
  └─ department         →      private String department

UserCertification       →    User.certifications (List<String>)
  └─ certification      →      loaded via addCertification()
```

### The Key OOP Principles at Work

| Principle | What It Means | Where You See It |
|-----------|--------------|-----------------|
| **Inheritance** | Child classes get parent's fields for free | `Staff extends User` — Staff has userID, name, email without declaring them |
| **Encapsulation** | Fields are `private`, accessed via methods | `private String department` + `getDepartment()` |
| **`super()`** | Child constructor builds the parent part first | `super(userID, name, email, "Staff", maxActiveBookings)` |
| **`static`** | Belongs to the class, not any single object | `private static int totalUsers` — shared across ALL User instances |

---

## Your Turn!

Now open your `.java` files and try building each class yourself following these steps:

1. **Start with `User.java`** — declare the fields, write the constructor, add the methods
2. **Then `Staff.java`** — use `extends User`, call `super()`, add `department`
3. **Then `Student.java`** — almost identical to Staff, just with `"Student"` as the role
4. Try to compile after each class to catch errors early!
