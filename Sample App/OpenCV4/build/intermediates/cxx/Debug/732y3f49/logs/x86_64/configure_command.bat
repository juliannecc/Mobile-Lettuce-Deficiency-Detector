@echo off
"C:\\Users\\julia\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\julia\\Desktop\\Mobile-Lettuce-Deficiency-Detector\\Sample App\\OpenCV4\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86_64" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86_64" ^
  "-DANDROID_NDK=C:\\Users\\julia\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\julia\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\julia\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\julia\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\julia\\Desktop\\Mobile-Lettuce-Deficiency-Detector\\Sample App\\OpenCV4\\build\\intermediates\\cxx\\Debug\\732y3f49\\obj\\x86_64" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\julia\\Desktop\\Mobile-Lettuce-Deficiency-Detector\\Sample App\\OpenCV4\\build\\intermediates\\cxx\\Debug\\732y3f49\\obj\\x86_64" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\julia\\Desktop\\Mobile-Lettuce-Deficiency-Detector\\Sample App\\OpenCV4\\.cxx\\Debug\\732y3f49\\x86_64" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
