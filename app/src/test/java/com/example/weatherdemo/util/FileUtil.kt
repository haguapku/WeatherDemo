package com.example.weatherdemo.util

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

fun readFileFromPath(): String {

    val file = ClassLoader.getSystemClassLoader().getResource("test.json")

    return Files.lines(Paths.get(file.toURI())).parallel().collect(Collectors.joining())

}