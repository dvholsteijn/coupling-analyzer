package nl.dvholsteijn.couplinganalyzer.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyTest {

    @Test
    void shouldCreateDependencyWithBasicInfo() {
        // Given: Basic dependency information
        String sourceId = "com.example.ClassA";
        String targetId = "com.example.ClassB";
        DependencyType type = DependencyType.USES;

        // When: Creating a Dependency
        Dependency dependency = new Dependency(sourceId, targetId, type);

        // Then: All properties are set correctly
        assertThat(dependency.getSourceId()).isEqualTo(sourceId);
        assertThat(dependency.getTargetId()).isEqualTo(targetId);
        assertThat(dependency.getType()).isEqualTo(type);
        assertThat(dependency.getWeight()).isEqualTo(1);
    }

    @Test
    void shouldIncrementWeight() {
        // Given: A dependency with initial weight
        Dependency dependency = new Dependency("A", "B", DependencyType.USES);

        // When: Incrementing weight multiple times
        dependency.incrementWeight();
        dependency.incrementWeight();

        // Then: Weight is incremented correctly
        assertThat(dependency.getWeight()).isEqualTo(3);
    }

    @Test
    void shouldSupportEquality() {
        // Given: Two dependencies with same source, target, and type
        Dependency dep1 = new Dependency("A", "B", DependencyType.USES);
        Dependency dep2 = new Dependency("A", "B", DependencyType.USES);

        // When: Comparing them
        // Then: They should be equal
        assertThat(dep1).isEqualTo(dep2);
        assertThat(dep1.hashCode()).isEqualTo(dep2.hashCode());
    }

    @Test
    void shouldDifferByType() {
        // Given: Two dependencies with different types
        Dependency dep1 = new Dependency("A", "B", DependencyType.USES);
        Dependency dep2 = new Dependency("A", "B", DependencyType.EXTENDS);

        // When: Comparing them
        // Then: They should not be equal
        assertThat(dep1).isNotEqualTo(dep2);
    }
}

