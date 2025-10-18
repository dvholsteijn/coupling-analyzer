package nl.dvholsteijn.couplinganalyzer.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.dvholsteijn.couplinganalyzer.model.CouplingGraph;

import java.io.File;
import java.io.IOException;

/**
 * Exports coupling graphs to JSON format using Jackson.
 */
public class JsonGraphExporter implements GraphExporter {

    private final ObjectMapper objectMapper;

    public JsonGraphExporter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void export(CouplingGraph graph, File outputFile) throws IOException {
        objectMapper.writeValue(outputFile, graph);
    }

    @Override
    public String exportToString(CouplingGraph graph) throws IOException {
        return objectMapper.writeValueAsString(graph);
    }
}

