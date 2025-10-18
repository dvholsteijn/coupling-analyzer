# Coupling Analyzer Maven Plugin

A Maven plugin that helps analyze coupling and cohesion in your Java codebase to identify refactoring opportunities and improve modularization.

## Requirements

- Java 17 or higher
- Maven 3.9.x or higher

## Building the Plugin

To build and install the plugin locally:

```bash
mvn clean install
```

This will:
1. Compile the plugin
2. Run all unit tests
3. Install the plugin to your local Maven repository (~/.m2/repository)

## Usage

### Basic Configuration

Add the plugin to your project's `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>nl.dvholsteijn</groupId>
            <artifactId>coupling-analyzer-maven-plugin</artifactId>
            <version>0.1.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>analyze</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Running the Analysis

Execute the plugin during the verify phase:

```bash
mvn verify
```

Or run the plugin directly:

```bash
mvn coupling-analyzer:analyze
```

### Configuration Options

You can customize the plugin behavior with the following parameters:

```xml
<plugin>
    <groupId>nl.dvholsteijn</groupId>
    <artifactId>coupling-analyzer-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <configuration>
        <!-- Directory containing source files to analyze (default: src/main/java) -->
        <sourceDirectory>${project.build.sourceDirectory}</sourceDirectory>
        
        <!-- Directory where reports will be generated (default: target/coupling-analysis) -->
        <outputDirectory>${project.build.directory}/coupling-analysis</outputDirectory>
    </configuration>
</plugin>
```

## Output

The plugin generates a report at `target/coupling-analysis/coupling-report.txt` containing:
- Project information
- Analysis status
- Coupling metrics (coming in future releases)

## Development Status

**Current Version**: 0.1.0-SNAPSHOT

This is the initial release with basic plugin infrastructure. The plugin currently:
- ✅ Validates project structure
- ✅ Creates output directories
- ✅ Generates basic project information report

### Roadmap

Future releases will include:
- AST parsing of Java source files
- Dependency graph construction
- Coupling metrics calculation
- Cohesion analysis
- HTML/JSON report generation
- Integration with visualization tools

## Contributing

This project follows trunk-based development with:
- SOLID principles
- Unit test coverage (Given-When-Then pattern)
- Standard Java coding conventions

## License

See [LICENSE](LICENSE) file for details.

