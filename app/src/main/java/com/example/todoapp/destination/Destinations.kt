package com.example.todoapp.destination

interface Destinations {
    val route : String

}

object Splash : Destinations{
    override val route = "Splash"
}

object Home : Destinations{
    override val route = "Home"
}