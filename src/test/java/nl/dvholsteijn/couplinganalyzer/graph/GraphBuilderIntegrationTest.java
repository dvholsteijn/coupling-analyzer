package nl.dvholsteijn.couplinganalyzer.graph;

import nl.dvholsteijn.couplinganalyzer.model.CouplingGraph;
import nl.dvholsteijn.couplinganalyzer.model.CouplingMetrics;
import nl.dvholsteijn.couplinganalyzer.parser.JavaParserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the complete graph building process.
 */
class GraphBuilderIntegrationTest {

    private GraphBuilder builder;

    @BeforeEach
    void setUp() {
        // Given: A graph builder with JavaParser
        builder = new GraphBuilder(new JavaParserImpl());
    }

    @Test
    void shouldBuildGraphFromSampleProject() throws IOException, URISyntaxException {
        // Given: The sample project directory
        File sampleDir = new File("src/test/resources/sample-project");
        
        if (!sampleDir.exists()) {
            // Skip test if sample directory doesn't exist
            return;
        }

        // When: Building the graph
        CouplingGraph graph = builder.buildGraph("sample-project", "1.0.0", sampleDir);

        // Then: Graph contains expected data
        assertThat(graph).isNotNull();
        assertThat(graph.getProjectName()).isEqualTo("sample-project");
        assertThat(graph.getVersion()).isEqualTo("1.0.0");
        assertThat(graph.getClasses()).isNotEmpty();
        assertThat(graph.getGeneratedAt()).isNotNull();
        
        // Verify metrics are calculated
        CouplingMetrics metrics = graph.getMetrics();
        assertThat(metrics).isNotNull();
        assertThat(metrics.getTotalClasses()).isEqualTo(graph.getClasses().size());
        assertThat(metrics.getTotalDependencies()).isEqualTo(graph.getDependencies().size());
    }

    @Test
    void shouldHandleEmptyDirectory() throws IOException {
        // Given: A temporary empty directory
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "empty-test-" + System.currentTimeMillis());
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        // When: Building graph from empty directory
        CouplingGraph graph = builder.buildGraph("empty-project", "1.0.0", tempDir);

        // Then: Graph is created with no classes
        assertThat(graph.getClasses()).isEmpty();
        assertThat(graph.getDependencies()).isEmpty();
        assertThat(graph.getMetrics().getTotalClasses()).isZero();
    }
}

