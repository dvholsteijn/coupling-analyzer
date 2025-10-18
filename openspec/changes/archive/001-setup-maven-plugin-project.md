# Proposal: Setup Minimal Maven Plugin Project

**Status**: Approved  
**Created**: 2025-10-18  
**Approved**: 2025-10-18  
**Type**: Feature

## Summary

Set up the basic Maven plugin project structure for the coupling-analyzer tool. This will create a minimal, working Maven plugin that can be built and used in other Maven projects.

## Context

The coupling-analyzer tool aims to help developers and AI agents refactor monolithic applications by visualizing coupling and cohesion. It will integrate with Maven-based projects as a plugin. Currently, the project has no Java/Maven structure.

## Proposed Changes

### 1. Project Structure

Create the following directory structure:

```
coupling-analyzer/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── nl/
│   │           └── dvholsteijn/
│   │               └── couplinganalyzer/
│   │                   ├── CouplingAnalyzerMojo.java
│   │                   └── analyzer/
│   │                       └── package-info.java
│   └── test/
│       └── java/
│           └── nl/
│               └── dvholsteijn/
│                   └── couplinganalyzer/
│                       └── CouplingAnalyzerMojoTest.java
└── README.md (existing)
```

### 2. Root POM Configuration

Create `pom.xml` with:
- **groupId**: `nl.dvholsteijn.coupling-analyzer`
- **artifactId**: `coupling-analyzer-maven-plugin`
- **version**: `0.1.0-SNAPSHOT`
- **packaging**: `maven-plugin`

**Key Dependencies**:
- `maven-plugin-api` (3.9.x) - Core Maven plugin API
- `maven-plugin-annotations` (3.9.x) - For @Mojo annotations
- `maven-project` (2.2.1) - Access to project model
- `junit-jupiter` (5.10.x) - For unit testing

**Build Configuration**:
- `maven-plugin-plugin` - To build the plugin
- Java version: 17 (LTS)
- Source encoding: UTF-8

### 3. Minimal Mojo Implementation

Create `CouplingAnalyzerMojo.java` - the main plugin entry point:

**Goal**: `analyze`
**Phase**: `verify` (default)
**Thread Safe**: Yes

**Parameters**:
- `project` (readonly, required) - The Maven project
- `outputDirectory` (default: `${project.build.directory}/coupling-analysis`) - Where to write reports
- `sourceDirectory` (default: `${project.build.sourceDirectory}`) - Source code location

**Functionality** (Phase 1):
- Log basic project information
- Validate that source directory exists
- Create output directory
- Generate a minimal placeholder report (e.g., `coupling-report.txt`)

### 4. Unit Tests

Create `CouplingAnalyzerMojoTest.java`:
- Test mojo initialization
- Test with valid project structure
- Test with missing source directory
- Test output directory creation

Use Given-When-Then pattern as per project conventions.

### 5. Documentation Updates

Update `README.md` to include:
- Build instructions (`mvn clean install`)
- Usage example in another project's pom.xml
- Basic plugin configuration options

## Benefits

1. **Immediate Value**: Working plugin that can be installed and tested
2. **Foundation**: Proper structure for adding actual analysis logic
3. **Testable**: Unit test infrastructure in place
4. **Standards Compliant**: Follows Maven plugin best practices
5. **Extensible**: Easy to add new goals and parameters

## Implementation Steps

1. Create project directory structure
2. Create root `pom.xml` with dependencies
3. Implement minimal `CouplingAnalyzerMojo`
4. Add unit tests
5. Update documentation
6. Verify build: `mvn clean install`
7. Test in a sample Maven project

## Testing Strategy

**Unit Tests**:
- Mojo initialization and configuration
- Parameter validation
- Directory operations
- Error handling

**Integration Test** (manual for phase 1):
- Install plugin locally
- Create a test Maven project
- Add plugin to test project
- Run `mvn coupling-analyzer:analyze`
- Verify output

## Example Usage

After installation, users can add to their `pom.xml`:

```xml
<plugin>
    <groupId>nl.dvholsteijn.couplinganalyzer</groupId>
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
```

Then run: `mvn verify` or `mvn coupling-analyzer:analyze`

## Future Enhancements

This minimal setup provides foundation for:
- AST parsing of Java source files
- Dependency graph construction
- Coupling metrics calculation
- Cohesion analysis
- HTML/JSON report generation
- Integration with visualization tools

## Risks & Mitigations

**Risk**: Maven plugin API learning curve
**Mitigation**: Start with minimal implementation, follow official Maven plugin tutorial

**Risk**: Java version compatibility
**Mitigation**: Use Java 17 (LTS), maintain backward compatibility where possible

## Alternatives Considered

1. **Gradle Plugin**: Decided on Maven first per project requirements
2. **Standalone CLI Tool**: Less integrated, Maven plugin provides better DX
3. **Multi-module Project**: Overkill for initial phase, can refactor later

## Definition of Done

- [ ] Project builds successfully with `mvn clean install`
- [ ] All unit tests pass
- [ ] Plugin can be installed to local Maven repository
- [ ] Plugin can be executed in a test project
- [ ] Basic report is generated
- [ ] README.md updated with usage instructions
- [ ] Code follows standard Java conventions
- [ ] Tests follow Given-When-Then pattern

## Estimated Effort

~2-4 hours for experienced Java/Maven developer

## References

- [Maven Plugin Development Guide](https://maven.apache.org/guides/plugin/guide-java-plugin-development.html)
- [Maven Plugin API](https://maven.apache.org/ref/current/maven-plugin-api/)
- [Maven Plugin Testing](https://maven.apache.org/plugin-developers/plugin-testing.html)

