package org.example

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal

data class RepoData(
    val fullName: String,
    val issues: List<GitHubIssue>
)

const val CLEAR_SCREEN = "\u001b[2J\u001b[H"
const val HIDE_CURSOR = "\u001b[?25l"
const val SHOW_CURSOR = "\u001b[?25h"

fun classifyIssue(issue: GitHubIssue): String {
    val isBug = issue.labels.any { it.name.equals("bug", ignoreCase = true) }
    return when {
        isBug -> "BUG"
        issue.assignee != null -> "In Progress"
        else -> "Needs Attention"
    }
}

fun buildIssueTable(
    terminal: Terminal,
    repo: RepoData,
    currentUser: String?
): com.github.ajalt.mordant.rendering.Widget {
    return table {
        captionTop(
            (bold + cyan)("  ${repo.fullName}") +
            gray("  (${repo.issues.size} open)")
        )
        header {
            style = bold.style
            row {
                cell("#") { align = TextAlign.CENTER }
                cell("Status") { align = TextAlign.CENTER }
                cell("Assignee") { align = TextAlign.CENTER }
                cell("Labels") { align = TextAlign.CENTER }
                cell("Title") { align = TextAlign.LEFT }
            }
        }
        body {
            for (issue in repo.issues) {
                val status = classifyIssue(issue)

                val styledStatus = when (status) {
                    "BUG" -> (bold + red)("BUG")
                    "In Progress" -> green("In Progress")
                    else -> yellow("Needs Attention")
                }

                val assigneeName = issue.assignee?.login ?: "Unassigned"
                val isYou = currentUser != null &&
                    assigneeName.equals(currentUser, ignoreCase = true)
                val styledAssignee = when {
                    assigneeName == "Unassigned" -> gray("—")
                    isYou -> (bold + brightWhite)("$assigneeName (you)")
                    else -> white(assigneeName)
                }

                val labelStr = if (issue.labels.isEmpty()) {
                    gray("—").toString()
                } else {
                    issue.labels.joinToString(", ") { label ->
                        when {
                            label.name.equals("bug", ignoreCase = true) -> red(label.name)
                            label.name.equals("enhancement", ignoreCase = true) -> cyan(label.name)
                            else -> magenta(label.name)
                        }.toString()
                    }
                }

                row {
                    cell(gray("#${issue.number}")) { align = TextAlign.CENTER }
                    cell(styledStatus) { align = TextAlign.CENTER }
                    cell(styledAssignee) { align = TextAlign.CENTER }
                    cell(labelStr) { align = TextAlign.CENTER }
                    cell(white(issue.title)) { align = TextAlign.LEFT }
                }
            }
        }
    }
}

fun buildNavBar(repos: List<RepoData>, selectedIndex: Int, vararg extraMessages: String): String {
    val sb = StringBuilder()
    sb.append("\n")

    for ((i, repo) in repos.withIndex()) {
        val shortName = repo.fullName.substringAfter("/")
        if (i == selectedIndex) {
            sb.append(" ${(bold + brightWhite + com.github.ajalt.mordant.rendering.TextStyles.inverse)(" $shortName ")} ")
        } else {
            sb.append(" ${gray(shortName)} ")
        }
        if (i < repos.lastIndex) sb.append(gray("│"))
    }

    sb.append("\n")
    sb.append(gray("  ← → Navigate repos  │  Ctrl+C Exit"))
    
    for (msg in extraMessages) {
        sb.append(gray("  │  $msg"))
    }
    
    sb.append("\n")
    return sb.toString()
}
