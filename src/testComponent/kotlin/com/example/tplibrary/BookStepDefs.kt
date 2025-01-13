package com.example.tplibrary

import io.cucumber.java.fr.Alors
import io.cucumber.java.fr.Etantdonné
import io.cucumber.java.fr.Quand
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookStepDefs {
    private var response: Response? = null
    private var livreTitre: String = ""
    private var livreAuteur: String = ""

    init {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = 8080
    }

    @Etantdonné("un livre avec le titre {string} et l'auteur {string}")
    fun unLivreAvecLeTitreEtLAuteur(titre: String, auteur: String) {
        livreTitre = titre
        livreAuteur = auteur
    }

    @Quand("je crée le livre")
    fun jeCreeLeLivre() {
        val corps = """{"title": "$livreTitre", "author": "$livreAuteur"}"""
        response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(corps)
            .post("/books")
    }

    @Alors("le livre est ajouté avec succès")
    fun leLivreEstAjouteAvecSucces() {
        assertEquals(200, response?.statusCode)
        val message = response?.body?.asString()
        assertTrue(message?.contains("Book added successfully!") == true)
    }
}
