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
{
   "configurations":[
      {
         "name":"Debug",
         "projects":[
            {
               "buildDirectory":"C:/Users/jomof/projects/cmakeserver/test-output/cmake-bindings-test132256646897976579",
               "name":"Project",
               "sourceDirectory":"C:/Users/jomof/projects/cmakeserver/test-data/cmake-projects/android-shared-lib",
               "targets":[
                  {
                     "artifacts":[
                        "C:/Users/jomof/projects/cmakeserver/test-output/cmake-bindings-test132256646897976579/libnative-lib.so"
                     ],
                     "buildDirectory":"C:/Users/jomof/projects/cmakeserver/test-output/cmake-bindings-test132256646897976579",
                     "fileGroups":[
                        {
                           "compileFlags":"-g -DANDROID -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -Wa,--noexecstack -Wformat -Werror=format-security -fno-exceptions -fno-rtti -g -DANDROID -ffunction-sections -funwind-tables -fstack-protector-strong -no-canonical-prefixes -Wa,--noexecstack -Wformat -Werror=format-security -fno-exceptions -fno-rtti  -O0 -O0  -fPIC  ",
                           "defines":[
                              "native_lib_EXPORTS"
                           ],
                           "includePath":[
                              {
                                 "isSystem":true,
                                 "path":"C:/Users/jomof/projects/cmakeserver/prebuilts/android-ndk-r13b/sources/cxx-stl/gnu-libstdc++/4.9/include"
                              },
                              {
                                 "isSystem":true,
                                 "path":"C:/Users/jomof/projects/cmakeserver/prebuilts/android-ndk-r13b/sources/cxx-stl/gnu-libstdc++/4.9/libs/arm64-v8a/include"
                              },
                              {
                                 "isSystem":true,
                                 "path":"C:/Users/jomof/projects/cmakeserver/prebuilts/android-ndk-r13b/sources/cxx-stl/gnu-libstdc++/4.9/include/backward"
                              }
                           ],
                           "isGenerated":false,
                           "language":"CXX",
                           "sources":[
                              "src/main/cpp/native-lib.cpp"
                           ]
                        }
                     ],
                     "fullName":"libnative-lib.so",
                     "linkFlags":"-Wl,--build-id -Wl,--warn-shared-textrel -Wl,--fatal-warnings -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now -Wl,--build-id -Wl,--warn-shared-textrel -Wl,--fatal-warnings -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now",
                     "linkLibraries":"-llog -lm \"C:/Users/jomof/projects/cmakeserver/prebuilts/android-ndk-r13b/sources/cxx-stl/gnu-libstdc++/4.9/libs/arm64-v8a/libgnustl_static.a\"",
                     "linkerLanguage":"CXX",
                     "name":"native-lib",
                     "sourceDirectory":"C:/Users/jomof/projects/cmakeserver/test-data/cmake-projects/android-shared-lib",
                     "sysroot":"C:/Users/jomof/projects/cmakeserver/prebuilts/android-ndk-r13b/platforms/android-21/arch-arm64",
                     "type":"SHARED_LIBRARY"
                  }
               ]
            }
         ]
      }
   ],
   "cookie":"",
   "inReplyTo":"codemodel",
   "type":"reply"
}
 */
@SuppressWarnings("unused")
public class CodeModelReplyMessage extends BaseMessage {
    public Configuration configurations[];
    public String cookie;
    public String inReplyTo;
}
