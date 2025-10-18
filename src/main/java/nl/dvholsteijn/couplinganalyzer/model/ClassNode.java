package nl.dvholsteijn.couplinganalyzer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Java class or interface in the coupling graph.
 */
public class ClassNode {
    private String id;              // Fully qualified name
    private String name;            // Simple name
    private String packageName;
    private String filePath;
    private ClassType type;
    private List<String> methods;
    private List<String> fields;
    private int lineCount;
    private boolean isPublic;

    public ClassNode() {
        this.methods = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public ClassNode(String id, String name, String packageName) {
        this();
        this.id = id;
        this.name = name;
        this.packageName = packageName;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ClassType getType() {
        return type;
    }

    public void setType(ClassType type) {
        this.type = type;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassNode classNode = (ClassNode) o;
        return Objects.equals(id, classNode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ClassNode{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}

