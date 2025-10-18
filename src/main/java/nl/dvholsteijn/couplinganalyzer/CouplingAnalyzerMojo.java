package nl.dvholsteijn.couplinganalyzer;

import nl.dvholsteijn.couplinganalyzer.export.JsonGraphExporter;
import nl.dvholsteijn.couplinganalyzer.graph.GraphBuilder;
import nl.dvholsteijn.couplinganalyzer.model.CouplingGraph;
import nl.dvholsteijn.couplinganalyzer.parser.JavaParserImpl;
import nl.dvholsteijn.couplinganalyzer.parser.JavaSourceParser;
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
import java.util.List;

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

    /**
     * Output format for the analysis report.
     */
    @Parameter(defaultValue = "BOTH", property = "coupling.outputFormat")
    private String outputFormat;

    /**
     * Whether to include detailed metrics in the output.
     */
    @Parameter(defaultValue = "true", property = "coupling.includeMetrics")
    private boolean includeMetrics;

    /**
     * Packages to exclude from analysis.
     */
    @Parameter(property = "coupling.excludePackages")
    private List<String> excludePackages;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting Coupling Analyzer...");
        getLog().info("Project: " + project.getName());
        getLog().info("Version: " + project.getVersion());

        validateSourceDirectory();
        createOutputDirectory();

        CouplingGraph graph = analyzeProject();

        if (shouldGenerateJson()) {
            generateJsonReport(graph);
        }

        if (shouldGenerateText()) {
            generateTextReport(graph);
        }

        logSummary(graph);
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

    private CouplingGraph analyzeProject() throws MojoExecutionException {
        try {
            getLog().info("Parsing Java source files...");

            JavaSourceParser parser = new JavaParserImpl();
            GraphBuilder builder = new GraphBuilder(parser);

            CouplingGraph graph = builder.buildGraph(
                project.getName(),
                project.getVersion(),
                sourceDirectory
            );

            getLog().info("Found " + graph.getClasses().size() + " classes, " +
                         graph.getDependencies().size() + " dependencies");

            return graph;
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to analyze project", e);
        }
    }

    private void generateJsonReport(CouplingGraph graph) throws MojoExecutionException {
        Path jsonPath = outputDirectory.toPath().resolve("coupling-graph.json");

        try {
            JsonGraphExporter exporter = new JsonGraphExporter();
            exporter.export(graph, jsonPath.toFile());
            getLog().info("JSON report generated: " + jsonPath.toAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write JSON report", e);
        }
    }

    private void generateTextReport(CouplingGraph graph) throws MojoExecutionException {
        Path reportPath = outputDirectory.toPath().resolve("coupling-report.txt");

        try {
            StringBuilder report = new StringBuilder();
            report.append("Coupling Analysis Report\n");
            report.append("========================\n\n");
            report.append("Project: ").append(graph.getProjectName()).append("\n");
            report.append("Version: ").append(graph.getVersion()).append("\n");
            report.append("Generated: ").append(graph.getGeneratedAt()).append("\n");
            report.append("Source Directory: ").append(graph.getSourceDirectory()).append("\n");
            report.append("\n");

            if (graph.getMetrics() != null) {
                report.append("Summary\n");
                report.append("-------\n");
                report.append("Total Classes: ").append(graph.getMetrics().getTotalClasses()).append("\n");
                report.append("Total Dependencies: ").append(graph.getMetrics().getTotalDependencies()).append("\n");
                report.append("Average Dependencies per Class: ")
                      .append(String.format("%.2f", graph.getMetrics().getAverageDependenciesPerClass()))
                      .append("\n\n");

                if (!graph.getMetrics().getMostCoupledClasses().isEmpty()) {
                    report.append("Most Coupled Classes\n");
                    report.append("--------------------\n");
                    graph.getMetrics().getMostCoupledClasses().forEach((className, count) ->
                        report.append("  - ").append(className).append(" (").append(count).append(" dependencies)\n")
                    );
                    report.append("\n");
                }

                if (!graph.getMetrics().getCircularDependencies().isEmpty()) {
                    report.append("Circular Dependencies Detected: ")
                          .append(graph.getMetrics().getCircularDependencies().size()).append("\n");
                    report.append("-------------------------------\n");
                    for (List<String> cycle : graph.getMetrics().getCircularDependencies()) {
                        report.append("  ").append(String.join(" -> ", cycle)).append("\n");
                    }
                }
            }

            Files.writeString(reportPath, report.toString());
            getLog().info("Text report generated: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write text report", e);
        }
    }

    private void logSummary(CouplingGraph graph) {
        if (graph.getMetrics() != null) {
            getLog().info("Analysis Summary:");
            getLog().info("  Total Classes: " + graph.getMetrics().getTotalClasses());
            getLog().info("  Total Dependencies: " + graph.getMetrics().getTotalDependencies());

            if (!graph.getMetrics().getMostCoupledClasses().isEmpty()) {
                getLog().info("  Most coupled classes:");
                graph.getMetrics().getMostCoupledClasses().entrySet().stream()
                    .limit(3)
                    .forEach(entry -> getLog().info("    - " + entry.getKey() + " (" + entry.getValue() + " dependencies)"));
            }

            if (!graph.getMetrics().getCircularDependencies().isEmpty()) {
                getLog().warn("  Circular dependencies detected: " + graph.getMetrics().getCircularDependencies().size());
            }
        }
    }

    private boolean shouldGenerateJson() {
        String format = outputFormat != null ? outputFormat : "BOTH";
        return "JSON".equalsIgnoreCase(format) || "BOTH".equalsIgnoreCase(format);
    }

    private boolean shouldGenerateText() {
        String format = outputFormat != null ? outputFormat : "BOTH";
        return "TEXT".equalsIgnoreCase(format) || "BOTH".equalsIgnoreCase(format);
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

