package com.charleex.vidgenius.open_ai

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.open_ai.model.ContentInfo
import com.charleex.vidgenius.open_ai.model.Description
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

interface OpenAiRepository {
    suspend fun getContextFromDescriptions(
        descriptions: List<String>,
        categoryQuery: String,
    ): String?

    suspend fun getContentInfo(
        descriptions: List<String>,
        categoryQuery: String,
        languageCodes: List<String>,
    ): ContentInfo?

    suspend fun generateTitle(
        description: String?,
        categoryQuery: String,
        languageCodes: String,
    ): String?

    suspend fun generateDescription(
        description: String?,
        categoryQuery: String,
        languageCodes: String,
        channelLink: String,
    ): Pair<String?, List<String>>

    suspend fun translateText(
        text: String,
        targetLanguage: String,
    ): String?
}

internal class OpenAiRepositoryImpl(
    private val logger: Logger,
    private val chatService: ChatService,
) : OpenAiRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun getContextFromDescriptions(
        descriptions: List<String>,
        categoryQuery: String,
    ): String? = try {
        chatService.simpleChat(
            message = queryDescriptionsContext(categoryQuery, descriptions)
        )
            .also { logger.i("ANSWER: $it") }
            ?: error("No message found")
    } catch (e: Exception) {
        logger.e("Failed to generate context", e)
        null
    }

    override suspend fun getContentInfo(
        descriptions: List<String>,
        categoryQuery: String,
        languageCodes: List<String>,
    ): ContentInfo? = try {
        val chatCompletion = chatService.simpleChat(
            message = contentInfoQuery(
                categoryQuery = categoryQuery,
                descriptions = descriptions,
                languageCodes = languageCodes
            )
        ) ?: error("No message found")
        logger.i("ANSWER: $chatCompletion")
        json.decodeFromString(ContentInfo.serializer(), chatCompletion)
    } catch (e: Exception) {
        logger.e("Failed to generate title", e)
        null
    }

    override suspend fun generateTitle(
        description: String?,
        categoryQuery: String,
        languageCodes: String,
    ): String? = try {
        chatService.simpleChat(
            message = queryTitle(
                description = description,
                categoryQuery = categoryQuery,
                languageCode = languageCodes
            )
        )
            ?.replace("\"", "")
            .also { logger.i("ANSWER: $it") }
    } catch (e: Exception) {
        logger.e("Failed to generate title", e)
        null
    }

    override suspend fun generateDescription(
        description: String?,
        categoryQuery: String,
        languageCodes: String,
        channelLink: String,
    ): Pair<String?, List<String>> = try {
        val answer = chatService.simpleChat(
            message = queryDescription(
                description = description,
                categoryQuery = categoryQuery,
                languageCode = languageCodes,
                channelLink = channelLink,
            )
        ) ?: error("No message found")
        logger.i("ANSWER: $answer")

        val descriptionItem = json.decodeFromString(Description.serializer(), answer)

        val desc = "${descriptionItem.short}\n\n" +
                "${descriptionItem.long}\n\n" +
                "${descriptionItem.subscribe}\n\n" +
                descriptionItem.tags
        val tags = descriptionItem.tags.split(" ").map { it.trim() }
        Pair(desc, tags)
    } catch (e: Exception) {
        logger.e("Failed to generate title", e)
        Pair(null, emptyList())
    }

    override suspend fun translateText(text: String, targetLanguage: String): String? {
        return try {
            chatService.simpleChat(
                message = translateTextQuery(
                    text = text,
                    targetLanguage = targetLanguage,
                )
            ) ?: error("No message found")
                .also { logger.i("ANSWER: $it") }
        } catch (e: Exception) {
            logger.e("Failed to generate title", e)
            null
        }
    }
}

internal class OpenAiRepositoryDebug : OpenAiRepository {
    override suspend fun getContextFromDescriptions(
        descriptions: List<String>,
        categoryQuery: String,
    ): String {
        delay(100)
        return "description context"
    }

    override suspend fun getContentInfo(
        descriptions: List<String>,
        categoryQuery: String,
        languageCodes: List<String>,
    ): ContentInfo {
        delay(300)
        return ContentInfo()
    }

    override suspend fun generateTitle(
        description: String?,
        categoryQuery: String,
        languageCodes: String,
    ): String? {
        TODO("Not yet implemented")
    }

    override suspend fun generateDescription(
        description: String?,
        categoryQuery: String,
        languageCodes: String,
        channelLink: String,
    ): Pair<String?, List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun translateText(text: String, targetLanguage: String): String? {
        TODO("Not yet implemented")
    }
}

private fun translateTextQuery(text: String, targetLanguage: String): String =
"""
Translate text '$text' to language with code '$targetLanguage'. Don't add any extra comments!
"""

private fun queryTitle(
    description: String?,
    categoryQuery: String,
    languageCode: String,
): String = """
Generate title for Youtube video about '$categoryQuery' ${description ?: ""}  
    - in language with code '$languageCode'
    - include top SEO keywords for '$categoryQuery' category
    - put important keywords at the front and back
    - max length 60 characters
    - about 10 words
    - use power-words, emotion-words and Uncommon-words 
    - make it to reach 80 points on the 'Headline analyser'
    - don't put it in quotes so it is ready for copy and paste

- Power-words: [Selected, Crammed, Challenge, Greatest, Huge Gift, Fascinating, Surprise, Highest, Timely, Unique, Simple, Quality, Approved, Sensational, Mainstream, Advice, Strange, New, Unusual, Amazing, Genuine, Tremendous, Insider, Revealing, Helpful, Astonishing, Odd, Noted, Successful, Energy, Spotlight, Exploit, Excellent, Bottom Line, Better, Important, Exclusive, Special, Fundamentals, Scarce, Skill, Complete, Perspective, Practical, Opportunities, Reliable, Colorful, Weird, Sampler, Latest, Delivered, Value, Wealth, Simplified, Daring, Largest, Download, Imagination, Liberal, Shrewd, Compromise, Unlimited, Edge, Informative, Unsurpassed, Wanted, Direct, Simplistic, Sturdy, Introducing, Unlock, Terrific, Attractive, Affordable, Revisited, Urgent, Luxury, Competitive, Endorsed, Wonderful, How To, Startling, Under Priced, Suddenly, Sale, Hurry, Secrets, Growth, Special Offer, Technology, Soar, Big, Beautiful, Powerful, Colossal, Security, Gigantic, Popular, Sizable, Fortune, Just Arrived, Reward, Mammoth, Interesting, Tested, Quick, Absolutely Lowest, Guaranteed, Free, Easy, Lifetime, Destiny, Portfolio, Remarkable, Valuable, Unparalleled, Launching, Enormous, Love, Lavishly, Emerging, Announcing, Compare, Useful, Easily, Zinger, Unconditional, Quickly, Willpower, Surging, Magic, Breakthrough, Alert Famous, It's Here, Improved, Expert, Profitable, Bargain, The Truth About, Instructive, Last Chance, Miracle, Full, Survival, Discount, Monumental, Confidential, Proven, Strong, Now, Exciting, Sure Fire, Limited, Refundable, Bonanza, Delighted, Reduced, Revolutionary, Professional, High Tech, Obsession, Rare, Great, Innovative, Promising, Pioneering, Superior, Outstanding, Immediately, Focus, Ultimate, Last Minute, Authentic]
- Emotion-words: [Empower, Shameless, Hidden, Vindication, Money-grubbing, The Best, Insanely, Double, Revenge, Dumb, Sure, Overcome, Sinful, Swindle, Loathsome, Unbelievably, Dirty, Taboo, Fooled, Hope, Official, Fantastic, This Is, What This, For The First Time, Sniveling, Provocative, Unscrupulous, Searing, Insidious, Gullible, Helpless, Vibrant, Stuck Up, Horrific, Private, Freebie, When You, You See, Truth, Soaring, Marked Down, You Need To Know, Tailspin, Explode, It Looks Like A, Lurking, Under, Lust, Spine, Volatile, Help Are The, Greed, Smuggled, Scary, Priced, Irresistibly, Moneyback, Plunge, Hoax, Underhanded, Evil, You See What, Wicked, Prison, Tired, Drowning, Murder, Meltdown, Might Look Like A, Fleece, Embarrass, Looming, Pitfall, Illegal, Disinformation, Stupid, Pay Zero, Trap, Had Enough, Pluck, Six-figure, Poor, Disgusting, Wondrous, Painful, Pound, Flush, Vaporize, Surrender, Line, Whopping, Frantic, Triple, Money, Triumph, Risky, No Questions Asked, Research, Wounded, Fool, Treasure, Temporary Fix, Staggering, Rich, Disastrous, Thrilling, Smug, Quadruple, Energize, Inexpensive, Extra, Whip, Make You, Toxic, Victim, Fearless, Myths, Never Again, Reckoning, That Will Make You, What Happens When, Skyrocket, Tawdry, Viral, Sleazy, Is What Happens When, Of The, Invasion, Floundering, Vulnerable, Massive, Scream, Eye-opening, Ruthless, To Be, Tech, Fail, Uplifting, Destroy, Forgotten, In The World, Like A Normal, Tempting, Lies, Jackpot, Frenzy, Hypnotic, Spirit, Revolting, Refund, Lost, Piranha, Hate, Turn The Tables, Forbidden, Excited, Lying, Thing Ive Ever Seen, Spectacular, What Happened, Minute, Preposterous, Ironclad, What Happens, Unauthorized, This Is The, Targeted, You Need To, Peril, Undo, Unexpected, Obnoxious, Shellacking, Panic, Outlawed, Fulfill, Silly, Wanton, Devoted, Will Make You, Prize, Valor, Lick, Off-limits, Jail, Dollar, Happy, Results, The Ranking Of, Thrilled, The Most, Mired, How To Make, To The, Strangle, Tank, Looks Like A, Frightening, Jubilant, Grateful, Gambling, Fresh On The Mind, Unusually, Victory, Tantalizing, Jeopardy, Shatter, Pointless, Lonely, Plummet, Stunning, Privacy, Remarkably, Foul, Surprisingly, Profit, Withheld, Force-fed, The Reason Why Is, Teetering, Snob, On The, Slaughter, Reclaim, This Is What Happens, Know It All, In The, Feast, Rave, Jaw-dropping, Hack, Exposed, Lawsuit, Varify, Punish, Terror, Hazardous, Secure, Lunatic, Pummel, Smash, When You See, Famous, Plague, Try Before You Buy, Fire, That Will Make, Feeble, Offer, Refugee, Mistakes, Killer, Payback, Surge, Devastating, This Is What, Surprising, Played, Warning, Sick And Tired, Uncommonly, Worry, In A, No Good, Gift, Frugal, Grit, Luxurious, Pale, High, Epic, Protected, Nightmare, Seize, Hurricane, Faith, Lowest, Is The, Secutively, That Will, Mind-blowing]
- Uncommon-words: [First, See, Look, Awesome, Valentines, Years, Need, Way, Social, Little, Life, Old, Baby, More, Mind, Never, Reasons, Man, Made, One, Now, Thing, Really, Girl, Something, Being, Boy, Dog, World, Seen, Think, Video, Good, Here, Happened, Found, Make, Love, Beautiful, Best, Watch, Guy, New, Year, Want, People, Its, Out, Know, Media, Makes, Right, Facebook, Better, Ways, Heart, You'll, Down, Time, Photos, Actually]
        """.trimIndent()

private fun queryDescription(
    description: String?,
    categoryQuery: String,
    channelLink: String,
    languageCode: String,
): String = """
Generate description for Youtube video about '$categoryQuery' ${description ?: ""}:
- in language with code '$languageCode'
- generate it in four sections: short, long, subscribe and tags
- make short section to be 140 characters long, use power-words, emotion-words and Uncommon-words, include a lot of keywords for '$categoryQuery'  category, include a lot of SEO keywords for '$categoryQuery'  category
- long section should have about 300 characters and be engaging for the user, include a lot of keywords for '$categoryQuery'  category, use power-words, emotion-words and Uncommon-words 
- subscribe section should have about 100 characters and be engaging for the user and have link to the channel homepage '${channelLink}?sub_confirmation=1' and ask user to subscribe and click on the link
- hashtags section need to have 20 best hashtags for $categoryQuery category
- make it all to reach 80 points on the 'Headline analyser'
- add emojis so it looks pretty

- Power-words: [Selected, Crammed, Challenge, Greatest, Huge Gift, Fascinating, Surprise, Highest, Timely, Unique, Simple, Quality, Approved, Sensational, Mainstream, Advice, Strange, New, Unusual, Amazing, Genuine, Tremendous, Insider, Revealing, Helpful, Astonishing, Odd, Noted, Successful, Energy, Spotlight, Exploit, Excellent, Bottom Line, Better, Important, Exclusive, Special, Fundamentals, Scarce, Skill, Complete, Perspective, Practical, Opportunities, Reliable, Colorful, Weird, Sampler, Latest, Delivered, Value, Wealth, Simplified, Daring, Largest, Download, Imagination, Liberal, Shrewd, Compromise, Unlimited, Edge, Informative, Unsurpassed, Wanted, Direct, Simplistic, Sturdy, Introducing, Unlock, Terrific, Attractive, Affordable, Revisited, Urgent, Luxury, Competitive, Endorsed, Wonderful, How To, Startling, Under Priced, Suddenly, Sale, Hurry, Secrets, Growth, Special Offer, Technology, Soar, Big, Beautiful, Powerful, Colossal, Security, Gigantic, Popular, Sizable, Fortune, Just Arrived, Reward, Mammoth, Interesting, Tested, Quick, Absolutely Lowest, Guaranteed, Free, Easy, Lifetime, Destiny, Portfolio, Remarkable, Valuable, Unparalleled, Launching, Enormous, Love, Lavishly, Emerging, Announcing, Compare, Useful, Easily, Zinger, Unconditional, Quickly, Willpower, Surging, Magic, Breakthrough, Alert Famous, It's Here, Improved, Expert, Profitable, Bargain, The Truth About, Instructive, Last Chance, Miracle, Full, Survival, Discount, Monumental, Confidential, Proven, Strong, Now, Exciting, Sure Fire, Limited, Refundable, Bonanza, Delighted, Reduced, Revolutionary, Professional, High Tech, Obsession, Rare, Great, Innovative, Promising, Pioneering, Superior, Outstanding, Immediately, Focus, Ultimate, Last Minute, Authentic]
- Emotion-words: [Empower, Shameless, Hidden, Vindication, Money-grubbing, The Best, Insanely, Double, Revenge, Dumb, Sure, Overcome, Sinful, Swindle, Loathsome, Unbelievably, Dirty, Taboo, Fooled, Hope, Official, Fantastic, This Is, What This, For The First Time, Sniveling, Provocative, Unscrupulous, Searing, Insidious, Gullible, Helpless, Vibrant, Stuck Up, Horrific, Private, Freebie, When You, You See, Truth, Soaring, Marked Down, You Need To Know, Tailspin, Explode, It Looks Like A, Lurking, Under, Lust, Spine, Volatile, Help Are The, Greed, Smuggled, Scary, Priced, Irresistibly, Moneyback, Plunge, Hoax, Underhanded, Evil, You See What, Wicked, Prison, Tired, Drowning, Murder, Meltdown, Might Look Like A, Fleece, Embarrass, Looming, Pitfall, Illegal, Disinformation, Stupid, Pay Zero, Trap, Had Enough, Pluck, Six-figure, Poor, Disgusting, Wondrous, Painful, Pound, Flush, Vaporize, Surrender, Line, Whopping, Frantic, Triple, Money, Triumph, Risky, No Questions Asked, Research, Wounded, Fool, Treasure, Temporary Fix, Staggering, Rich, Disastrous, Thrilling, Smug, Quadruple, Energize, Inexpensive, Extra, Whip, Make You, Toxic, Victim, Fearless, Myths, Never Again, Reckoning, That Will Make You, What Happens When, Skyrocket, Tawdry, Viral, Sleazy, Is What Happens When, Of The, Invasion, Floundering, Vulnerable, Massive, Scream, Eye-opening, Ruthless, To Be, Tech, Fail, Uplifting, Destroy, Forgotten, In The World, Like A Normal, Tempting, Lies, Jackpot, Frenzy, Hypnotic, Spirit, Revolting, Refund, Lost, Piranha, Hate, Turn The Tables, Forbidden, Excited, Lying, Thing Ive Ever Seen, Spectacular, What Happened, Minute, Preposterous, Ironclad, What Happens, Unauthorized, This Is The, Targeted, You Need To, Peril, Undo, Unexpected, Obnoxious, Shellacking, Panic, Outlawed, Fulfill, Silly, Wanton, Devoted, Will Make You, Prize, Valor, Lick, Off-limits, Jail, Dollar, Happy, Results, The Ranking Of, Thrilled, The Most, Mired, How To Make, To The, Strangle, Tank, Looks Like A, Frightening, Jubilant, Grateful, Gambling, Fresh On The Mind, Unusually, Victory, Tantalizing, Jeopardy, Shatter, Pointless, Lonely, Plummet, Stunning, Privacy, Remarkably, Foul, Surprisingly, Profit, Withheld, Force-fed, The Reason Why Is, Teetering, Snob, On The, Slaughter, Reclaim, This Is What Happens, Know It All, In The, Feast, Rave, Jaw-dropping, Hack, Exposed, Lawsuit, Varify, Punish, Terror, Hazardous, Secure, Lunatic, Pummel, Smash, When You See, Famous, Plague, Try Before You Buy, Fire, That Will Make, Feeble, Offer, Refugee, Mistakes, Killer, Payback, Surge, Devastating, This Is What, Surprising, Played, Warning, Sick And Tired, Uncommonly, Worry, In A, No Good, Gift, Frugal, Grit, Luxurious, Pale, High, Epic, Protected, Nightmare, Seize, Hurricane, Faith, Lowest, Is The, Secutively, That Will, Mind-blowing]
- Uncommon-words: [First, See, Look, Awesome, Valentines, Years, Need, Way, Social, Little, Life, Old, Baby, More, Mind, Never, Reasons, Man, Made, One, Now, Thing, Really, Girl, Something, Being, Boy, Dog, World, Seen, Think, Video, Good, Here, Happened, Found, Make, Love, Beautiful, Best, Watch, Guy, New, Year, Want, People, Its, Out, Know, Media, Makes, Right, Facebook, Better, Ways, Heart, You'll, Down, Time, Photos, Actually]

Don't add any extra comments!

return response json format only without any extra notes:
{
   "short": "put content of short description here",
  "long": "put content of long description here",
  "subscribe": "put content of subscribe section with link here",
  "tags": "put 20 best SEO hashtags here"
}
        """.trimIndent()

private fun queryDescriptionsContext(
    categoryQuery: String,
    descriptions: List<String>,
) =
    "Here is a list of screenshot descriptions. Pick those related to the funny $categoryQuery videos, and return " +
            "them: $descriptions. If you cannot find an funny $categoryQuery videos choose one the most popular one."

private fun contentInfoQuery(
    categoryQuery: String,
    descriptions: List<String>,
    languageCodes: List<String>,
) = """
    Here is a list of screenshot descriptions for $categoryQuery videos for YouTube: $descriptions. 
    If no descriptions then use the category $categoryQuery.
    Generate it in languages $languageCodes and return it as json, don't add any extra text. 
    Make title to be catchy phrase and to have related emojis at the front and back of the title. 
    Make description SEO friendly, 100 words long and include best hashtags at the front of description.  
    Create 5 best ranking SEO tags.
    
    {
      "en-US": {
        "title": "generated title here",
        "description": "generated description here"
      },
      "es": {
        "title": "generated title here",
        "description": "generated description here"
      },
      "zh": {
        "title": "generated title here",
        "description": "generated description here"
      },
      "pt": {
        "title": "generated title here",
        "description": "generated description here"
      },
      "hi": {
        "title": "generated title here",
        "description": "generated description here"
      },
      "tags": [
        "generated tag with no hashtag",
        "generated tag with no hashtag",
        "generated tag with no hashtag",
        "generated tag with no hashtag",
        "generated tag with no hashtag",
      ]
    }

    RETURN JSON OBJECT ONLY!!!
""".trimIndent()
