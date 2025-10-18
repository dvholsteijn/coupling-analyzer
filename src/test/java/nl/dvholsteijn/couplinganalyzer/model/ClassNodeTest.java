package nl.dvholsteijn.couplinganalyzer.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClassNodeTest {

    @Test
    void shouldCreateClassNodeWithBasicInfo() {
        // Given: Basic class information
        String id = "com.example.MyClass";
        String name = "MyClass";
        String packageName = "com.example";

        // When: Creating a ClassNode
        ClassNode classNode = new ClassNode(id, name, packageName);

        // Then: All basic properties are set correctly
        assertThat(classNode.getId()).isEqualTo(id);
        assertThat(classNode.getName()).isEqualTo(name);
        assertThat(classNode.getPackageName()).isEqualTo(packageName);
        assertThat(classNode.getMethods()).isEmpty();
        assertThat(classNode.getFields()).isEmpty();
    }

    @Test
    void shouldSupportEquality() {
        // Given: Two ClassNodes with the same ID
        ClassNode node1 = new ClassNode("com.example.MyClass", "MyClass", "com.example");
        ClassNode node2 = new ClassNode("com.example.MyClass", "MyClass", "com.example");

        // When: Comparing them
        // Then: They should be equal
        assertThat(node1).isEqualTo(node2);
        assertThat(node1.hashCode()).isEqualTo(node2.hashCode());
    }

    @Test
    void shouldAddMethodsAndFields() {
        // Given: A ClassNode
        ClassNode classNode = new ClassNode("com.example.MyClass", "MyClass", "com.example");

        // When: Adding methods and fields
        classNode.getMethods().add("doSomething():void");
        classNode.getFields().add("name:String");

        // Then: They are stored correctly
        assertThat(classNode.getMethods()).hasSize(1);
        assertThat(classNode.getFields()).hasSize(1);
    }
}

