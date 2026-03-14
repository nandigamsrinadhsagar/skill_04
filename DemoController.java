package com.experiment.di.runner;

import com.experiment.di.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DemoController – exposes all DI beans and explanations via HTTP.
 *
 * Endpoints:
 *   GET /di/beans                  → all 3 annotation-configured beans
 *   GET /di/beans/{id}             → single bean by name
 *   GET /di/compare                → side-by-side comparison table
 *   GET /di/explain                → DI concept explanation
 *   GET /di/constructor-vs-setter  → when to use which injection type
 */
@RestController
@RequestMapping("/di")
public class DemoController {

    @Autowired
    private ApplicationContext context;

    /**
     * GET /di/beans
     * Returns all three annotation-configured Student beans.
     */
    @GetMapping("/beans")
    public ResponseEntity<Map<String, Object>> getAllBeans() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("source", "Annotation (@Configuration / @Bean)");
        result.put("beans", List.of(
                toMap("studentAnnotationConstructor", "Constructor Injection"),
                toMap("studentAnnotationSetter",       "Setter Injection"),
                toMap("studentAnnotationMixed",        "Mixed Injection")
        ));
        return ResponseEntity.ok(result);
    }

    /**
     * GET /di/beans/{beanName}
     * Returns a single bean by its id.
     * Examples:
     *   /di/beans/studentAnnotationConstructor
     *   /di/beans/studentAnnotationSetter
     *   /di/beans/studentAnnotationMixed
     */
    @GetMapping("/beans/{beanName}")
    public ResponseEntity<?> getBeanByName(@PathVariable String beanName) {
        try {
            Student s = context.getBean(beanName, Student.class);
            return ResponseEntity.ok(studentToMap(s, beanName, ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error",    "Bean not found: " + beanName,
                    "available", List.of("studentAnnotationConstructor",
                                         "studentAnnotationSetter",
                                         "studentAnnotationMixed")
            ));
        }
    }

    /**
     * GET /di/compare
     * Returns a comparison of both injection methods.
     */
    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compare() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("experiment", "Experiment 4 – Spring Dependency Injection");

        Map<String, Object> constructor = new LinkedHashMap<>();
        constructor.put("description",  "Spring calls the all-args constructor once");
        constructor.put("xmlElement",   "<constructor-arg index=\"0\" value=\"...\"/>");
        constructor.put("javaStyle",    "return new Student(id, name, course, year);");
        constructor.put("pros",         List.of("Immutable beans", "Mandatory deps enforced", "Easier unit testing"));
        constructor.put("cons",         List.of("Verbose with many params", "Requires all-args constructor"));
        constructor.put("beanExample",  studentToMap(
                context.getBean("studentAnnotationConstructor", Student.class),
                "studentAnnotationConstructor", "Constructor"));

        Map<String, Object> setter = new LinkedHashMap<>();
        setter.put("description",  "Spring calls no-arg constructor then each setter");
        setter.put("xmlElement",   "<property name=\"studentId\" value=\"...\"/>");
        setter.put("javaStyle",    "s = new Student(); s.setStudentId(...); s.setName(...);");
        setter.put("pros",         List.of("Optional dependencies", "Easy to read", "Re-injectable"));
        setter.put("cons",         List.of("Object usable before injection", "Not immutable"));
        setter.put("beanExample",  studentToMap(
                context.getBean("studentAnnotationSetter", Student.class),
                "studentAnnotationSetter", "Setter"));

        result.put("constructorInjection", constructor);
        result.put("setterInjection",      setter);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /di/explain
     * Returns the DI concept explanation for each task.
     */
    @GetMapping("/explain")
    public ResponseEntity<Map<String, Object>> explain() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("experiment",  "Experiment 4 – Spring Dependency Injection");
        m.put("task1",       "Student class with fields: studentId, name, course, year");
        m.put("task2",       "All-args constructor: new Student(String, String, String, int)");
        m.put("task3",       "Setter methods: setStudentId(), setName(), setCourse(), setYear()");
        m.put("task4a_xml",  "applicationContext.xml — <constructor-arg> and <property> tags");
        m.put("task4b_ann",  "AppConfig.java — @Configuration + @Bean factory methods");
        m.put("task5",       "ClassPathXmlApplicationContext (XML) + SpringApplication.run() (Annotation)");
        m.put("task6",       "context.getBean('beanId') then student.display()");
        m.put("iocContainer","Spring IoC container manages object creation and wiring");
        m.put("dipPrinciple","High-level modules should not depend on low-level modules; both depend on abstractions");
        return ResponseEntity.ok(m);
    }

    /**
     * GET /di/constructor-vs-setter
     * Decision guide: when to use which injection type.
     */
    @GetMapping("/constructor-vs-setter")
    public ResponseEntity<Map<String, Object>> guide() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("useConstructorWhen", List.of(
                "Dependency is mandatory (required)",
                "You want an immutable bean (final fields)",
                "You want to guarantee the object is fully initialised",
                "Writing unit tests without a Spring context"
        ));
        m.put("useSetterWhen", List.of(
                "Dependency is optional",
                "You want to allow re-configuration after creation",
                "The bean has many optional properties",
                "Legacy code with a no-arg constructor"
        ));
        m.put("springRecommendation",
                "Spring team recommends Constructor Injection for mandatory deps, " +
                "Setter Injection for optional deps.");
        return ResponseEntity.ok(m);
    }

    // ── Helpers ───────────────────────────────────────────────────

    private Map<String, Object> toMap(String beanName, String injectionType) {
        Student s = context.getBean(beanName, Student.class);
        return studentToMap(s, beanName, injectionType);
    }

    private Map<String, Object> studentToMap(Student s, String beanName, String injectionType) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (!beanName.isEmpty())      m.put("beanName",      beanName);
        if (!injectionType.isEmpty()) m.put("injectionType", injectionType);
        m.put("studentId", s.getStudentId());
        m.put("name",      s.getName());
        m.put("course",    s.getCourse());
        m.put("year",      s.getYear());
        return m;
    }
}
