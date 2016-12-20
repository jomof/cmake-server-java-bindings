[![Build Status](https://travis-ci.org/jomof/cmake-server-java-bindings.svg?branch=master)](https://travis-ci.org/jomof/cmake-server-java-bindings)

# CMake Server Java Bindings
Creates java bindings for CMake Server. Example usage,

            CMakeServerConnection connection =
                    new CMakeServerConnectionBuilder(getCMakeInstallFolder())
                            .create();
            HandshakeRequest message = new HandshakeRequest();
            message.cookie = "my-cookie";
            message.generator = "Ninja";
            message.sourceDirectory = "./hello-world";
            message.buildDirectory = "./hello-world-output";
            ProtocolVersion version = new ProtocolVersion();
            version.major = 1;
            message.protocolVersion = version;
            connection.configure();
            connection.compute();
            CodeModel codemodel = connection.codemodel();

[Download cmakeserver-1.0-alpha1.jar] (https://github.com/jomof/cmake-server-java-bindings/releases/tag/cmakeserver-1.0-alpha1)

Or build it yourself:

    git clone https://github.com/jomof/cmake-server-java-bindings.git
    cd cmake-server-java-bindings
    ./gradlew assemble check
