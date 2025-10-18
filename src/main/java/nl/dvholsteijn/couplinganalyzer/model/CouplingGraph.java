package nl.dvholsteijn.couplinganalyzer.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Root container for the coupling analysis graph.
 * This is the main data structure that will be serialized to JSON for AI agents.
 */
public class CouplingGraph {
    private String projectName;
    private String version;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime generatedAt;

    private String sourceDirectory;
    private List<ClassNode> classes;
    private List<Dependency> dependencies;
    private CouplingMetrics metrics;
    private Map<String, Object> metadata;
    private String aiAnalysisPrompt;

    public CouplingGraph() {
        this.classes = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.generatedAt = LocalDateTime.now();
        this.aiAnalysisPrompt = "This graph represents the coupling structure of a Java project. " +
                "Analyze for: 1) Classes with high coupling (>5 dependencies), " +
                "2) Circular dependencies, 3) Potential service boundaries, " +
                "4) Candidates for extraction into separate modules.";
    }

    public CouplingGraph(String projectName, String version) {
        this();
        this.projectName = projectName;
        this.version = version;
    }

    // Getters and setters
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public List<ClassNode> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassNode> classes) {
        this.classes = classes;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public CouplingMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(CouplingMetrics metrics) {
        this.metrics = metrics;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getAiAnalysisPrompt() {
        return aiAnalysisPrompt;
    }

    public void setAiAnalysisPrompt(String aiAnalysisPrompt) {
        this.aiAnalysisPrompt = aiAnalysisPrompt;
    }

    @Override
    public String toString() {
        return "CouplingGraph{" +
                "projectName='" + projectName + '\'' +
                ", version='" + version + '\'' +
                ", classes=" + classes.size() +
                ", dependencies=" + dependencies.size() +
                '}';
    }
}

