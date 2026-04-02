# Translating Staff and Student UML to Java

This guide covers how to build the `Staff` and `Student` classes based on the `Staff/Student` box in your UML diagram. 

## 1. Inheritance (`extends`)
The UML diagram shows an open arrow pointing from `Staff/Student` up to `User`. This means they are **Subclasses** of `User`.
In Java, you use the `extends` keyword. By extending `User`, the `Staff` and `Student` classes automatically get all the core properties (like name, email, role, and bookings) without you having to retype them.

```java
public class Staff extends User {
    // We only need to define things specific to Staff here!
}
```

## 2. Setting Up Subclass Attributes
Looking at the `Staff/Student` box, we see two attributes:
`- department: String`
`- certifications: List`

Because of the `-` sign, they must be `private`. Let's add them:
```java
    private String department;
    private List<String> certifications;
```

## 3. The `super()` Constructor
The UML diagram lists the constructor: `+Staff/Student(id:String, dept:String)`. 
In Java, when a child class is created, it **must** call the parent class's constructor. The parent `User` constructor expects four things: `id, name, email, role`. 
You pass these values up to the parent using the `super(...)` method. It must be the very first line inside your constructor.

```java
    // Notice how we take in all the User details + the new department detail
    public Staff(String id, String name, String email, String department) {
        
        // 1. Call the parent User constructor FIRST. 
        // We can hardcode the role as "Staff"!
        super(id, name, email, "Staff"); 
        
        // 2. Set the variables specific to this short subclass
        this.department = department;
        
        // 3. Initialize the list so it's ready to use
        this.certifications = new ArrayList<>(); 
    }
```
*(Note: Sometimes UML diagrams only list the *new* parameters in a subclass constructor to save space. We still need to ask for `name` and `email` here so we can properly build the parent `User` portion!)*

## 4. Subclass Methods and `@Override`
The UML lists two methods:
`+getCertifications(): List`
`+getDepartment(): String`

In the lesson on `User`, we created a `getCertifications()` method that returns an empty list. Because `Staff` owns the *actual* list of certificates, we use the `@Override` annotation to tell Java that `Staff` has its own specific version of this method!

```java
    @Override
    public List<String> getCertifications() {
        // This replaces the empty default method from the User class
        return this.certifications;
    }
    
    // A standard getter
    public String getDepartment() {
        return this.department;
    }
```

## Your Turn!
The UML diagram groups `Staff/Student` into the same box because their implementation is going to be almost identical. 

Try writing your `Staff.java` file first using the `extends User` keyword, the `super()` method in the constructor, and the `@Override` annotation. Once you have that working, your `Student.java` file will follow the exact same structure!
