 CMAKE_URL="https://cmake.org/files/v3.7/cmake-3.7.1-Linux-x86_64.tar.gz"
 mkdir cmake
 wget --no-check-certificate --quiet -O - ${CMAKE_URL} | tar --strip-components=1 -xz -C cmake

