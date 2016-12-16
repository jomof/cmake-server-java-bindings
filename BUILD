# Copyright 2016 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
################################################################################

java_import(
    name = "gson",
    jars = [
        "prebuilts/gson-2.2.4/gson-2.2.4.jar",
    ],
    visibility = ["//visibility:public"],
)

java_library(
    name = "com.jomofisher.cmakeserver",
    srcs = [
        "com/jomofisher/cmakeserver/CMakeServerConnection.java",
        "com/jomofisher/cmakeserver/CMakeServerConnectionBuilder.java",
        "com/jomofisher/cmakeserver/Capabilities.java",
        "com/jomofisher/cmakeserver/ComputeReplyMessage.java",
        "com/jomofisher/cmakeserver/ConfigureReplyMessage.java",
        "com/jomofisher/cmakeserver/Generator.java",
        "com/jomofisher/cmakeserver/GlobalSettingsReplyMessage.java",
        "com/jomofisher/cmakeserver/HandshakeReplyMessage.java",
        "com/jomofisher/cmakeserver/HelloMessage.java",
        "com/jomofisher/cmakeserver/JsonUtils.java",
        "com/jomofisher/cmakeserver/Message.java",
        "com/jomofisher/cmakeserver/MessageMessage.java",
        "com/jomofisher/cmakeserver/ProgressMessage.java",
        "com/jomofisher/cmakeserver/ProgressReceiver.java",
        "com/jomofisher/cmakeserver/ProtocolVersion.java",
        "com/jomofisher/cmakeserver/Version.java",
    ],
    deps = ["gson"],
)

java_test(
    name = "TestCMakeServer",
    size = "small",
    srcs = glob(["com/jomofisher/cmakeserver/TestCMakeServer.java"]),
    test_class = "com.jomofisher.cmakeserver.TestCMakeServer",
    deps = [
        ":com.jomofisher.cmakeserver",
    ],
)
