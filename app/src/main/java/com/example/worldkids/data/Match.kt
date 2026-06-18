package com.example.worldkids.data

data class Match(
    val id: String,
    val label: String,
    val countryAId: String,
    val countryBId: String,
    val competition: String,
    val dateLabel: String,
    val groupOrStage: String,
    val phase: String = "group",
    val featured: Boolean = false
)
