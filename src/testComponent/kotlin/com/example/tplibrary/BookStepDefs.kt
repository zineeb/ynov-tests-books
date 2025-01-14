package com.example.tplibrary

import io.cucumber.java.Before
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

    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost:8080"

        // Attendre que l'application soit prête
        var serverReady = false
        repeat(10) {
            try {
                RestAssured.get("/books").then().statusCode(200)
                serverReady = true
                return@repeat
            } catch (e: Exception) {
                println("Attente du démarrage du serveur...")
                Thread.sleep(2000)
            }
        }
        require(serverReady) { "Le serveur n'est pas prêt après plusieurs tentatives." }
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
        assertEquals(200, response?.statusCode, "Le statut attendu est 200")
        val message = response?.body?.asString()
        assertTrue(message?.contains("Book added successfully!") == true, "Message reçu : $message")
    }

    @Alors("une erreur indiquant {string} est renvoyée")
    fun uneErreurIndiquantEstRenvoyee(messageErreur: String) {
        assertEquals(400, response?.statusCode, "Le statut attendu est 400")
        val messageRecu = response?.body?.asString()
        assertTrue(messageRecu?.contains(messageErreur) == true, "Le message attendu est $messageErreur, reçu : $messageRecu")
    }

    @Etantdonné("une collection de livres existante")
    fun uneCollectionDeLivresExistante() {
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body("""{"title": "Z Livre", "author": "Auteur Z"}""")
            .post("/books")
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body("""{"title": "A Livre", "author": "Auteur A"}""")
            .post("/books")
    }

    @Quand("je demande la liste des livres")
    fun jeDemandeLaListeDesLivres() {
        response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .get("/books")
    }

    @Alors("la liste des livres est renvoyée triée par titre")
    fun laListeDesLivresEstRenvoyeeTrieeParTitre() {
        assertEquals(200, response?.statusCode, "Le statut attendu est 200")
        val titres = response?.jsonPath()?.getList<String>("title")
        assertTrue(titres == titres?.sorted(), "Les titres ne sont pas triés : $titres")
    }
}
