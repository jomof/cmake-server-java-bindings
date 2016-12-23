[![Build Status](https://travis-ci.org/jomof/cmake-server-java-bindings.svg?branch=master)](https://travis-ci.org/jomof/cmake-server-java-bindings)
[![](https://jitpack.io/v/com.jomofisher/cmake-server-java-bindings.svg)](https://jitpack.io/#com.jomofisher/cmake-server-java-bindings)
[![codecov](https://codecov.io/gh/jomof/cmake-server-java-bindings/branch/master/graph/badge.svg)](https://codecov.io/gh/jomof/cmake-server-java-bindings)

# CMake Server Java Bindings
Creates java bindings for CMake Server. Example usage,

            ServerConnection connection = new CMake(getCMakeInstallFolder())
                    .newServerBuilder()
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

# Building the Code

    git clone https://github.com/jomof/cmake-server-java-bindings.git
    cd cmake-server-java-bindings
    ./gradlew assemble check
