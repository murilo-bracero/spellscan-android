package com.example.spellscan.repository

abstract class LocalRepository<ID, T> protected constructor() {

    protected val db = HashMap<ID, T>()

    abstract fun save(card: T)

    open fun findAll(): ArrayList<T> {
        return db.values.toCollection(ArrayList())
    }

    open fun reset() {
        db.clear()
    }
}