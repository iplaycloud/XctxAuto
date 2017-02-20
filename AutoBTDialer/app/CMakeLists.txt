cmake_minimum_required(VERSION 3.6)

add_library( native-bt-lib  SHARED src/main/cpp/native-bt-lib.cpp )

find_library( log-lib log )

target_link_libraries( native-bt-lib ${log-lib} )
