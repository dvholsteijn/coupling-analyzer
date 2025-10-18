package nl.dvholsteijn.couplinganalyzer.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import nl.dvholsteijn.couplinganalyzer.model.Dependency;
import nl.dvholsteijn.couplinganalyzer.model.DependencyType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extracts dependency relationships from Java source files.
 */
public class DependencyExtractor {

    private final JavaParser javaParser;

    public DependencyExtractor() {
        this.javaParser = new JavaParser();
    }

    /**
     * Extract all dependencies from a Java source file.
     *
     * @param sourceFile the Java source file
     * @param sourceClassId the fully qualified name of the source class
     * @return list of dependencies
     * @throws IOException if there's an error reading the file
     */
    public List<Dependency> extractDependencies(File sourceFile, String sourceClassId) throws IOException {
        ParseResult<CompilationUnit> parseResult = javaParser.parse(sourceFile);

        if (!parseResult.isSuccessful() || !parseResult.getResult().isPresent()) {
            return new ArrayList<>();
        }

        CompilationUnit cu = parseResult.getResult().get();
        List<Dependency> dependencies = new ArrayList<>();

        // Get the primary type
        TypeDeclaration<?> primaryType = cu.getPrimaryType().orElse(null);
        if (primaryType == null || !primaryType.isClassOrInterfaceDeclaration()) {
            return dependencies;
        }

        ClassOrInterfaceDeclaration classDecl = primaryType.asClassOrInterfaceDeclaration();

        // Extract extends relationships
        for (ClassOrInterfaceType extendedType : classDecl.getExtendedTypes()) {
            String targetId = resolveTypeName(extendedType, cu);
            if (targetId != null && !isJavaLangType(targetId)) {
                Dependency dep = new Dependency(sourceClassId, targetId, DependencyType.EXTENDS);
                dep.setLocation(sourceFile.getName() + ":" + extendedType.getBegin().map(p -> p.line).orElse(0));
                dependencies.add(dep);
            }
        }

        // Extract implements relationships
        for (ClassOrInterfaceType implementedType : classDecl.getImplementedTypes()) {
            String targetId = resolveTypeName(implementedType, cu);
            if (targetId != null && !isJavaLangType(targetId)) {
                Dependency dep = new Dependency(sourceClassId, targetId, DependencyType.IMPLEMENTS);
                dep.setLocation(sourceFile.getName() + ":" + implementedType.getBegin().map(p -> p.line).orElse(0));
                dependencies.add(dep);
            }
        }

        // Extract field type dependencies
        for (FieldDeclaration field : classDecl.getFields()) {
            String typeId = resolveTypeName(field.getCommonType().asString(), cu);
            if (typeId != null && !isJavaLangType(typeId) && !typeId.equals(sourceClassId)) {
                Dependency dep = new Dependency(sourceClassId, typeId, DependencyType.FIELD_TYPE);
                dep.setLocation(sourceFile.getName() + ":" + field.getBegin().map(p -> p.line).orElse(0));
                dependencies.add(dep);
            }
        }

        // Extract method parameter and return type dependencies
        for (MethodDeclaration method : classDecl.getMethods()) {
            // Return type
            String returnType = resolveTypeName(method.getType().asString(), cu);
            if (returnType != null && !isJavaLangType(returnType) && !returnType.equals(sourceClassId)) {
                Dependency dep = new Dependency(sourceClassId, returnType, DependencyType.RETURN_TYPE);
                dep.setLocation(sourceFile.getName() + ":" + method.getBegin().map(p -> p.line).orElse(0));
                dependencies.add(dep);
            }

            // Parameter types
            method.getParameters().forEach(param -> {
                String paramType = resolveTypeName(param.getType().asString(), cu);
                if (paramType != null && !isJavaLangType(paramType) && !paramType.equals(sourceClassId)) {
                    Dependency dep = new Dependency(sourceClassId, paramType, DependencyType.PARAMETER_TYPE);
                    dep.setLocation(sourceFile.getName() + ":" + param.getBegin().map(p -> p.line).orElse(0));
                    dependencies.add(dep);
                }
            });
        }

        return dependencies;
    }

    private String resolveTypeName(ClassOrInterfaceType type, CompilationUnit cu) {
        return resolveTypeName(type.getNameAsString(), cu);
    }

    private String resolveTypeName(String typeName, CompilationUnit cu) {
        // Remove generics and array brackets
        typeName = typeName.replaceAll("<.*>", "").replaceAll("\\[\\]", "").trim();

        // Skip primitives
        if (isPrimitive(typeName)) {
            return null;
        }

        // If already fully qualified
        if (typeName.contains(".")) {
            return typeName;
        }

        // Check imports
        String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");

        for (var importDecl : cu.getImports()) {
            String importName = importDecl.getNameAsString();
            if (importName.endsWith("." + typeName)) {
                return importName;
            }
        }

        // Assume same package
        return packageName.isEmpty() ? typeName : packageName + "." + typeName;
    }

    private boolean isPrimitive(String typeName) {
        return typeName.equals("void") || typeName.equals("boolean") ||
               typeName.equals("byte") || typeName.equals("short") ||
               typeName.equals("int") || typeName.equals("long") ||
               typeName.equals("float") || typeName.equals("double") ||
               typeName.equals("char");
    }

    private boolean isJavaLangType(String typeName) {
        return typeName.startsWith("java.lang.") ||
               typeName.equals("String") || typeName.equals("Object") ||
               typeName.equals("Integer") || typeName.equals("Long") ||
               typeName.equals("Double") || typeName.equals("Boolean");
    }
}

