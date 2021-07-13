package com.example.musiccrawler.hifini

data class DataSearch(val attributes:ArrayList<Attributes>, val pageNumber:ArrayList<String>){

        data class Attributes(val songTitle:String="0",val songHref:String="0")

}