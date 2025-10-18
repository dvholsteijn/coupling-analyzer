package nl.dvholsteijn.couplinganalyzer.graph;

import nl.dvholsteijn.couplinganalyzer.model.ClassNode;
import nl.dvholsteijn.couplinganalyzer.model.CouplingGraph;
import nl.dvholsteijn.couplinganalyzer.model.CouplingMetrics;
import nl.dvholsteijn.couplinganalyzer.model.Dependency;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculates coupling metrics from a dependency graph.
 */
public class MetricsCalculator {

    /**
     * Calculate coupling metrics for the given graph.
     *
     * @param graph the coupling graph
     * @return calculated metrics
     */
    public CouplingMetrics calculateMetrics(CouplingGraph graph) {
        CouplingMetrics metrics = new CouplingMetrics();

        List<ClassNode> classes = graph.getClasses();
        List<Dependency> dependencies = graph.getDependencies();

        // Basic counts
        metrics.setTotalClasses(classes.size());
        metrics.setTotalDependencies(dependencies.size());

        // Average dependencies per class
        if (!classes.isEmpty()) {
            double average = (double) dependencies.size() / classes.size();
            metrics.setAverageDependenciesPerClass(Math.round(average * 100.0) / 100.0);
        }

        // Most coupled classes
        Map<String, Integer> couplingCounts = calculateCouplingCounts(dependencies);
        Map<String, Integer> topCoupled = couplingCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        metrics.setMostCoupledClasses(topCoupled);

        // Detect circular dependencies
        List<List<String>> cycles = detectCircularDependencies(graph);
        metrics.setCircularDependencies(cycles);

        return metrics;
    }

    private Map<String, Integer> calculateCouplingCounts(List<Dependency> dependencies) {
        Map<String, Integer> counts = new HashMap<>();

        for (Dependency dep : dependencies) {
            counts.merge(dep.getSourceId(), 1, Integer::sum);
            counts.merge(dep.getTargetId(), 1, Integer::sum);
        }

        return counts;
    }

    private List<List<String>> detectCircularDependencies(CouplingGraph graph) {
        List<List<String>> cycles = new ArrayList<>();

        // Build adjacency list
        Map<String, Set<String>> adjacencyList = new HashMap<>();
        for (Dependency dep : graph.getDependencies()) {
            adjacencyList.computeIfAbsent(dep.getSourceId(), k -> new HashSet<>())
                    .add(dep.getTargetId());
        }

        // Use DFS to detect cycles
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        List<String> currentPath = new ArrayList<>();

        for (String classId : adjacencyList.keySet()) {
            if (!visited.contains(classId)) {
                findCycles(classId, adjacencyList, visited, recursionStack, currentPath, cycles);
            }
        }

        return cycles;
    }

    private void findCycles(String node, Map<String, Set<String>> adjacencyList,
                           Set<String> visited, Set<String> recursionStack,
                           List<String> currentPath, List<List<String>> cycles) {

        visited.add(node);
        recursionStack.add(node);
        currentPath.add(node);

        Set<String> neighbors = adjacencyList.getOrDefault(node, Collections.emptySet());

        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                findCycles(neighbor, adjacencyList, visited, recursionStack, currentPath, cycles);
            } else if (recursionStack.contains(neighbor)) {
                // Found a cycle
                int cycleStart = currentPath.indexOf(neighbor);
                if (cycleStart >= 0) {
                    List<String> cycle = new ArrayList<>(currentPath.subList(cycleStart, currentPath.size()));
                    cycle.add(neighbor); // Close the cycle

                    // Only add if we haven't seen this cycle before (in any rotation)
                    if (!containsCycle(cycles, cycle)) {
                        cycles.add(cycle);
                    }
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
        recursionStack.remove(node);
    }

    private boolean containsCycle(List<List<String>> cycles, List<String> newCycle) {
        for (List<String> existingCycle : cycles) {
            if (isSameCycle(existingCycle, newCycle)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSameCycle(List<String> cycle1, List<String> cycle2) {
        if (cycle1.size() != cycle2.size()) {
            return false;
        }

        // Check all rotations
        String combined = String.join(",", cycle2) + "," + String.join(",", cycle2);
        String cycle1Str = String.join(",", cycle1);

        return combined.contains(cycle1Str);
    }
}

