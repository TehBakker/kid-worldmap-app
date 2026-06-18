package com.example.worldkids.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.worldkids.data.Country
import com.example.worldkids.data.MainTab
import com.example.worldkids.data.Match
import com.example.worldkids.data.MatchFilter
import com.example.worldkids.data.WorldCupGroup
import com.example.worldkids.data.WorldDataRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorldDataRepository(application)

    val countries: List<Country> = repository.countries
    val borders: Map<String, com.example.worldkids.data.CountryShape> = repository.borders
    val allMatches: List<Match> = repository.matches
    val worldCupGroups: List<WorldCupGroup> = repository.worldCupGroups.groups
    val worldCupCompetition: String = repository.worldCupGroups.competition

    var mainTab by mutableStateOf(MainTab.EXPLORE)
        private set

    var matchFilter by mutableStateOf(MatchFilter.ALL)
        private set

    var selectedMatch by mutableStateOf<Match?>(null)
        private set

    var selectedCountry by mutableStateOf<Country?>(null)
        private set

    var selectedGroupId by mutableStateOf<String?>(null)
        private set

    var showConfetti by mutableStateOf(false)
        private set

    /** Le panneau de recherche / Coupe du monde est-il déplié ? (se replie au clic sur un pays) */
    var explorerExpanded by mutableStateOf(true)
        private set

    val filteredMatches: List<Match>
        get() = repository.filterMatches(matchFilter)

    val highlightedCountryIds: Set<String>
        get() = buildSet {
            selectedMatch?.let {
                add(it.countryAId)
                add(it.countryBId)
            }
            selectedCountry?.let { add(it.id) }
            selectedGroupId?.let { groupId ->
                worldCupGroups.find { it.id == groupId }?.teamIds?.forEach { add(it) }
            }
        }

    val focusCountryId: String?
        get() = selectedCountry?.id

    fun onMainTabChange(tab: MainTab) {
        mainTab = tab
        explorerExpanded = true
        if (tab == MainTab.WORLD_CUP) {
            matchFilter = MatchFilter.FEATURED
        }
    }

    fun onMatchFilterChange(filter: MatchFilter) {
        matchFilter = filter
    }

    fun selectMatch(match: Match) {
        selectedMatch = match
        selectedCountry = null
        selectedGroupId = null
        showConfetti = true
    }

    fun selectCountry(country: Country) {
        selectedCountry = country
        selectedMatch = null
        selectedGroupId = country.worldCup2026Group
        // On replie la recherche pour laisser place à la carte + à la fiche détaillée
        explorerExpanded = false
    }

    fun selectCountryById(id: String) {
        repository.countryById(id)?.let { selectCountry(it) }
    }

    fun toggleExplorer() {
        explorerExpanded = !explorerExpanded
    }

    fun selectGroup(groupId: String) {
        selectedGroupId = groupId
        selectedMatch = null
        selectedCountry = null
    }

    fun searchCountries(query: String): List<Country> = repository.searchCountries(query)

    fun countryById(id: String): Country? = repository.countryById(id)

    fun countriesInGroup(groupId: String): List<Country> =
        repository.countriesByIds(
            worldCupGroups.find { it.id == groupId }?.teamIds.orEmpty()
        )

    fun clearConfetti() {
        showConfetti = false
    }
}
