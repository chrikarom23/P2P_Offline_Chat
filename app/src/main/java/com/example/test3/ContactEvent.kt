package com.example.test3

sealed interface ContactEvent {
    object ShowConvo: ContactEvent
    data class IntoConvo(val DeviceName: String):ContactEvent
    data class SortConvo(val sortType: sortType):ContactEvent
    data class DeleteConvo(val DeviceName: String): ContactEvent
}