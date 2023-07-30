package com.charleex.vidgenius.youtube.youtube.video

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.StoredCredential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory

import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
 * Shared class used by every sample. Contains methods for authorizing a user and caching credentials.
 */
object Auth {
    val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
    val JSON_FACTORY: JsonFactory = JacksonFactory()
    private val CREDENTIALS_DIRECTORY = ".oauth-credentials"

    @Throws(IOException::class)
    fun authorize(scopes: List<String>, credentialDatastore: String): Credential {
        val clientSecretReader = InputStreamReader(Auth::class.java.getResourceAsStream("/client_secrets.json"))
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader)

        if (clientSecrets.details.clientId.startsWith("Enter") || clientSecrets.details.clientSecret.startsWith("Enter ")) {
            println("Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential " + "into src/main/resources/client_secrets.json")
            System.exit(1)
        }

        val fileDataStoreFactory = FileDataStoreFactory(File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY))
        val datastore = fileDataStoreFactory.getDataStore<StoredCredential>(credentialDatastore)

        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
                .setCredentialDataStore(datastore)
                .build()

        val localReceiver = LocalServerReceiver.Builder().setPort(8080).build()
        return AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user")
    }
}
