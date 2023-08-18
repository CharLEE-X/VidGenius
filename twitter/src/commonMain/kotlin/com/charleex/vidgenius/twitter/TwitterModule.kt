package com.charleex.vidgenius.twitter

import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import twitter4j.v2

// https://github.com/takke/twitter4j-v2

// Consumer keys
val consumerKey = "HYTitwAjrNvdXO0hxb1i02MWw"
val consumerSecret = "TibYOteHlxbBcpPHVAOmafjI5a49UlXL9W9CGBPu7Gktkk1MbJ"

// Authentication tokens
val bearerToken =
    "AAAAAAAAAAAAAAAAAAAAAHbGpQEAAAAA5Rjh9i7cC%2BaGZMra6gPJw9%2B0SMU%3DUW0GKWKmLSWDudIYVrWxRjLjSInPHGl02poUeRBNNdOYQeq75Q"

val accessToken = "1692081900557320192-mHcnskjl5EUqFzDsvptYys1RqJn57D"
val accessTokenSecret = "FREW5WXCuruavo5PIul2j5PdY4GLvYC1Yg5qLtPj7OjOH"

// OAuth2
val clientId = "YmhIVUZibWlMSGtxSll3bndKXzI6MTpjaQ"
val clientSecret = "VcdJeHVt3H76ocXMQUBw9kaU-LEQe7XONqbOcTOfSjYTePsukv"

const val ANIMALS_CHANNEL_LINK = "https://www.youtube.com/channel/UCmWNmg5PyF1VKUCttVcVzuw?sub_confirmation=1"

fun createNewTweet() {
    val conf = ConfigurationBuilder()
        .setJSONStoreEnabled(true)
        .setOAuthConsumerKey(consumerKey)
        .setOAuthConsumerSecret(consumerSecret)
        .setOAuthAccessToken(accessToken)
        .setOAuthAccessTokenSecret(accessTokenSecret)
        .setOAuth2Scope("tweet.read.write")
        .build()

    val twitter = TwitterFactory(conf).instance

    val createTweetResponse = twitter.v2.createTweet(
        text = """
            🐾📹 Love funny animal moments? Join the laughter on my YouTube channel! 🤣🐶🐱
            Hey friends! 🌟 If you're all about adorable animals and hilarious antics, you're in for a treat. 🎉🐾 I've started a YouTube channel to share the funniest clips of our furry pals.
            
            Subscribe now, please: $ANIMALS_CHANNEL_LINK
            
            Let's grow the laughter together - hit that "Subscribe" button and share with fellow animal lovers! 🐾🚀
            
            #FunnyAnimalVideos #funnyvideos #trynottolaugh
        """.trimIndent(),
    )

    println("Response: $createTweetResponse")
}
