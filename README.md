[![Build Status](https://travis-ci.org/jomof/cmake-server-java-bindings.svg?branch=master)](https://travis-ci.org/jomof/cmake-server-java-bindings)

# CMake Server Java Bindings
Creates java bindings for CMake Server. Example usage,

            CMakeServerConnection connection =
                    new CMakeServerConnectionBuilder(getCMakeInstallFolder())
                            .create();
            connection.handshake(new HandshakeMessage()
                    .setCookie("my-cookie")
                    .setGenerator("Ninja")
                    .setSourceDirectory("./hello-world")
                    .setBuildDirectory("./hello-world-output")
                    .setProtocolVersion(new ProtocolVersion()
                            .setMajor(1)
                            .setMinor(0)));
            connection.configure();
            connection.compute();
            CodeModelReplyMessage codemodelReply = connection.codemodel();

[Download cmakeserver-1.0-alpha1.jar] (https://github.com/jomof/cmake-server-java-bindings/releases/tag/cmakeserver-1.0-alpha1)

Or build it yourself:

    git clone https://github.com/jomof/cmake-server-java-bindings.git
    cd cmake-server-java-bindings
    ./gradlew assemble check
