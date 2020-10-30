package io.github.mike10004.jettywebapp.testing;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public interface JspServletConfig {

    default File servletContextTempDir() {
        return new File(System.getProperty("java.io.tmpdir"), "servlet-context-tmp");
    }

    default Map<String, String> initParams() {
        Map<String, String> jspInitParams = new LinkedHashMap<>();
        jspInitParams.put("logVerbosityLevel", "DEBUG");
        jspInitParams.put("fork", "false");
        jspInitParams.put("xpoweredBy", "false");
        jspInitParams.put("compilerTargetVM", "1.8");
        jspInitParams.put("compilerSourceVM", "1.8");
        jspInitParams.put("keepgenerated", "true");
        return jspInitParams;
    }

    default Stream<String> pathSpecs() {
        return Stream.of("*.jsp");
    }

}
