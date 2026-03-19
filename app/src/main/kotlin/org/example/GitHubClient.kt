package org.example

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.github.cdimascio.dotenv.dotenv
import java.net.URI
import java.io.File
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object GitHubClient {
	 private val jarFile = File(GitHubClient::class.java.protectionDomain.codeSource.location.toURI())
    
    private val executionDir = File(jarFile.parentFile.parentFile, "bin").absolutePath
    private val dotenv =
        dotenv {
				//uncomment for Compiling the executables
				// directory = executionDir
            ignoreIfMissing = true
        }
	
    private val token: String? = dotenv["GITHUB_TOKEN"]
    private val gson: Gson = Gson()
    private val client: HttpClient =
        HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()

    private fun apiGet(url: String): String? {
        if (token.isNullOrBlank()) {
            println("GITHUB_TOKEN is not set. Please add it to your .env file.")
            return null
        }

        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer $token")
                .GET()
                .build()

        return try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == 200) {
                response.body()
            } else {
                println("GitHub API returned status ${response.statusCode()} for $url")
                null
            }
        } catch (e: Exception) {
            println("Network error: ${e.message}")
            null
        }
    }

    fun fetchUserRepos(): List<GitHubRepo> {
        val url = "https://api.github.com/user/repos?affiliation=owner,collaborator&sort=updated&per_page=100"
        val json = apiGet(url) ?: return emptyList()

        val repoType = object : TypeToken<List<GitHubRepo>>() {}.type
        return gson.fromJson(json, repoType)
    }

    fun fetchAuthenticatedUser(): GitHubUser? {
        val json = apiGet("https://api.github.com/user") ?: return null
        return gson.fromJson(json, GitHubUser::class.java)
    }

    fun fetchActiveIssues(
        repoOwner: String,
        repoName: String = "internal-finance-app",
    ): List<GitHubIssue> {
        val url = "https://api.github.com/repos/$repoOwner/$repoName/issues?state=open"
        val json = apiGet(url) ?: return emptyList()

        val issueType = object : TypeToken<List<GitHubIssue>>() {}.type
        return gson.fromJson(json, issueType)
    }
}
