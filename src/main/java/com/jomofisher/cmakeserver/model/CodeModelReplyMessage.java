/*
 * Copyright 2016 Jomo Fisher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
