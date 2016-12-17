package com.jomofisher.cmakeserver.model;

/**
 * Example,
 * <p>
 * {
 * "configurations":[
 * {
 * "name":"",
 * "projects":[
 * {
 * "buildDirectory":"/tmp",
 * "name":"hello",
 * "sourceDirectory":"/usr/local/google/home/jomof/projects/cmake-server-java-bindings/test-data/cmake-projects/hello-world",
 * "targets":[
 * {
 * "artifacts":["/tmp/hello"],
 * "buildDirectory":"/tmp",
 * "fileGroups":[
 * {
 * "compileFlags":" ",
 * "isGenerated":false,
 * "language":"CXX",
 * "sources":["hello.cpp"]
 * }
 * ],
 * "fullName":"hello",
 * "linkLibraries":"-rdynamic",
 * "linkerLanguage":"CXX",
 * "name":"hello",
 * "sourceDirectory":"/usr/local/google/home/jomof/projects/cmake-server-java-bindings/test-data/cmake-projects/hello-world",
 * "type":"EXECUTABLE"
 * }
 * ]
 * }
 * ]
 * }
 * ],
 * "cookie":"",
 * "inReplyTo":"codemodel",
 * "type":"reply"
 * }
 */
@SuppressWarnings("unused")
public class CodeModelReplyMessage extends BaseMessage {
  public Configuration configurations[];
  public String cookie;
  public String inReplyTo;
}
