package com.bitflip.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Checks GitHub Releases for a newer version of the app.
 * Uses only the standard Android/Java networking stack — no extra libraries.
 * Fails silently when offline so the core app experience is never affected.
 */

data class AppUpdate(
    val latestVersion: String,
    val releaseUrl: String,
    val releaseNotes: String
)

object UpdateChecker {

    private const val GITHUB_API_URL =
        "https://api.github.com/repos/cybersaad/BitFlip/releases/latest"

    /**
     * Compares [currentVersion] (e.g. "3.0") against the latest GitHub Release tag.
     * Returns an [AppUpdate] when a newer release exists, or `null` otherwise.
     * Never throws — returns null on any network/parse error.
     */
    suspend fun checkForUpdate(currentVersion: String): AppUpdate? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(GITHUB_API_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode != 200) return@withContext null

                val response = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val json = JSONObject(response)
                val tagName = json.getString("tag_name")           // e.g. "v3.1"
                val latestVersion = tagName.removePrefix("v")      // "3.1"
                val releaseUrl = json.getString("html_url")
                val releaseNotes = json.optString("body", "")

                if (isNewerVersion(latestVersion, currentVersion)) {
                    AppUpdate(latestVersion, releaseUrl, releaseNotes)
                } else {
                    null
                }
            } catch (_: Exception) {
                null   // No internet, API error, parse error — just skip silently
            }
        }
    }

    /**
     * Semantic version comparison: "3.1" > "3.0", "4.0" > "3.9", etc.
     */
    private fun isNewerVersion(remote: String, local: String): Boolean {
        val remoteParts = remote.split(".").map { it.toIntOrNull() ?: 0 }
        val localParts = local.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLen = maxOf(remoteParts.size, localParts.size)

        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val l = localParts.getOrElse(i) { 0 }
            if (r > l) return true
            if (r < l) return false
        }
        return false
    }
}
