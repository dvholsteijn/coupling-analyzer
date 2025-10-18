package nl.dvholsteijn.couplinganalyzer.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import nl.dvholsteijn.couplinganalyzer.model.ClassNode;
import nl.dvholsteijn.couplinganalyzer.model.ClassType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of JavaSourceParser using JavaParser library.
 */
public class JavaParserImpl implements JavaSourceParser {

    private final JavaParser javaParser;

    public JavaParserImpl() {
        this.javaParser = new JavaParser();
    }

    @Override
    public List<ClassNode> parseDirectory(File sourceDirectory) throws IOException {
        List<ClassNode> classNodes = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(sourceDirectory.toPath())) {
            List<File> javaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            for (File javaFile : javaFiles) {
                try {
                    ClassNode classNode = parseFile(javaFile);
                    if (classNode != null) {
                        classNodes.add(classNode);
                    }
                } catch (Exception e) {
                    // Log error but continue parsing other files
                    System.err.println("Error parsing file " + javaFile.getPath() + ": " + e.getMessage());
                }
            }
        }

        return classNodes;
    }

    @Override
    public ClassNode parseFile(File sourceFile) throws IOException {
        ParseResult<CompilationUnit> parseResult = javaParser.parse(sourceFile);

        if (!parseResult.isSuccessful() || !parseResult.getResult().isPresent()) {
            return null;
        }

        CompilationUnit cu = parseResult.getResult().get();

        // Get the primary type (class, interface, enum, etc.)
        TypeDeclaration<?> primaryType = cu.getPrimaryType().orElse(null);
        if (primaryType == null) {
            return null;
        }

        ClassNode classNode = new ClassNode();

        // Set basic information
        String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");

        classNode.setPackageName(packageName);
        classNode.setName(primaryType.getNameAsString());

        String fullyQualifiedName = packageName.isEmpty()
                ? primaryType.getNameAsString()
                : packageName + "." + primaryType.getNameAsString();
        classNode.setId(fullyQualifiedName);
        classNode.setFilePath(sourceFile.getPath());
        classNode.setPublic(primaryType.isPublic());

        // Determine class type
        classNode.setType(determineClassType(primaryType));

        // Extract methods
        List<String> methods = extractMethods(primaryType);
        classNode.setMethods(methods);

        // Extract fields
        List<String> fields = extractFields(primaryType);
        classNode.setFields(fields);

        // Count lines
        try {
            long lineCount = Files.lines(sourceFile.toPath()).count();
            classNode.setLineCount((int) lineCount);
        } catch (IOException e) {
            classNode.setLineCount(0);
        }

        return classNode;
    }

    private ClassType determineClassType(TypeDeclaration<?> type) {
        if (type.isClassOrInterfaceDeclaration()) {
            ClassOrInterfaceDeclaration classOrInterface = type.asClassOrInterfaceDeclaration();
            if (classOrInterface.isInterface()) {
                return ClassType.INTERFACE;
            } else if (classOrInterface.isAbstract()) {
                return ClassType.ABSTRACT_CLASS;
            } else {
                return ClassType.CLASS;
            }
        } else if (type.isEnumDeclaration()) {
            return ClassType.ENUM;
        } else if (type.isAnnotationDeclaration()) {
            return ClassType.ANNOTATION;
        }
        return ClassType.CLASS;
    }

    private List<String> extractMethods(TypeDeclaration<?> type) {
        List<String> methods = new ArrayList<>();

        for (MethodDeclaration method : type.getMethods()) {
            // Include public/protected methods, or all methods for interfaces (which are implicitly public)
            if (method.isPublic() || method.isProtected() || type.isClassOrInterfaceDeclaration() && type.asClassOrInterfaceDeclaration().isInterface()) {
                StringBuilder methodSig = new StringBuilder();
                methodSig.append(method.getNameAsString()).append("(");

                // Add parameter types
                String params = method.getParameters().stream()
                        .map(param -> param.getType().asString())
                        .collect(Collectors.joining(","));
                methodSig.append(params);
                methodSig.append("):");
                methodSig.append(method.getType().asString());

                methods.add(methodSig.toString());
            }
        }

        return methods;
    }

    private List<String> extractFields(TypeDeclaration<?> type) {
        List<String> fields = new ArrayList<>();

        for (FieldDeclaration field : type.getFields()) {
            if (field.isPublic() || field.isProtected() || field.isPrivate()) {
                for (VariableDeclarator variable : field.getVariables()) {
                    String fieldInfo = variable.getNameAsString() + ":" + variable.getType().asString();
                    fields.add(fieldInfo);
                }
            }
        }

        return fields;
    }
}

