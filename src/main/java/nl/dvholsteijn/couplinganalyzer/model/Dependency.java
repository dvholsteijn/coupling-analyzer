package nl.dvholsteijn.couplinganalyzer.model;

import java.util.Objects;

/**
 * Represents a dependency relationship between two classes.
 */
public class Dependency {
    private String sourceId;        // Source class FQN
    private String targetId;        // Target class FQN
    private DependencyType type;
    private String location;        // Where in code (file:line)
    private int weight;             // Frequency/strength of coupling

    public Dependency() {
        this.weight = 1;
    }

    public Dependency(String sourceId, String targetId, DependencyType type) {
        this();
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.type = type;
    }

    // Getters and setters
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public DependencyType getType() {
        return type;
    }

    public void setType(DependencyType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void incrementWeight() {
        this.weight++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return Objects.equals(sourceId, that.sourceId) &&
                Objects.equals(targetId, that.targetId) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId, targetId, type);
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "sourceId='" + sourceId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", type=" + type +
                ", weight=" + weight +
                '}';
    }
}

