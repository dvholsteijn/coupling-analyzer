package nl.dvholsteijn.couplinganalyzer.parser;

import nl.dvholsteijn.couplinganalyzer.model.ClassNode;
import nl.dvholsteijn.couplinganalyzer.model.ClassType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JavaParserImplTest {

    @TempDir
    Path tempDir;

    private JavaParserImpl parser;

    @BeforeEach
    void setUp() {
        // Given: A fresh parser instance for each test
        parser = new JavaParserImpl();
    }

    @Test
    void shouldParseSimpleClass() throws IOException {
        // Given: A simple Java class file
        String javaCode = """
            package com.example;
            
            public class SimpleClass {
                private String name;
                
                public String getName() {
                    return name;
                }
                
                public void setName(String name) {
                    this.name = name;
                }
            }
            """;

        File javaFile = createJavaFile("SimpleClass.java", javaCode);

        // When: Parsing the file
        ClassNode classNode = parser.parseFile(javaFile);

        // Then: Class information is extracted correctly
        assertThat(classNode).isNotNull();
        assertThat(classNode.getId()).isEqualTo("com.example.SimpleClass");
        assertThat(classNode.getName()).isEqualTo("SimpleClass");
        assertThat(classNode.getPackageName()).isEqualTo("com.example");
        assertThat(classNode.getType()).isEqualTo(ClassType.CLASS);
        assertThat(classNode.isPublic()).isTrue();
        assertThat(classNode.getMethods()).hasSize(2);
        assertThat(classNode.getFields()).hasSize(1);
    }

    @Test
    void shouldParseInterface() throws IOException {
        // Given: An interface file
        String javaCode = """
            package com.example;
            
            public interface MyInterface {
                void doSomething();
                String getValue();
            }
            """;

        File javaFile = createJavaFile("MyInterface.java", javaCode);

        // When: Parsing the file
        ClassNode classNode = parser.parseFile(javaFile);

        // Then: Interface is identified correctly
        assertThat(classNode).isNotNull();
        assertThat(classNode.getType()).isEqualTo(ClassType.INTERFACE);
        assertThat(classNode.getMethods()).hasSize(2);
    }

    @Test
    void shouldParseEnum() throws IOException {
        // Given: An enum file
        String javaCode = """
            package com.example;
            
            public enum Status {
                ACTIVE, INACTIVE, PENDING
            }
            """;

        File javaFile = createJavaFile("Status.java", javaCode);

        // When: Parsing the file
        ClassNode classNode = parser.parseFile(javaFile);

        // Then: Enum is identified correctly
        assertThat(classNode).isNotNull();
        assertThat(classNode.getType()).isEqualTo(ClassType.ENUM);
    }

    @Test
    void shouldParseDirectoryRecursively() throws IOException {
        // Given: Multiple Java files in a directory structure
        createJavaFile("ClassA.java", "package com.example; public class ClassA {}");

        Path subDir = tempDir.resolve("sub");
        Files.createDirectories(subDir);
        Files.writeString(subDir.resolve("ClassB.java"),
            "package com.example.sub; public class ClassB {}");

        // When: Parsing the directory
        List<ClassNode> classes = parser.parseDirectory(tempDir.toFile());

        // Then: All classes are found
        assertThat(classes).hasSize(2);
        assertThat(classes).extracting(ClassNode::getName)
            .containsExactlyInAnyOrder("ClassA", "ClassB");
    }

    @Test
    void shouldHandleParsingErrors() throws IOException {
        // Given: A directory with valid and invalid Java files
        createJavaFile("Valid.java", "package com.example; public class Valid {}");
        createJavaFile("Invalid.java", "this is not valid Java code @#$%");

        // When: Parsing the directory
        List<ClassNode> classes = parser.parseDirectory(tempDir.toFile());

        // Then: Valid file is parsed, invalid is skipped
        assertThat(classes).hasSize(1);
        assertThat(classes.get(0).getName()).isEqualTo("Valid");
    }

    private File createJavaFile(String fileName, String content) throws IOException {
        Path filePath = tempDir.resolve(fileName);
        Files.writeString(filePath, content);
        return filePath.toFile();
    }
}

