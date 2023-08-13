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
import com.google.common.collect.Lists
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
 * Shared class used by every sample. Contains methods for authorizing a user and caching credentials.
 */
interface GoogleAuth {
    fun authorizeYouTube(config: String): Credential
    fun signOut(channelId: String)
}

internal class GoogleAuthImpl(
    private val logger: Logger,
    private val appDataDir: String,
    private val httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
    private val credentialDirectory: String,
) : GoogleAuth {
    @Throws(IOException::class)
    override fun authorizeYouTube(config: String): Credential {
        logger.d { "Authorizing..." }
        val clientSecrets = getGoogleClientSecrets(config)
        val scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube")
        val datastore = getDataStore("1")
        val flow = GoogleAuthorizationCodeFlow
            .Builder(httpTransport, jsonFactory, clientSecrets, scopes)
            .setCredentialDataStore(datastore)
            .build()

        val localReceiver = getLocalServerReceiver()
        val credential: Credential = AuthorizationCodeInstalledApp(flow, localReceiver)
            .authorize("user")

        logger.i { "Authorization successful." }
        return credential
    }

    override fun signOut(channelId: String) {
        val datastore = getDataStore(channelId)
        datastore.clear()
    }

    // Build the local server and bind it to port 8080
    private fun getLocalServerReceiver(): LocalServerReceiver {
        return LocalServerReceiver.Builder().setPort(8080).build()
            ?: error("LocalServerReceiver not found")
    }

    // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
    private fun getDataStore(credentialDatastore: String): DataStore<StoredCredential> {
        val dataStoreDir = File("$appDataDir/$credentialDirectory")
        val fileDataStoreFactory = FileDataStoreFactory(dataStoreDir)
        return fileDataStoreFactory.getDataStore(credentialDatastore)
            ?: error("Datastore not found: $credentialDatastore")
    }

    private fun getGoogleClientSecrets(secretsFile: String): GoogleClientSecrets {
        val inputStreamReader = this::class.java
            .getResourceAsStream(secretsFile)
            ?.let { InputStreamReader(it) }
            ?: error("Resource not found: $secretsFile")
        return GoogleClientSecrets.load(jsonFactory, inputStreamReader)
    }
}
