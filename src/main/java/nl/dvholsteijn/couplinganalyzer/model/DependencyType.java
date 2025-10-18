package nl.dvholsteijn.couplinganalyzer.model;

/**
 * Enumeration of dependency types between classes.
 */
public enum DependencyType {
    /** Class extends another class */
    EXTENDS,

    /** Class implements an interface */
    IMPLEMENTS,

    /** Class uses another class (general usage) */
    USES,

    /** Method calls another class's method */
    CALLS,

    /** Field has a type of another class */
    FIELD_TYPE,

    /** Method parameter has a type of another class */
    PARAMETER_TYPE,

    /** Method return type is another class */
    RETURN_TYPE
}

