package com.example.worldkids.data

data class Country(
    val id: String,
    val nameFr: String,
    val nameEn: String,
    val flagEmoji: String,
    val capital: String,
    val population: String,
    val continent: String,
    val mainLanguage: String,
    val currency: String,
    val kidFactTitle: String,
    val kidFacts: List<String>,
    val memoryHook: String,
    val images: List<String>,
    val mapX: Float,
    val mapY: Float,
    val colorHex: String
)
