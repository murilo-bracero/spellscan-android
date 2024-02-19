package com.example.spellscan.converter

import com.example.spellscan.R

fun symbolToDrawable(symbol: String): Int {
    return when (symbol) {
        "W" -> R.drawable.white_mana_symbol
        "U" -> R.drawable.blue_mana_symbol
        "B" -> R.drawable.black_mana_symbol
        "R" -> R.drawable.red_mana_symbol
        "G" -> R.drawable.green_mana_symbol
        "C" -> R.drawable.colorless_mana_symbol

        "W/U" -> R.drawable.white_blue_mana_symbol
        "W/B" -> R.drawable.white_black_mana_symbol
        "U/B" -> R.drawable.blue_black_mana_symbol
        "U/R" -> R.drawable.blue_red_mana_symbol
        "B/R" -> R.drawable.black_red_mana_symbol
        "B/G" -> R.drawable.black_green_mana_symbol
        "R/G" -> R.drawable.red_green_mana_symbol
        "R/W" -> R.drawable.red_white_mana_symbol
        "G/W" -> R.drawable.green_white_mana_symbol
        "G/U" -> R.drawable.green_blue_mana_symbol

        "0" -> R.drawable.zero_mana_symbol
        "1" -> R.drawable.one_mana_symbol
        "2" -> R.drawable.two_mana_symbol
        "3" -> R.drawable.three_mana_symbol
        "4" -> R.drawable.four_mana_symbol
        "5" -> R.drawable.five_mana_symbol
        "6" -> R.drawable.six_mana_symbol
        "7" -> R.drawable.seven_mana_symbol
        "8" -> R.drawable.eight_mana_symbol
        "9" -> R.drawable.nine_mana_symbol
        "10" -> R.drawable.ten_mana_symbol
        "11" -> R.drawable.eleven_mana_symbol
        "12" -> R.drawable.twelve_mana_symbol
        "13" -> R.drawable.thirteen_mana_symbol
        "14" -> R.drawable.fourteen_mana_symbol
        "15" -> R.drawable.fifteen_mana_symbol
        "16" -> R.drawable.sixteen_mana_symbol
        "17" -> R.drawable.seventeen_mana_symbol
        "18" -> R.drawable.eighteen_mana_symbol
        "19" -> R.drawable.nineteen_mana_symbol
        "20" -> R.drawable.twenty_mana_symbol

        "X" -> R.drawable.x_mana_symbol

        "P" -> R.drawable.p_mana_symbol
        "W/P" -> R.drawable.wp_mana_symbol
        "U/P" -> R.drawable.up_mana_symbol
        "B/P" -> R.drawable.bp_mana_symbol
        "R/P" -> R.drawable.rp_mana_symbol
        "G/P" -> R.drawable.gp_mana_symbol
        "W/U/P" -> R.drawable.wup_mana_symbol
        "W/B/P" -> R.drawable.wbp_mana_symbol
        "U/B/P" -> R.drawable.ubp_mana_symbol
        "U/R/P" -> R.drawable.urp_mana_symbol
        "B/R/P" -> R.drawable.brp_mana_symbol
        "B/G/P" -> R.drawable.bgp_mana_symbol
        "R/G/P" -> R.drawable.rgp_mana_symbol
        "R/W/P" -> R.drawable.rwp_mana_symbol
        "G/W/P" -> R.drawable.gwp_mana_symbol
        "G/U/P" -> R.drawable.gup_mana_symbol

        "2W" -> R.drawable.two_white_mana_symbol
        "2U" -> R.drawable.two_blue_mana_symbol
        "2B" -> R.drawable.two_black_mana_symbol
        "2R" -> R.drawable.two_red_mana_symbol
        "2G" -> R.drawable.two_green_mana_symbol

        "CHAOS" -> R.drawable.chaos

        "PW" -> R.drawable.pw
        "TK" -> R.drawable.tk
        "E" -> R.drawable.energy
        "T" -> R.drawable.tap
        "Q" -> R.drawable.q
        "S" -> R.drawable.snow
        else -> R.drawable.two_mana_symbol
    }
}

fun languageToResource(lang: String): Int {
    return when (lang) {
        "de" -> R.string.de
        "en" -> R.string.en
        "fr" -> R.string.fr
        "it" -> R.string.it
        "la" -> R.string.la
        "pt" -> R.string.pt
        else -> R.string.unsupported
    }
}