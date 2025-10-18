# Proposal: Coupling Graph Generator for AI Analysis

**Status**: Proposed  
**Created**: 2025-10-18  
**Type**: Feature

## Summary

Implement a minimal coupling graph generator that analyzes Java source code and produces a structured graph representation in JSON format, optimized for AI coding agents to analyze coupling patterns and suggest refactoring opportunities.

## Context

AI coding agents need a clear, structured representation of code dependencies to effectively analyze coupling and cohesion. The current Maven plugin provides basic infrastructure but lacks the core analysis capability. This proposal adds a graph generator that:

1. Parses Java source files using AST (Abstract Syntax Tree)
2. Identifies classes, methods, and their dependencies
3. Generates a graph in JSON format that AI agents can easily consume
4. Provides metadata about coupling metrics

The output format is designed to be:
- **Human-readable** - Easy to inspect and debug
- **Machine-parsable** - Structured for programmatic analysis
- **LLM-friendly** - Contains contextual information for AI reasoning

## Proposed Changes

### 1. Core Graph Model

Create domain classes to represent the coupling graph:

**Package**: `nl.dvholsteijn.couplinganalyzer.model`

**Classes**:
- `CouplingGraph` - Root container for the entire graph
- `ClassNode` - Represents a Java class/interface
- `MethodNode` - Represents a method within a class
- `Dependency` - Represents a dependency relationship between nodes
- `DependencyType` - Enum (EXTENDS, IMPLEMENTS, USES, CALLS, FIELD_TYPE, PARAMETER_TYPE)
- `CouplingMetrics` - Container for calculated metrics

**Graph Structure**:
```java
CouplingGraph {
    String projectName;
    String version;
    List<ClassNode> classes;
    List<Dependency> dependencies;
    CouplingMetrics metrics;
    Map<String, Object> metadata;
}

ClassNode {
    String id;              // Fully qualified name
    String name;            // Simple name
    String packageName;
    String filePath;
    ClassType type;         // CLASS, INTERFACE, ENUM, ABSTRACT_CLASS
    List<String> methods;   // Method signatures
    List<String> fields;    // Field names and types
    int lineCount;
    boolean isPublic;
}

Dependency {
    String sourceId;        // Source class/method FQN
    String targetId;        // Target class/method FQN
    DependencyType type;
    String location;        // Where in code (line number)
    int weight;             // Frequency/strength of coupling
}

CouplingMetrics {
    int totalClasses;
    int totalDependencies;
    double averageDependenciesPerClass;
    Map<String, Integer> mostCoupledClasses;  // Top 10
    Map<String, List<String>> cohesionClusters;
}
```

### 2. Java Parser Implementation

**Package**: `nl.dvholsteijn.couplinganalyzer.parser`

Use **JavaParser** library (from javaparser.org) for AST parsing.

**Classes**:
- `JavaSourceParser` - Main parser interface
- `JavaParserImpl` - Implementation using JavaParser library
- `ClassVisitor` - Visitor pattern to extract class information
- `DependencyExtractor` - Extracts dependency relationships

**Capabilities**:
- Parse Java source files recursively from a directory
- Extract class declarations, methods, fields
- Identify import statements
- Detect method calls and field usages
- Handle inheritance and interface implementations
- Extract annotations (for future Spring analysis)

### 3. Graph Builder

**Package**: `nl.dvholsteijn.couplinganalyzer.graph`

**Classes**:
- `GraphBuilder` - Builds CouplingGraph from parsed data
- `DependencyResolver` - Resolves and validates dependencies
- `MetricsCalculator` - Calculates coupling metrics

**Responsibilities**:
- Aggregate parsed data into graph structure
- Calculate coupling strength (dependency weights)
- Identify circular dependencies
- Calculate basic metrics (fan-in, fan-out)
- Detect tightly coupled clusters

### 4. JSON Serializer

**Package**: `nl.dvholsteijn.couplinganalyzer.export`

Use **Jackson** or **Gson** for JSON serialization.

**Classes**:
- `JsonGraphExporter` - Exports CouplingGraph to JSON
- `GraphExporter` - Interface for future format support (GraphML, DOT)

**JSON Output Format**:
```json
{
  "projectName": "my-project",
  "version": "1.0.0",
  "generatedAt": "2025-10-18T20:00:00Z",
  "sourceDirectory": "/path/to/src",
  "classes": [
    {
      "id": "com.example.UserService",
      "name": "UserService",
      "packageName": "com.example",
      "filePath": "src/main/java/com/example/UserService.java",
      "type": "CLASS",
      "methods": [
        "getUser(Long):User",
        "createUser(UserDto):User",
        "deleteUser(Long):void"
      ],
      "fields": [
        "userRepository:UserRepository",
        "emailService:EmailService"
      ],
      "lineCount": 85,
      "isPublic": true
    }
  ],
  "dependencies": [
    {
      "sourceId": "com.example.UserService",
      "targetId": "com.example.UserRepository",
      "type": "USES",
      "location": "UserService.java:15",
      "weight": 5
    }
  ],
  "metrics": {
    "totalClasses": 42,
    "totalDependencies": 156,
    "averageDependenciesPerClass": 3.71,
    "mostCoupledClasses": {
      "com.example.UserService": 12,
      "com.example.OrderService": 10
    },
    "circularDependencies": [
      ["com.example.A", "com.example.B", "com.example.A"]
    ]
  },
  "aiAnalysisPrompt": "This graph represents the coupling structure of a Java project. Analyze for: 1) Classes with high coupling (>5 dependencies), 2) Circular dependencies, 3) Potential service boundaries, 4) Candidates for extraction into separate modules."
}
```

### 5. Integration with Maven Plugin

Update `CouplingAnalyzerMojo` to use the new graph generator.

**Changes to CouplingAnalyzerMojo**:
- Add parameter: `outputFormat` (JSON, TEXT, BOTH) - default: JSON
- Add parameter: `includeMetrics` (boolean) - default: true
- Add parameter: `excludePackages` (List<String>) - packages to exclude
- Wire up JavaSourceParser, GraphBuilder, and JsonGraphExporter
- Generate both JSON graph and human-readable text report

**Workflow**:
1. Validate source directory
2. Parse all Java files → List<ClassNode>
3. Build dependency graph → CouplingGraph
4. Calculate metrics
5. Export to JSON → `coupling-graph.json`
6. Generate text report → `coupling-report.txt`
7. Log summary to console

### 6. Dependencies to Add

Update `pom.xml` with:

```xml
<!-- JavaParser for AST parsing -->
<dependency>
    <groupId>com.github.javaparser</groupId>
    <artifactId>javaparser-symbol-solver-core</artifactId>
    <version>3.25.8</version>
</dependency>

<!-- Jackson for JSON serialization -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.1</version>
</dependency>

<!-- AssertJ for fluent test assertions -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.25.3</version>
    <scope>test</scope>
</dependency>
```

## Benefits

1. **AI-Friendly Format**: JSON structure is easy for AI agents to parse and reason about
2. **Complete Context**: Includes metadata, metrics, and even suggested analysis prompts
3. **Extensible**: Easy to add more dependency types or metrics
4. **Testable**: Each component (parser, builder, exporter) can be unit tested
5. **Tool Integration**: JSON can be consumed by visualization tools, IDEs, or CI/CD pipelines
6. **Incremental Analysis**: Can be run repeatedly to track coupling evolution

## Implementation Steps

1. Add JavaParser and Jackson dependencies to pom.xml
2. Create model classes (CouplingGraph, ClassNode, Dependency, etc.)
3. Implement JavaSourceParser with JavaParser library
4. Create GraphBuilder to aggregate parsed data
5. Implement MetricsCalculator for coupling metrics
6. Create JsonGraphExporter for JSON serialization
7. Update CouplingAnalyzerMojo to integrate all components
8. Write comprehensive unit tests for each component
9. Create integration test with sample Java project
10. Update README.md with JSON schema documentation

## Testing Strategy

### Unit Tests

**JavaSourceParser**:
- Parse simple class with no dependencies
- Parse class with field dependencies
- Parse class with method calls
- Parse inheritance relationships
- Parse interface implementations
- Handle parsing errors gracefully

**GraphBuilder**:
- Build graph from empty source
- Build graph with single class
- Build graph with circular dependencies
- Calculate dependency weights correctly
- Handle unresolved dependencies

**MetricsCalculator**:
- Calculate correct totals
- Identify most coupled classes
- Detect circular dependencies
- Calculate average metrics

**JsonGraphExporter**:
- Export empty graph
- Export complex graph
- Validate JSON schema
- Handle special characters in names

### Integration Tests

Create `src/test/resources/sample-project/` with example Java files:
- Simple classes
- Interdependent classes
- Inheritance hierarchy
- Interface implementations

Run full analysis and verify JSON output structure.

## Example Usage

### Maven Configuration

```xml
<plugin>
    <groupId>nl.dvholsteijn</groupId>
    <artifactId>coupling-analyzer-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <configuration>
        <outputFormat>BOTH</outputFormat>
        <includeMetrics>true</includeMetrics>
        <excludePackages>
            <exclude>com.example.generated</exclude>
            <exclude>com.example.test</exclude>
        </excludePackages>
    </configuration>
</plugin>
```

### Command Line

```bash
mvn coupling-analyzer:analyze

# Output:
# [INFO] Parsing 42 Java source files...
# [INFO] Found 42 classes, 156 dependencies
# [INFO] Most coupled classes:
# [INFO]   - com.example.UserService (12 dependencies)
# [INFO]   - com.example.OrderService (10 dependencies)
# [INFO] Circular dependencies detected: 2
# [INFO] Reports generated:
# [INFO]   - target/coupling-analysis/coupling-graph.json
# [INFO]   - target/coupling-analysis/coupling-report.txt
```

### AI Agent Usage

An AI coding agent can:
1. Read `coupling-graph.json`
2. Analyze the dependency structure
3. Identify high-coupling classes
4. Suggest refactoring strategies
5. Propose module boundaries

**Example AI Prompt**:
```
Analyze this coupling graph and suggest how to break down 
UserService (12 dependencies) into smaller, more cohesive services.
Focus on identifying bounded contexts and potential microservices.

Graph: [contents of coupling-graph.json]
```

## Future Enhancements

Phase 2 (future proposals):
- **Spring Framework Analysis**: Detect @Service, @Controller, @Repository annotations
- **Package-level Analysis**: Group classes by package and analyze package coupling
- **Temporal Coupling**: Integrate with Git history to find classes that change together
- **Visualization**: Generate HTML with D3.js or Cytoscape.js for interactive graphs
- **Comparison**: Compare graphs over time to track coupling trends
- **AI Suggestions**: Built-in AI prompts for common refactoring patterns
- **Custom Rules**: Allow users to define coupling rules and violations

## Risks & Mitigations

**Risk**: JavaParser may fail on complex/non-standard Java code
**Mitigation**: Catch parsing exceptions per file, continue with others, report errors

**Risk**: Large codebases may generate huge JSON files
**Mitigation**: Add filtering options, pagination, or separate output per package

**Risk**: Dependency resolution across modules
**Mitigation**: Phase 1 focuses on single-module projects, multi-module in Phase 2

**Risk**: Performance on large projects (1000+ classes)
**Mitigation**: Implement parallel parsing, add progress logging

## Definition of Done

- [ ] Model classes created and documented
- [ ] JavaSourceParser implementation complete with JavaParser
- [ ] GraphBuilder aggregates data correctly
- [ ] MetricsCalculator computes basic metrics
- [ ] JsonGraphExporter produces valid JSON
- [ ] CouplingAnalyzerMojo integrated with new components
- [ ] All unit tests pass (>80% code coverage)
- [ ] Integration test with sample project passes
- [ ] JSON output validated against example schema
- [ ] README.md updated with JSON format documentation
- [ ] Build succeeds: `mvn clean install`
- [ ] Manual test: Run on a real Java project, verify JSON output
- [ ] AI agent can successfully parse and analyze the JSON output

## Estimated Effort

~8-12 hours for experienced Java developer

## JSON Schema Example

For AI agents to validate the output format:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CouplingGraph",
  "type": "object",
  "required": ["projectName", "classes", "dependencies", "metrics"],
  "properties": {
    "projectName": {"type": "string"},
    "version": {"type": "string"},
    "generatedAt": {"type": "string", "format": "date-time"},
    "classes": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["id", "name", "packageName"],
        "properties": {
          "id": {"type": "string"},
          "name": {"type": "string"},
          "packageName": {"type": "string"},
          "type": {"enum": ["CLASS", "INTERFACE", "ENUM", "ABSTRACT_CLASS"]},
          "methods": {"type": "array", "items": {"type": "string"}},
          "fields": {"type": "array", "items": {"type": "string"}}
        }
      }
    },
    "dependencies": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["sourceId", "targetId", "type"],
        "properties": {
          "sourceId": {"type": "string"},
          "targetId": {"type": "string"},
          "type": {"enum": ["EXTENDS", "IMPLEMENTS", "USES", "CALLS"]},
          "weight": {"type": "integer"}
        }
      }
    },
    "metrics": {
      "type": "object",
      "properties": {
        "totalClasses": {"type": "integer"},
        "totalDependencies": {"type": "integer"},
        "averageDependenciesPerClass": {"type": "number"}
      }
    }
  }
}
```

## References

- [JavaParser Documentation](https://javaparser.org/)
- [Jackson JSON Documentation](https://github.com/FasterXML/jackson-databind)
- [Martin Fowler - Refactoring](https://refactoring.com/)
- [Measuring Coupling and Cohesion](https://www.aivosto.com/project/help/pm-oo-cohesion.html)
- [JSON Schema](https://json-schema.org/)

## Related Changes

- Depends on: `001-setup-maven-plugin-project` (Approved, Implemented)
- Enables: Future visualization and AI-assisted refactoring tools

