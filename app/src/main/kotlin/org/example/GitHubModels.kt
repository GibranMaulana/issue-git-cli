package org.example

import com.google.gson.annotations.SerializedName

data class Assignee(
    val login: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?
)

data class Label(
    val name: String,
    val color: String
)

data class GitHubIssue(
    val number: Int,
    val title: String,
    val state: String,
    val body: String?,
    val assignee: Assignee?,
    val labels: List<Label>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class RepoOwner(
    val login: String
)

data class GitHubRepo(
    @SerializedName("full_name")
    val fullName: String,
    val name: String,
    val owner: RepoOwner,
    @SerializedName("open_issues_count")
    val openIssuesCount: Int,
    val private: Boolean
)
