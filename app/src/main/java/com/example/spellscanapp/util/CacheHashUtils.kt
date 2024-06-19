package com.example.spellscanapp.util

import com.example.spellscanapp.model.CacheCallEnum
import java.security.MessageDigest

fun buildCacheHash(call: CacheCallEnum, vararg params: String): String {
    val base = "${call.name}:${params.joinToString(":")}"
    return sha1(base)
}

private fun sha1(data: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    val digest = md.digest(data.toByteArray())
    return convertToHex(digest)
}

private fun convertToHex(data: ByteArray): String {
    val buf = StringBuilder()
    data.forEach {
        var hb = it.toInt() ushr 4 and 0x0F
        var th = 0
        do {
            buf.append(if (hb in 0..9) ('0'.code + hb).toChar() else ('a'.code + (hb - 10)).toChar())
            hb = it.toInt() and 0x0F
        } while (th++ < 1)
    }
    return buf.toString()
}
