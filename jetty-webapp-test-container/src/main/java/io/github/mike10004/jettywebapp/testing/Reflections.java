package io.github.mike10004.jettywebapp.testing;

import javax.annotation.Nullable;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.lang.annotation.Annotation;

class Reflections {

    private Reflections() {
        throw new AssertionError("not to be instantiated");
    }

    @Nullable
    public static <A extends Annotation> A getAnnotationFromHierarchy(Class<?> derived, Class<A> annotationClass) {
        Class<?> superclass = derived;
        A anno = null;
        while (superclass != null && (anno = superclass.getAnnotation(annotationClass)) == null) {
            superclass = superclass.getSuperclass();
        }
        return anno;
    }


}
