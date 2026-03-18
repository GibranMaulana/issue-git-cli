package org.example

val RESET   = "\u001b[0m"
val BOLD    = "\u001b[1m"
val DIM     = "\u001b[2m"

val RED     = "\u001b[31m"
val GREEN   = "\u001b[32m"
val YELLOW  = "\u001b[33m"
val CYAN    = "\u001b[36m"
val WHITE   = "\u001b[37m"

val BG_RED  = "\u001b[41m"
val BG_CYAN = "\u001b[46m"

val HEADER = """
$CYAN$BOLD
  ╔═══════════════════════════════════════════╗
  ║         kt-issue  *  Stand-Up            ║
  ║       Team Issue Tracker CLI v1.0        ║
  ╚═══════════════════════════════════════════╝
$RESET
""".trimIndent()

fun renderTicket(issue: GitHubIssue): String {
    val assigneeName: String = issue.assignee?.login ?: "Unassigned"

    val isBug = issue.labels.any { it.name.equals("bug", ignoreCase = true) }

    val status: String = if (isBug) {
        "BUG"
    } else if (issue.assignee != null) {
        "In Progress"
    } else {
        "Needs Attention"
    }

    val icon: String = if (status == "BUG") {
        "$RED$BOLD[!!]$RESET"
    } else if (status == "In Progress") {
        "$GREEN[>>]$RESET"
    } else {
        "$YELLOW[??]$RESET"
    }

    val coloredStatus: String = if (status == "BUG") {
        "$RED$BOLD$status$RESET"
    } else if (status == "In Progress") {
        "$GREEN$status$RESET"
    } else {
        "$YELLOW$status$RESET"
    }

    val coloredAssignee = if (assigneeName == "Unassigned") {
        "$DIM$assigneeName$RESET"
    } else {
        "$WHITE$BOLD$assigneeName$RESET"
    }

    val num = "#${issue.number}".padEnd(6)
    val statusPad = coloredStatus.padEnd(30)

    return "  $icon $num $DIM|$RESET $statusPad $DIM|$RESET $coloredAssignee $DIM|$RESET ${issue.title}"
}

fun printDevWorkload(allIssues: List<Pair<String, GitHubIssue>>, vararg devNames: String) {
    println("\n${CYAN}${BOLD}  -- Developer Workload Filter --${RESET}")
    println("  ${DIM}Filtering for: ${devNames.joinToString(", ")}${RESET}\n")

    for (devName in devNames) {
        val devIssues = allIssues.filter {
            it.second.assignee?.login.equals(devName, ignoreCase = true)
        }

        val issueCount = devIssues.size
        val countColor = if (issueCount == 0) GREEN else YELLOW
        println("  $WHITE$BOLD$devName$RESET $DIM->$RESET $countColor$issueCount ticket(s)$RESET")

        for ((repoName, issue) in devIssues) {
            println("    $DIM$repoName$RESET ${renderTicket(issue)}")
        }
    }
    println()
}

// ──────────────────────────────────────────────────────────
// Main
// ──────────────────────────────────────────────────────────

fun main() {
    println(HEADER)
    println("  ${DIM}Fetching your repositories (owner + collaborator)...${RESET}\n")

    val repos = GitHubClient.fetchUserRepos()

    if (repos.isEmpty()) {
        println("  ${RED}No repositories found. Check your token and network connection.${RESET}")
        return
    }

    val activeRepos = repos.filter { it.openIssuesCount > 0 }

    if (activeRepos.isEmpty()) {
        println("  ${GREEN}None of your ${repos.size} repositories have open issues. All clean!${RESET}")
        return
    }

    println("  Found ${BOLD}${activeRepos.size}${RESET} repo(s) with open issues out of ${repos.size} total.\n")

    val allIssues = mutableListOf<Pair<String, GitHubIssue>>()

    for (repo in activeRepos) {
        println("  ${CYAN}${BOLD}${repo.fullName}${RESET} ${DIM}(${repo.openIssuesCount} open)${RESET}")
        println("  ${DIM}${"─".repeat(50)}${RESET}")

        val issues = GitHubClient.fetchActiveIssues(
            repoOwner = repo.owner.login,
            repoName  = repo.name
        )

        if (issues.isEmpty()) {
            println("  ${DIM}(no issues returned)${RESET}\n")
            continue
        }

        for (issue in issues) {
            println(renderTicket(issue))
            allIssues.add(Pair(repo.fullName, issue))
        }
        println()
    }

    val assignees = allIssues
        .mapNotNull { it.second.assignee?.login }
        .distinct()
        .toTypedArray()

    if (assignees.isNotEmpty()) {
        printDevWorkload(allIssues, *assignees)
    }
}
