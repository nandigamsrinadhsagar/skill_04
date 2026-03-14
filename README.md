# Experiment 4 – Spring Dependency Injection: Constructor & Setter Injection

## Overview
Demonstrates Spring IoC container injecting values into a `Student` POJO using both **Constructor Injection** and **Setter Injection**, configured via **XML** and **Java `@Configuration`**.

---

## Project Structure
```
src/main/
├── java/com/experiment/di/
│   ├── DIApplication.java                # Entry point – Tasks 5 & 6
│   ├── model/
│   │   └── Student.java                  # Tasks 1, 2, 3 – POJO with constructor + setters
│   ├── config/
│   │   └── AppConfig.java                # Task 4b – @Configuration beans
│   └── runner/
│       └── DemoController.java           # REST endpoints for browser/Postman demo
└── resources/
    ├── applicationContext.xml            # Task 4a – XML bean definitions
    └── application.properties
```

---

## Task Reference

| Task | Location | Detail |
|---|---|---|
| 1 | `Student.java` | Fields: `studentId`, `name`, `course`, `year` |
| 2 | `Student.java` | All-args constructor |
| 3 | `Student.java` | `setStudentId()`, `setName()`, `setCourse()`, `setYear()` |
| 4a (XML) | `applicationContext.xml` | `<constructor-arg>` and `<property>` tags |
| 4b (Annotation) | `AppConfig.java` | `@Configuration` + `@Bean` factory methods |
| 5 | `DIApplication.java` | `ClassPathXmlApplicationContext` + `SpringApplication.run()` |
| 6 | `DIApplication.java` CommandLineRunner | `context.getBean()` + `student.display()` |

---

## Injection Styles at a Glance

### XML – Constructor Injection
```xml
<bean id="studentConstructor" class="com.experiment.di.model.Student">
    <constructor-arg index="0" type="java.lang.String" value="STU-XML-01"/>
    <constructor-arg index="1" type="java.lang.String" value="Alice Johnson"/>
    <constructor-arg index="2" type="java.lang.String" value="Computer Science"/>
    <constructor-arg index="3" type="int"              value="2"/>
</bean>
```

### XML – Setter Injection
```xml
<bean id="studentSetter" class="com.experiment.di.model.Student">
    <property name="studentId" value="STU-XML-02"/>
    <property name="name"      value="Bob Smith"/>
    <property name="course"    value="Information Technology"/>
    <property name="year"      value="3"/>
</bean>
```

### Annotation – Constructor Injection
```java
@Bean
public Student studentAnnotationConstructor() {
    return new Student("STU-ANN-01", "David Lee", "Software Engineering", 3);
}
```

### Annotation – Setter Injection
```java
@Bean
public Student studentAnnotationSetter() {
    Student s = new Student();
    s.setStudentId("STU-ANN-02");
    s.setName("Eve Williams");
    s.setCourse("Artificial Intelligence");
    s.setYear(4);
    return s;
}
```

---

## Run
```bash
mvn spring-boot:run
```
Watch the **console** for the bean creation sequence and the formatted Student display table.

**Server:** `http://localhost:8080`

---

## Postman / Browser Endpoints

| URL | Description |
|---|---|
| `GET /di/beans` | All 3 annotation-configured beans |
| `GET /di/beans/studentAnnotationConstructor` | Single bean by name |
| `GET /di/beans/studentAnnotationSetter` | Single bean by name |
| `GET /di/beans/studentAnnotationMixed` | Single bean by name |
| `GET /di/compare` | Side-by-side comparison with pros/cons |
| `GET /di/explain` | Task-by-task DI explanation |
| `GET /di/constructor-vs-setter` | Decision guide |

### Sample – GET /di/compare (excerpt)
```json
{
  "constructorInjection": {
    "xmlElement": "<constructor-arg index=\"0\" value=\"...\"/>",
    "pros": ["Immutable beans", "Mandatory deps enforced", "Easier unit testing"]
  },
  "setterInjection": {
    "xmlElement": "<property name=\"studentId\" value=\"...\"/>",
    "pros": ["Optional dependencies", "Easy to read", "Re-injectable"]
  }
}
```

---

## Constructor vs Setter – Quick Guide
| Use | When |
|---|---|
| **Constructor** | Mandatory dependency, want immutability, unit testing |
| **Setter** | Optional dependency, many configurable properties |

Spring team recommends **Constructor Injection** for mandatory dependencies.

---

## GitHub
```bash
git init
git add .
git commit -m "Experiment 4: Spring DI – Constructor and Setter Injection"
git remote add origin <your-repo-url>
git push -u origin main
```
