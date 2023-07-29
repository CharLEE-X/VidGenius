package com.charleex.vidgenius.yt.auth

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
}

internal class GoogleAuthImpl(
    private val logger: Logger,
    private val httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
    private val credentialDirectory: String,
) : GoogleAuth {
    @Throws(IOException::class)
    override fun authorize(scopes: List<String>, credentialDatastore: String): Credential {
        val clientSecrets = getGoogleClientSecrets()

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (
            clientSecrets != null &&
            (
                    clientSecrets.details.clientId.startsWith("Enter") ||
                            clientSecrets.details.clientSecret.startsWith("Enter ")
                    )
        ) {
            logger.e {
                "Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential " +
                        "into src/main/resources/client_secrets.json"
            }
        }

        val datastore = getDataStore(credentialDatastore)
        val flow = GoogleAuthorizationCodeFlow
            .Builder(httpTransport, jsonFactory, clientSecrets, scopes)
            .setCredentialDataStore(datastore)
            .build()

        val localReceiver = getLocalServerReceiver()
        return AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user")
    }

    // Build the local server and bind it to port 8080
    private fun getLocalServerReceiver(): LocalServerReceiver? {
        return LocalServerReceiver.Builder().setPort(8080).build()
    }

    // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
    private fun getDataStore(credentialDatastore: String): DataStore<StoredCredential>? {
        val fileDataStoreFactory =
            FileDataStoreFactory(File(System.getProperty("user.home") + "/" + credentialDirectory))
        return fileDataStoreFactory.getDataStore(credentialDatastore)
    }

    private fun getGoogleClientSecrets(): GoogleClientSecrets? {
//        val file = File(GOOGLE_CREDENTIALS_JSON)
//        val fileInputStream = file.inputStream()
        val inputStream = this::class.java
            .getResourceAsStream("/client_secrets.json")
//            ?: error("No client_secrets.json found in resources")
        val inputStreamReader = InputStreamReader(inputStream)
        return GoogleClientSecrets.load(jsonFactory, inputStreamReader)
    }
}


private val GOOGLE_CREDENTIALS_JSON =
    "/Users/adrianwitaszak/.config/gcloud/application_default_credentials.json"
