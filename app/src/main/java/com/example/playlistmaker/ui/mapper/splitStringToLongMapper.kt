package com.example.playlistmaker.ui.mapper

fun splitStringToLongMapper(input: String): List<Long> {
    return input.split(",").map { it.toLong() }
}