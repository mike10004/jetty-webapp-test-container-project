package io.github.mike10004.jettywebapp.testing;

import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.Assert.*;

public class ReflectionsTest {

    @Test
    public void getAnnotationFromHierarchy_annotated() {
        assertNotNull("AnnotatedClass", Reflections.getAnnotationFromHierarchy(AnnotatedClass.class, MyAnnotation.class));
    }

    @Test
    public void getAnnotationFromHierarchy_subclassOfAnnotated() {
        assertNotNull("UnannotatedSubclassOfAnnotatedClass", Reflections.getAnnotationFromHierarchy(UnannotatedSubclassOfAnnotatedClass.class, MyAnnotation.class));
    }

    @Test
    public void getAnnotationFromHierarchy_unannotated() {
        assertNull("UnannotatedClass", Reflections.getAnnotationFromHierarchy(UnannotatedClass.class, MyAnnotation.class));
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface MyAnnotation {}

    @MyAnnotation
    private static class AnnotatedClass {}

    private static class UnannotatedSubclassOfAnnotatedClass extends AnnotatedClass {}

    private static class UnannotatedClass {}
}