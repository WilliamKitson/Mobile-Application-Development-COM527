package com.example.a4kitsw10com527

class Landmark(
    val name: String,
    val type: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    private var rooms: Int,
    val meals: Boolean
) {
    fun bookRoom() {
        rooms -= 1

        if (rooms < 0) {
            rooms = 0
        }
    }

    fun getRooms(): Int {
        return rooms
    }
}