package com.example.musiccrawler.hifini

import android.icu.text.CaseMap

data class DataSearch(val attributes:ArrayList<Attributes>, val pageNumber:ArrayList<String>){

        data class Attributes(val songTitle:String="0",val songHref:String="0")

}