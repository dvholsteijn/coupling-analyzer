package nl.dvholsteijn.couplinganalyzer.model;

import java.util.*;

/**
 * Container for coupling metrics calculated from the dependency graph.
 */
public class CouplingMetrics {
    private int totalClasses;
    private int totalDependencies;
    private double averageDependenciesPerClass;
    private Map<String, Integer> mostCoupledClasses;
    private List<List<String>> circularDependencies;

    public CouplingMetrics() {
        this.mostCoupledClasses = new LinkedHashMap<>();
        this.circularDependencies = new ArrayList<>();
    }

    // Getters and setters
    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getTotalDependencies() {
        return totalDependencies;
    }

    public void setTotalDependencies(int totalDependencies) {
        this.totalDependencies = totalDependencies;
    }

    public double getAverageDependenciesPerClass() {
        return averageDependenciesPerClass;
    }

    public void setAverageDependenciesPerClass(double averageDependenciesPerClass) {
        this.averageDependenciesPerClass = averageDependenciesPerClass;
    }

    public Map<String, Integer> getMostCoupledClasses() {
        return mostCoupledClasses;
    }

    public void setMostCoupledClasses(Map<String, Integer> mostCoupledClasses) {
        this.mostCoupledClasses = mostCoupledClasses;
    }

    public List<List<String>> getCircularDependencies() {
        return circularDependencies;
    }

    public void setCircularDependencies(List<List<String>> circularDependencies) {
        this.circularDependencies = circularDependencies;
    }

    @Override
    public String toString() {
        return "CouplingMetrics{" +
                "totalClasses=" + totalClasses +
                ", totalDependencies=" + totalDependencies +
                ", averageDependenciesPerClass=" + averageDependenciesPerClass +
                ", circularDependencies=" + circularDependencies.size() +
                '}';
    }
}

