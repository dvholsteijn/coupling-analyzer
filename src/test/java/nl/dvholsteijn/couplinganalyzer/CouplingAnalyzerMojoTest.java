package nl.dvholsteijn.couplinganalyzer;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CouplingAnalyzerMojoTest {

    @TempDir
    Path tempDir;

    private CouplingAnalyzerMojo mojo;
    private MavenProject project;

    @BeforeEach
    void setUp() {
        // Given: A fresh mojo instance for each test
        mojo = new CouplingAnalyzerMojo();
        project = new MavenProject();
        project.setName("test-project");
        project.setVersion("1.0.0-SNAPSHOT");
        mojo.setProject(project);
    }

    @Test
    void shouldInitializeMojoSuccessfully() {
        // Given: A mojo with project set
        // When: Getting the mojo state
        // Then: Mojo should be properly initialized
        assertNotNull(mojo);
    }

    @Test
    void shouldExecuteWithValidProjectStructure() throws Exception {
        // Given: Valid source and output directories
        Path sourceDir = tempDir.resolve("src/main/java");
        Files.createDirectories(sourceDir);
        Path outputDir = tempDir.resolve("target/coupling-analysis");

        mojo.setSourceDirectory(sourceDir.toFile());
        mojo.setOutputDirectory(outputDir.toFile());

        // When: Executing the mojo
        mojo.execute();

        // Then: Output directory and reports should be created
        assertTrue(outputDir.toFile().exists());
        assertTrue(outputDir.resolve("coupling-report.txt").toFile().exists());
        assertTrue(outputDir.resolve("coupling-graph.json").toFile().exists());

        String reportContent = Files.readString(outputDir.resolve("coupling-report.txt"));
        assertTrue(reportContent.contains("test-project"));
        assertTrue(reportContent.contains("1.0.0-SNAPSHOT"));
        assertTrue(reportContent.contains("Summary"));

        String jsonContent = Files.readString(outputDir.resolve("coupling-graph.json"));
        assertTrue(jsonContent.contains("test-project"));
        assertTrue(jsonContent.contains("\"classes\""));
    }

    @Test
    void shouldFailWhenSourceDirectoryDoesNotExist() {
        // Given: A non-existent source directory
        File nonExistentDir = tempDir.resolve("non-existent").toFile();
        mojo.setSourceDirectory(nonExistentDir);
        mojo.setOutputDirectory(tempDir.resolve("output").toFile());

        // When: Executing the mojo
        // Then: Should throw MojoFailureException
        MojoFailureException exception = assertThrows(
            MojoFailureException.class,
            () -> mojo.execute()
        );

        assertTrue(exception.getMessage().contains("Source directory does not exist"));
    }

    @Test
    void shouldFailWhenSourceDirectoryIsFile() throws Exception {
        // Given: Source directory is actually a file
        Path sourceFile = tempDir.resolve("source.txt");
        Files.createFile(sourceFile);
        mojo.setSourceDirectory(sourceFile.toFile());
        mojo.setOutputDirectory(tempDir.resolve("output").toFile());

        // When: Executing the mojo
        // Then: Should throw MojoFailureException
        MojoFailureException exception = assertThrows(
            MojoFailureException.class,
            () -> mojo.execute()
        );

        assertTrue(exception.getMessage().contains("Source path is not a directory"));
    }

    @Test
    void shouldCreateOutputDirectoryIfNotExists() throws Exception {
        // Given: Valid source directory and non-existent output directory
        Path sourceDir = tempDir.resolve("src");
        Files.createDirectories(sourceDir);
        Path outputDir = tempDir.resolve("target/coupling-analysis");

        mojo.setSourceDirectory(sourceDir.toFile());
        mojo.setOutputDirectory(outputDir.toFile());

        // When: Executing the mojo
        mojo.execute();

        // Then: Output directory should be created
        assertTrue(outputDir.toFile().exists());
        assertTrue(outputDir.toFile().isDirectory());
    }

    @Test
    void shouldGenerateReportWithCorrectContent() throws Exception {
        // Given: Valid project structure
        Path sourceDir = tempDir.resolve("src");
        Files.createDirectories(sourceDir);
        Path outputDir = tempDir.resolve("output");

        mojo.setSourceDirectory(sourceDir.toFile());
        mojo.setOutputDirectory(outputDir.toFile());

        // When: Executing the mojo
        mojo.execute();

        // Then: Report should contain expected information
        Path reportPath = outputDir.resolve("coupling-report.txt");
        assertTrue(Files.exists(reportPath));

        String content = Files.readString(reportPath);
        assertAll(
            () -> assertTrue(content.contains("Coupling Analysis Report")),
            () -> assertTrue(content.contains("test-project")),
            () -> assertTrue(content.contains("1.0.0-SNAPSHOT")),
            () -> assertTrue(content.contains("Summary")),
            () -> assertTrue(content.contains("Total Classes:"))
        );

        // Then: JSON report should also be generated
        Path jsonPath = outputDir.resolve("coupling-graph.json");
        assertTrue(Files.exists(jsonPath));
        String jsonContent = Files.readString(jsonPath);
        assertTrue(jsonContent.contains("projectName"));
    }
}

