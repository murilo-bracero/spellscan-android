package com.example.spellscan.repository

import android.content.Context
import androidx.room.Room
import com.example.spellscan.db.CardCacheDatabase
import com.example.spellscan.db.entity.CardEntity
import com.spellscan.proto.CardResponse

class CardCacheRepository(context: Context) {

    private val db: CardCacheDatabase

    init {
        db = Room.databaseBuilder(
            context,
            CardCacheDatabase::class.java,
            "spellscan-database"
        )
            .build()
    }

    suspend fun save(card: CardResponse) {

        val entity = CardEntity(
            card.id,
            card.name,
            card.cost,
            card.type,
            card.set,
            card.lang,
            card.imageUrl
        )

        db.cardDao().save(entity)
    }

    suspend fun findAll(): List<CardEntity> {
        return db.cardDao().findAll()
    }

    suspend fun findByNameAndTypeAndSet(name: String, type: String, set: String): CardEntity? {
        return db.cardDao().findByNameAndTypeAndSet(name, type, set)
    }

    suspend fun delete(card: CardEntity) {
        db.cardDao().delete(card)
    }

    companion object {

        @Volatile
        private var instance: CardCacheRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: CardCacheRepository(context).also { instance = it }
        }
    }
}