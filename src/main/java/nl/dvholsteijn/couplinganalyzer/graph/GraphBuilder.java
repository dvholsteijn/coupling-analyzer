package nl.dvholsteijn.couplinganalyzer.graph;

import nl.dvholsteijn.couplinganalyzer.model.*;
import nl.dvholsteijn.couplinganalyzer.parser.DependencyExtractor;
import nl.dvholsteijn.couplinganalyzer.parser.JavaSourceParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds a coupling graph from parsed Java source files.
 */
public class GraphBuilder {

    private final JavaSourceParser parser;
    private final DependencyExtractor dependencyExtractor;
    private final MetricsCalculator metricsCalculator;

    public GraphBuilder(JavaSourceParser parser) {
        this.parser = parser;
        this.dependencyExtractor = new DependencyExtractor();
        this.metricsCalculator = new MetricsCalculator();
    }

    /**
     * Build a complete coupling graph from a source directory.
     *
     * @param projectName the project name
     * @param version the project version
     * @param sourceDirectory the source directory to analyze
     * @return the complete coupling graph
     * @throws IOException if there's an error reading files
     */
    public CouplingGraph buildGraph(String projectName, String version, File sourceDirectory) throws IOException {
        CouplingGraph graph = new CouplingGraph(projectName, version);
        graph.setSourceDirectory(sourceDirectory.getAbsolutePath());

        // Parse all classes
        List<ClassNode> classes = parser.parseDirectory(sourceDirectory);
        graph.setClasses(classes);

        // Build class ID to file mapping
        Map<String, File> classIdToFile = buildClassFileMap(classes);

        // Extract dependencies
        List<Dependency> allDependencies = new ArrayList<>();
        for (ClassNode classNode : classes) {
            File sourceFile = new File(classNode.getFilePath());
            if (sourceFile.exists()) {
                try {
                    List<Dependency> deps = dependencyExtractor.extractDependencies(sourceFile, classNode.getId());
                    allDependencies.addAll(deps);
                } catch (Exception e) {
                    System.err.println("Error extracting dependencies from " + classNode.getId() + ": " + e.getMessage());
                }
            }
        }

        // Consolidate dependencies (merge duplicates and calculate weights)
        List<Dependency> consolidatedDeps = consolidateDependencies(allDependencies);
        graph.setDependencies(consolidatedDeps);

        // Calculate metrics
        CouplingMetrics metrics = metricsCalculator.calculateMetrics(graph);
        graph.setMetrics(metrics);

        // Add metadata
        graph.getMetadata().put("totalFiles", classes.size());
        graph.getMetadata().put("analysisComplete", true);

        return graph;
    }

    private Map<String, File> buildClassFileMap(List<ClassNode> classes) {
        Map<String, File> map = new HashMap<>();
        for (ClassNode classNode : classes) {
            map.put(classNode.getId(), new File(classNode.getFilePath()));
        }
        return map;
    }

    private List<Dependency> consolidateDependencies(List<Dependency> dependencies) {
        Map<String, Dependency> dependencyMap = new HashMap<>();

        for (Dependency dep : dependencies) {
            String key = dep.getSourceId() + "->" + dep.getTargetId() + ":" + dep.getType();

            if (dependencyMap.containsKey(key)) {
                dependencyMap.get(key).incrementWeight();
            } else {
                dependencyMap.put(key, dep);
            }
        }

        return new ArrayList<>(dependencyMap.values());
    }
}

