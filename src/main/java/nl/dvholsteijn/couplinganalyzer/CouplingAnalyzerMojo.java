package nl.dvholsteijn.couplinganalyzer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Analyzes coupling and cohesion in a Maven project.
 */
@Mojo(name = "analyze", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class CouplingAnalyzerMojo extends AbstractMojo {

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Directory where analysis reports will be written.
     */
    @Parameter(defaultValue = "${project.build.directory}/coupling-analysis", property = "coupling.outputDirectory")
    private File outputDirectory;

    /**
     * Source directory to analyze.
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}", property = "coupling.sourceDirectory")
    private File sourceDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting Coupling Analyzer...");
        getLog().info("Project: " + project.getName());
        getLog().info("Version: " + project.getVersion());

        validateSourceDirectory();
        createOutputDirectory();
        generateReport();

        getLog().info("Coupling analysis completed successfully!");
    }

    private void validateSourceDirectory() throws MojoFailureException {
        if (sourceDirectory == null || !sourceDirectory.exists()) {
            throw new MojoFailureException(
                "Source directory does not exist: " + sourceDirectory
            );
        }

        if (!sourceDirectory.isDirectory()) {
            throw new MojoFailureException(
                "Source path is not a directory: " + sourceDirectory
            );
        }

        getLog().info("Source directory: " + sourceDirectory.getAbsolutePath());
    }

    private void createOutputDirectory() throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new MojoExecutionException(
                    "Failed to create output directory: " + outputDirectory
                );
            }
        }
        getLog().info("Output directory: " + outputDirectory.getAbsolutePath());
    }

    private void generateReport() throws MojoExecutionException {
        Path reportPath = outputDirectory.toPath().resolve("coupling-report.txt");

        try {
            StringBuilder report = new StringBuilder();
            report.append("Coupling Analysis Report\n");
            report.append("========================\n\n");
            report.append("Project: ").append(project.getName()).append("\n");
            report.append("Version: ").append(project.getVersion()).append("\n");
            report.append("Source Directory: ").append(sourceDirectory.getAbsolutePath()).append("\n");
            report.append("\n");
            report.append("Status: Analysis framework initialized successfully.\n");
            report.append("Note: Detailed coupling analysis will be implemented in future iterations.\n");

            Files.writeString(reportPath, report.toString());
            getLog().info("Report generated: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write report", e);
        }
    }

    // Getters for testing
    void setProject(MavenProject project) {
        this.project = project;
    }

    void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    File getOutputDirectory() {
        return outputDirectory;
    }

    File getSourceDirectory() {
        return sourceDirectory;
    }
}

