package nl.dvholsteijn.couplinganalyzer.export;

import nl.dvholsteijn.couplinganalyzer.model.CouplingGraph;

import java.io.File;
import java.io.IOException;

/**
 * Interface for exporting coupling graphs to various formats.
 */
public interface GraphExporter {

    /**
     * Export the coupling graph to a file.
     *
     * @param graph the coupling graph to export
     * @param outputFile the output file
     * @throws IOException if there's an error writing the file
     */
    void export(CouplingGraph graph, File outputFile) throws IOException;

    /**
     * Export the coupling graph to a string.
     *
     * @param graph the coupling graph to export
     * @return the exported string representation
     * @throws IOException if there's an error during serialization
     */
    String exportToString(CouplingGraph graph) throws IOException;
}

