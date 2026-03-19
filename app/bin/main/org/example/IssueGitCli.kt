package org.example

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.github.ajalt.mordant.terminal.Terminal

fun displayStaticMode(
    terminal: Terminal,
    repos: List<RepoData>,
    currentUser: String?
) {
    terminal.println(
        "\n" + yellow("   Non-interactive terminal detected. Showing all repos.\n")
    )
    if (currentUser != null) {
        terminal.println(gray("  Logged in as: ") + (bold + brightWhite)("$currentUser\n"))
    }

    for (repo in repos) {
        terminal.println(buildIssueTable(terminal, repo, currentUser))
        terminal.println()
    }
}

fun main() {
    val terminal = Terminal()

    val header = (bold + cyan)("""
  ╔═════════════════════════════════════════════════════════╗
  ║               issue-git-cli  |  Stand-Up                ║
  ║       Team Issue Tracker CLI v2.0 made by Gibran        ║
  ╚═════════════════════════════════════════════════════════╝
""".trimIndent())

    terminal.println(header)
    terminal.println(gray("  Fetching your repositories and issues...\n"))

    val currentUser = GitHubClient.fetchAuthenticatedUser()?.login

    val repos = GitHubClient.fetchUserRepos()
    if (repos.isEmpty()) {
        terminal.println(red("  No repositories found. Check your token and network connection."))
        return
    }

    val activeRepos = repos.filter { it.openIssuesCount > 0 }
    if (activeRepos.isEmpty()) {
        terminal.println(green("  None of your ${repos.size} repositories have open issues. All clean! "))
        return
    }

    terminal.println(
        gray("  Found ") + bold("${activeRepos.size}") +
        gray(" repo(s) with open issues out of ${repos.size} total.")
    )
    terminal.println(gray("  Fetching issues for each repo...\n"))

    val repoDataList = activeRepos.map { repo ->
        val issues = GitHubClient.fetchActiveIssues(
            repoOwner = repo.owner.login,
            repoName = repo.name
        )
        terminal.println(gray("   ${repo.fullName} (${issues.size} issues)"))
        RepoData(repo.fullName, issues)
    }

    if (repoDataList.isEmpty()) {
        terminal.println(green("\n  No issues found across any repos. All clean! "))
        return
    }

    val isInteractive = InteractiveViewer.start(terminal, repoDataList, currentUser, header.toString())

    if (!isInteractive) {
        displayStaticMode(terminal, repoDataList, currentUser)
    }
}
