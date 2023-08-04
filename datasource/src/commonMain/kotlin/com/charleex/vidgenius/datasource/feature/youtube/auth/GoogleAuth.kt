package com.charleex.vidgenius.datasource.feature.youtube.auth

import co.touchlab.kermit.Logger
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.StoredCredential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.util.store.DataStore
import com.google.api.client.util.store.FileDataStoreFactory
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
 * Shared class used by every sample. Contains methods for authorizing a user and caching credentials.
 */
interface GoogleAuth {

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param scopes              list of scopes needed to run youtube upload.
     * @param credentialDatastore name of the credential datastore to cache OAuth tokens
     */
    fun authorize(scopes: List<String>, credentialDatastore: String): Credential

    fun signout(credentialDatastore: String)
}

internal class GoogleAuthImpl(
    private val logger: Logger,
    private val httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
    private val credentialDirectory: String,
) : GoogleAuth {
    @Throws(IOException::class)
    override fun authorize(scopes: List<String>, credentialDatastore: String): Credential {
        logger.d { "Authorizing ${scopes}..." }
        val clientSecrets = getGoogleClientSecrets()

        val datastore = getDataStore(credentialDatastore)
        val flow = GoogleAuthorizationCodeFlow
            .Builder(httpTransport, jsonFactory, clientSecrets, scopes)
            .setCredentialDataStore(datastore)
            .build()

        val localReceiver = getLocalServerReceiver()
        return AuthorizationCodeInstalledApp(flow, localReceiver)
            .authorize("user")
    }

    override fun signout(credentialDatastore: String) {
        val datastore = getDataStore(credentialDatastore)
        datastore.clear()
    }

    // Build the local server and bind it to port 8080
    private fun getLocalServerReceiver(): LocalServerReceiver? {
        return LocalServerReceiver.Builder().setPort(8080).build()
    }

    // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
    private fun getDataStore(credentialDatastore: String): DataStore<StoredCredential> {
        val dataStoreDir = File(System.getProperty("user.home") + "/" + credentialDirectory)
        val fileDataStoreFactory = FileDataStoreFactory(dataStoreDir)
        return fileDataStoreFactory.getDataStore(credentialDatastore) ?: error("Datastore not found: $credentialDatastore")
    }

    private fun getGoogleClientSecrets(): GoogleClientSecrets {
        val configName = "/youtube.json"
        val inputStreamReader = this::class.java
            .getResourceAsStream(configName)
            ?.let { InputStreamReader(it) }
            ?: error("Resource not found: $configName")
        return GoogleClientSecrets.load(jsonFactory, inputStreamReader)
    }
}