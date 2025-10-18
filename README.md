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

The plugin generates two types of reports in the `target/coupling-analysis/` directory:

### 1. JSON Graph (`coupling-graph.json`)

A structured JSON file optimized for AI coding agents and programmatic analysis. Contains:
- **Classes**: Complete class information (name, package, methods, fields, line count)
- **Dependencies**: Typed relationships between classes (EXTENDS, IMPLEMENTS, USES, CALLS, etc.)
- **Metrics**: Calculated coupling metrics, circular dependencies, most coupled classes
- **AI Analysis Prompt**: Contextual information for AI agents

**Example JSON Structure**:
```json
{
  "projectName": "my-project",
  "version": "1.0.0",
  "generatedAt": "2025-10-18T20:00:00",
  "classes": [
    {
      "id": "com.example.UserService",
      "name": "UserService",
      "packageName": "com.example",
      "type": "CLASS",
      "methods": ["getUser(Long):User", "createUser(UserDto):User"],
      "fields": ["userRepository:UserRepository"]
    }
  ],
  "dependencies": [
    {
      "sourceId": "com.example.UserService",
      "targetId": "com.example.UserRepository",
      "type": "USES",
      "weight": 5
    }
  ],
  "metrics": {
    "totalClasses": 42,
    "totalDependencies": 156,
    "averageDependenciesPerClass": 3.71,
    "mostCoupledClasses": {
      "com.example.UserService": 12
    },
    "circularDependencies": []
  }
}
```

### 2. Text Report (`coupling-report.txt`)

A human-readable summary containing:
- Project information
- Summary statistics
- Most coupled classes
- Circular dependencies (if any)

## Using with AI Coding Agents

The JSON graph format is specifically designed for AI analysis. Example usage:

```bash
# Generate the coupling graph
mvn coupling-analyzer:analyze

# Use with an AI agent
cat target/coupling-analysis/coupling-graph.json | ai-agent analyze-coupling
```

AI agents can use this data to:
- Identify classes with high coupling (>5 dependencies)
- Detect circular dependencies
- Suggest refactoring boundaries
- Propose module extraction strategies
- Recommend microservice boundaries

## Development Status

**Current Version**: 0.1.0-SNAPSHOT

The plugin currently provides:
- ✅ AST parsing of Java source files (using JavaParser)
- ✅ Complete dependency graph construction
- ✅ Coupling metrics calculation
- ✅ Circular dependency detection
- ✅ JSON export for AI analysis
- ✅ Human-readable text reports

### Roadmap

Future releases will include:
- **Spring Framework Analysis**: Detect @Service, @Controller, @Repository annotations
- **Package-level Analysis**: Group classes by package and analyze package coupling
- **Temporal Coupling**: Integrate with Git history to find classes that change together
- **Visualization**: Generate HTML with interactive graphs
- **Comparison**: Compare graphs over time to track coupling trends
- **Custom Rules**: Allow users to define coupling rules and violations

## Contributing

This project follows trunk-based development with:
- SOLID principles
- Unit test coverage (Given-When-Then pattern)
- Standard Java coding conventions

## License

See [LICENSE](LICENSE) file for details.

