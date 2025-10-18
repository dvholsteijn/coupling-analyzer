package nl.dvholsteijn.couplinganalyzer.export;

import nl.dvholsteijn.couplinganalyzer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class JsonGraphExporterTest {

    @TempDir
    Path tempDir;

    private JsonGraphExporter exporter;

    @BeforeEach
    void setUp() {
        // Given: A JSON exporter instance
        exporter = new JsonGraphExporter();
    }

    @Test
    void shouldExportEmptyGraph() throws IOException {
        // Given: An empty coupling graph
        CouplingGraph graph = new CouplingGraph("test-project", "1.0.0");

        // When: Exporting to JSON
        String json = exporter.exportToString(graph);

        // Then: JSON contains basic project info
        assertThat(json).contains("test-project");
        assertThat(json).contains("1.0.0");
        assertThat(json).contains("classes");
        assertThat(json).contains("dependencies");
    }

    @Test
    void shouldExportGraphWithClasses() throws IOException {
        // Given: A graph with classes
        CouplingGraph graph = new CouplingGraph("test-project", "1.0.0");

        ClassNode classNode = new ClassNode("com.example.MyClass", "MyClass", "com.example");
        classNode.setType(ClassType.CLASS);
        classNode.setPublic(true);
        classNode.getMethods().add("doSomething():void");
        classNode.getFields().add("name:String");

        graph.getClasses().add(classNode);

        // When: Exporting to JSON
        String json = exporter.exportToString(graph);

        // Then: JSON contains class information
        assertThat(json).contains("MyClass");
        assertThat(json).contains("com.example");
        assertThat(json).contains("doSomething():void");
        assertThat(json).contains("name:String");
    }

    @Test
    void shouldExportGraphWithDependencies() throws IOException {
        // Given: A graph with dependencies
        CouplingGraph graph = new CouplingGraph("test-project", "1.0.0");

        Dependency dep = new Dependency("com.example.A", "com.example.B", DependencyType.USES);
        dep.setWeight(3);
        graph.getDependencies().add(dep);

        // When: Exporting to JSON
        String json = exporter.exportToString(graph);

        // Then: JSON contains dependency information
        assertThat(json).contains("com.example.A");
        assertThat(json).contains("com.example.B");
        assertThat(json).contains("USES");
        assertThat(json).contains("\"weight\" : 3");
    }

    @Test
    void shouldExportToFile() throws IOException {
        // Given: A graph and output file
        CouplingGraph graph = new CouplingGraph("test-project", "1.0.0");
        File outputFile = tempDir.resolve("coupling-graph.json").toFile();

        // When: Exporting to file
        exporter.export(graph, outputFile);

        // Then: File is created and contains JSON
        assertThat(outputFile).exists();
        String content = Files.readString(outputFile.toPath());
        assertThat(content).contains("test-project");
    }
}

