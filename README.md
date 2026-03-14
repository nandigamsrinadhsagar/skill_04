package com.experiment.di;

import com.experiment.di.model.Student;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DIApplicationTests {

    @Autowired ApplicationContext context;
    @Autowired MockMvc            mockMvc;

    // ── Task 5: XML container loads ───────────────────────────────

    @Test @Order(1)
    void xmlContextLoads() {
        try (ClassPathXmlApplicationContext xml =
                     new ClassPathXmlApplicationContext("applicationContext.xml")) {
            assertThat(xml).isNotNull();
            assertThat(xml.containsBean("studentConstructor")).isTrue();
            assertThat(xml.containsBean("studentSetter")).isTrue();
            assertThat(xml.containsBean("studentMixed")).isTrue();
        }
    }

    // ── Task 6: XML constructor injection ────────────────────────

    @Test @Order(2)
    void xmlConstructorInjection_valuesAreCorrect() {
        try (ClassPathXmlApplicationContext xml =
                     new ClassPathXmlApplicationContext("applicationContext.xml")) {
            Student s = (Student) xml.getBean("studentConstructor");
            assertThat(s.getStudentId()).isEqualTo("STU-XML-01");
            assertThat(s.getName()).isEqualTo("Alice Johnson");
            assertThat(s.getCourse()).isEqualTo("Computer Science");
            assertThat(s.getYear()).isEqualTo(2);
        }
    }

    // ── Task 6: XML setter injection ──────────────────────────────

    @Test @Order(3)
    void xmlSetterInjection_valuesAreCorrect() {
        try (ClassPathXmlApplicationContext xml =
                     new ClassPathXmlApplicationContext("applicationContext.xml")) {
            Student s = (Student) xml.getBean("studentSetter");
            assertThat(s.getStudentId()).isEqualTo("STU-XML-02");
            assertThat(s.getName()).isEqualTo("Bob Smith");
            assertThat(s.getCourse()).isEqualTo("Information Technology");
            assertThat(s.getYear()).isEqualTo(3);
        }
    }

    // ── Task 6: XML mixed injection ───────────────────────────────

    @Test @Order(4)
    void xmlMixedInjection_setterOverridesConstructor() {
        try (ClassPathXmlApplicationContext xml =
                     new ClassPathXmlApplicationContext("applicationContext.xml")) {
            Student s = (Student) xml.getBean("studentMixed");
            // setter should override constructor value
            assertThat(s.getCourse()).isEqualTo("Data Science (Hons)");
        }
    }

    // ── Task 4b & 5: Annotation context loads ────────────────────

    @Test @Order(5)
    void annotationContextLoads() {
        assertThat(context).isNotNull();
        assertThat(context.containsBean("studentAnnotationConstructor")).isTrue();
        assertThat(context.containsBean("studentAnnotationSetter")).isTrue();
        assertThat(context.containsBean("studentAnnotationMixed")).isTrue();
    }

    // ── Task 6: Annotation constructor injection ──────────────────

    @Test @Order(6)
    void annotationConstructorInjection_valuesAreCorrect() {
        Student s = context.getBean("studentAnnotationConstructor", Student.class);
        assertThat(s.getStudentId()).isEqualTo("STU-ANN-01");
        assertThat(s.getName()).isEqualTo("David Lee");
        assertThat(s.getCourse()).isEqualTo("Software Engineering");
        assertThat(s.getYear()).isEqualTo(3);
    }

    // ── Task 6: Annotation setter injection ───────────────────────

    @Test @Order(7)
    void annotationSetterInjection_valuesAreCorrect() {
        Student s = context.getBean("studentAnnotationSetter", Student.class);
        assertThat(s.getStudentId()).isEqualTo("STU-ANN-02");
        assertThat(s.getName()).isEqualTo("Eve Williams");
        assertThat(s.getCourse()).isEqualTo("Artificial Intelligence");
        assertThat(s.getYear()).isEqualTo(4);
    }

    // ── Singleton scope check ─────────────────────────────────────

    @Test @Order(8)
    void beansAreSingletons() {
        Student a = context.getBean("studentAnnotationConstructor", Student.class);
        Student b = context.getBean("studentAnnotationConstructor", Student.class);
        assertThat(a).isSameAs(b);
    }

    // ── REST endpoint tests ────────────────────────────────────────

    @Test @Order(9)
    void endpoint_beans_returns200() throws Exception {
        mockMvc.perform(get("/di/beans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beans").isArray())
                .andExpect(jsonPath("$.beans.length()").value(3));
    }

    @Test @Order(10)
    void endpoint_compare_returns200() throws Exception {
        mockMvc.perform(get("/di/compare"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constructorInjection").exists())
                .andExpect(jsonPath("$.setterInjection").exists());
    }

    @Test @Order(11)
    void endpoint_explain_returns200() throws Exception {
        mockMvc.perform(get("/di/explain"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task4a_xml").isNotEmpty());
    }

    @Test @Order(12)
    void endpoint_beanByName_found() throws Exception {
        mockMvc.perform(get("/di/beans/studentAnnotationConstructor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("David Lee"));
    }

    @Test @Order(13)
    void endpoint_beanByName_notFound_returns400() throws Exception {
        mockMvc.perform(get("/di/beans/nonExistentBean"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
