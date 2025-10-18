package nl.dvholsteijn.couplinganalyzer.parser;

import nl.dvholsteijn.couplinganalyzer.model.ClassNode;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface for parsing Java source files and extracting class information.
 */
public interface JavaSourceParser {

    /**
     * Parse all Java files in the given directory recursively.
     *
     * @param sourceDirectory the root directory containing Java source files
     * @return list of parsed class nodes
     * @throws IOException if there's an error reading files
     */
    List<ClassNode> parseDirectory(File sourceDirectory) throws IOException;

    /**
     * Parse a single Java source file.
     *
     * @param sourceFile the Java source file to parse
     * @return the parsed class node, or null if parsing fails
     * @throws IOException if there's an error reading the file
     */
    ClassNode parseFile(File sourceFile) throws IOException;
}

