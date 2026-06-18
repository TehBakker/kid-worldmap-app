package com.example.worldkids.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorldDataRepositoryTest {

  private val sampleCountriesJson = """
    [
      {"id":"france","nameFr":"France","nameEn":"France","flagEmoji":"🇫🇷","capital":"Paris",
       "population":"68 millions","continent":"Europe","mainLanguage":"Français","currency":"Euro",
       "mapX":0.48,"mapY":0.32,"colorHex":"#1565C0","worldCup2026Group":"I","isWorldCup2026":true},
      {"id":"japan","nameFr":"Japon","nameEn":"Japan","flagEmoji":"🇯🇵","capital":"Tokyo",
       "population":"125 millions","continent":"Asie","mainLanguage":"Japonais","currency":"Yen",
       "mapX":0.82,"mapY":0.38,"colorHex":"#E53935","isWorldCup2026":true}
    ]
  """.trimIndent()

  private val sampleMatchesJson = """
    [
      {"id":"m1","label":"France vs Japon","countryAId":"france","countryBId":"japan",
       "competition":"CDM 2026","dateLabel":"Juin 2026","groupOrStage":"Poule I","phase":"group","featured":true}
    ]
  """.trimIndent()

  @Test
  fun parseCountries_loadsWorldCupFields() {
    val list = WorldDataRepository.parseCountries(sampleCountriesJson)
    assertEquals(2, list.size)
    assertEquals("I", list[0].worldCup2026Group)
    assertTrue(list[0].isWorldCup2026)
  }

  @Test
  fun parseMatches_loads2026Match() {
    val list = WorldDataRepository.parseMatches(sampleMatchesJson)
    assertEquals(1, list.size)
    assertEquals("CDM 2026", list[0].competition)
    assertTrue(list[0].featured)
  }

  @Test
  fun searchCountries_findsFrenchName() {
    val countries = WorldDataRepository.parseCountries(sampleCountriesJson)
    val repo = object {
      fun search(q: String) = countries.filter {
        it.nameFr.contains(q, true) || it.nameEn.contains(q, true)
      }
    }
    assertEquals("France", repo.search("fran")[0].nameFr)
  }
}
