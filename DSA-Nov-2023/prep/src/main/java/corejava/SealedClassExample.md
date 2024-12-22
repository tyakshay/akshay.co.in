# Comprehensive Guide to Java Sealed Classes

## Introduction
Sealed classes, introduced in Java 17, provide a mechanism to restrict which classes can inherit from a class or implement an interface. This guide covers the key aspects of sealed classes including their interaction with modularity, generics, and access control, along with advanced topics and common pitfalls.

## Basic Syntax and Usage

### Declaring Sealed Classes
```java
public sealed class Shape 
    permits Circle, Rectangle, Triangle {
    // class implementation
}

public final class Circle extends Shape {
    // implementation
}

public final class Rectangle extends Shape {
    // implementation
}

public final class Triangle extends Shape {
    // implementation
}
```

Note that permitted subclasses must be explicitly declared as either:
- `final` (cannot be extended)
- `sealed` (can only be extended by its permitted subclasses)
- `non-sealed` (can be extended by any class)

## Advanced Topics and Edge Cases

### 1. Nested Sealed Classes
Sealed classes can be nested, creating complex hierarchies:

```java
public sealed class Outer permits Outer.Inner {
    public static sealed class Inner extends Outer 
        permits Inner.DeepNested {
        public static final class DeepNested extends Inner { }
    }
}
```

### 2. Anonymous Classes
Important: You cannot create anonymous subclasses of sealed classes. This won't compile:

```java
Shape shape = new Shape() { }; // Compilation error!
```

### 3. Records with Sealed Classes
Records can be permitted subclasses of sealed classes:

```java
public sealed interface JSONValue 
    permits JSONObject, JSONArray, JSONString, JSONNumber {
    
    public record JSONString(String value) implements JSONValue { }
    public record JSONNumber(double value) implements JSONValue { }
    public record JSONArray(List<JSONValue> values) implements JSONValue { }
    public record JSONObject(Map<String, JSONValue> values) implements JSONValue { }
}
```

### 4. Multiple Inheritance with Sealed Interfaces
Sealed interfaces can work together:

```java
public sealed interface Drawable permits Shape { }
public sealed interface Scalable permits Shape { }

public sealed class Shape 
    permits Circle, Rectangle 
    implements Drawable, Scalable { }

public final class Circle extends Shape { }
public final class Rectangle extends Shape { }
```

## Tricky Questions and Answers

### Q1: Can a sealed class have zero permitted subclasses?
A: Yes, but it's rare. This creates a class that can't be extended at all:

```java
public sealed class Singleton permits { }
```

This is equivalent to, but less clear than, using `final`:
```java
public final class Singleton { }
```

### Q2: What happens with type inference and sealed classes?
A: Type inference can be tricky. Consider:

```java
sealed interface Box<T> permits EmptyBox, FullBox { }
final class EmptyBox<T> implements Box<T> { }
final class FullBox<T> implements Box<T> {
    private final T value;
    FullBox(T value) { this.value = value; }
}

// This works:
Box<String> box1 = new EmptyBox<>();

// This fails:
var box2 = new EmptyBox<>(); // Error: Cannot infer type
```

### Q3: Can sealed classes be abstract?
A: Yes! This is actually a common pattern:

```java
public sealed abstract class AbstractShape 
    permits Circle, Rectangle {
    
    protected abstract double area();
    
    public final String getDescription() {
        return "Shape with area: " + area();
    }
}
```

### Q4: How do sealed classes interact with reflection?
A: You can inspect sealed classes using reflection:

```java
public class SealedClassInspector {
    public static void inspectClass(Class<?> clazz) {
        if (clazz.isSealed()) {
            System.out.println("Permitted subclasses of " + clazz.getName() + ":");
            for (Class<?> permittedSubclass : clazz.getPermittedSubclasses()) {
                System.out.println("- " + permittedSubclass.getName());
            }
        }
    }
}
```

### Q5: Can you have circular dependencies in sealed hierarchies?
A: No, this won't compile:

```java
sealed class A permits B { }
sealed class B permits A { } // Compilation error!
```

## Common Pitfalls and Solutions

### 1. Package Private Access
Problem:
```java
// package com.example.shapes
public sealed class Shape permits Circle { }

// package com.example.impl
class Circle extends Shape { } // Error!
```

Solution:
```java
// package com.example.impl
public class Circle extends Shape { }
```

### 2. Missing Permitted Subclass Declaration
Problem:
```java
public sealed class Vehicle permits Car, Truck { }
public class Car extends Vehicle { } // Error: must be final, sealed, or non-sealed
```

Solution:
```java
public final class Car extends Vehicle { }
```

### 3. Generic Type Parameter Bounds
Problem:
```java
public sealed interface Container<T> permits Box { }
public final class Box<T extends Number> implements Container<T> { } // Error!
```

Solution:
```java
public sealed interface Container<T> permits Box { }
public final class Box<T> implements Container<T> { }
// OR
public sealed interface Container<T extends Number> permits Box { }
public final class Box<T extends Number> implements Container<T> { }
```

## Performance Considerations

### Pattern Matching Optimization
The Java compiler can optimize pattern matching with sealed classes:

```java
public sealed interface Shape permits Circle, Rectangle {
    double area();
}

public final class Circle implements Shape {
    private final double radius;
    @Override public double area() { return Math.PI * radius * radius; }
}

public final class Rectangle implements Shape {
    private final double width, height;
    @Override public double area() { return width * height; }
}

// This switch will be optimized by the compiler
public double getArea(Shape shape) {
    return switch (shape) {
        case Circle c -> c.area();
        case Rectangle r -> r.area();
    }; // No default needed - compiler knows it's exhaustive
}
```

## Testing Strategies

### 1. Exhaustiveness Testing
Test that pattern matching handles all cases:

```java
@Test
void testShapeProcessing() {
    List<Shape> shapes = List.of(
        new Circle(5),
        new Rectangle(4, 6)
    );
    
    // Should process all shapes without exception
    shapes.forEach(shape -> {
        double area = switch (shape) {
            case Circle c -> c.area();
            case Rectangle r -> r.area();
        };
        assertNotNull(area);
    });
}
```

### 2. Boundary Testing
Test edge cases with generic sealed classes:

```java
@Test
void testGenericContainer() {
    Container<Number> numberContainer = new NumberContainer(42);
    Container<String> stringContainer = new StringContainer("test");
    
    assertEquals(42, numberContainer.getValue());
    assertEquals("test", stringContainer.getValue());
}
```

## Best Practices and Design Patterns

### 1. Factory Pattern with Sealed Classes
```java
public sealed interface Shape permits Circle, Rectangle {
    static Shape createCircle(double radius) {
        return new Circle(radius);
    }
    
    static Shape createRectangle(double width, double height) {
        return new Rectangle(width, height);
    }
}
```

### 2. Visitor Pattern
```java
public sealed interface Shape permits Circle, Rectangle {
    <T> T accept(ShapeVisitor<T> visitor);
}

public interface ShapeVisitor<T> {
    T visitCircle(Circle circle);
    T visitRectangle(Rectangle rectangle);
}

public final class Circle implements Shape {
    @Override
    public <T> T accept(ShapeVisitor<T> visitor) {
        return visitor.visitCircle(this);
    }
}
```

## Conclusion
Sealed classes provide a powerful way to control class hierarchies in Java. Understanding their nuances, including edge cases and common pitfalls, is crucial for effective usage. Regular practice with different scenarios and patterns will help build expertise with this feature.

## Additional Resources
- [JEP 409: Sealed Classes](https://openjdk.java.net/jeps/409)
- [Java Language Specification - Sealed Classes](https://docs.oracle.com/javase/specs/jls/se17/html/jls-8.html#jls-8.1.6)
