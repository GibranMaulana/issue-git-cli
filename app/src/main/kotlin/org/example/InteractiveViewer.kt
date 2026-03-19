package org.example

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.input.enterRawMode
import com.github.ajalt.mordant.input.KeyboardEvent
import com.github.ajalt.mordant.input.isCtrlC

object InteractiveViewer {
    fun renderScreen(
        terminal: Terminal,
        repos: List<RepoData>,
        selectedIndex: Int,
        currentUser: String?,
        header: String
    ) {
        print(CLEAR_SCREEN)
        terminal.println(header)

        if (currentUser != null) {
            terminal.println(gray("  Logged in as: ") + (bold + brightWhite)("$currentUser\n"))
        }

        val repo = repos[selectedIndex]
        if (repo.issues.isEmpty()) {
            terminal.println(green("  No open issues in this repo! \n"))
        } else {
            terminal.println(buildIssueTable(terminal, repo, currentUser))
        }

        terminal.print(buildNavBar(repos, selectedIndex, "Interactive Mode Active"))
    }

    fun start(
        terminal: Terminal,
        repos: List<RepoData>,
        currentUser: String?,
        header: String
    ): Boolean {
        return try {
            terminal.println(
                "\n" + (bold + cyan)("  Starting interactive mode...") +
                gray(" Use ← → to navigate, Ctrl+C to exit.\n")
            )
            Thread.sleep(1000)

            var selectedIndex = 0
            print(HIDE_CURSOR)

            terminal.enterRawMode().use { rawMode ->
                renderScreen(terminal, repos, selectedIndex, currentUser, header)

                while (true) {
                    val event = rawMode.readKey()
                    if (event == null || event.isCtrlC) break

                    when (event.key) {
                        "ArrowRight" -> {
                            if (selectedIndex < repos.lastIndex) {
                                selectedIndex++
                                renderScreen(terminal, repos, selectedIndex, currentUser, header)
                            }
                        }
                        "ArrowLeft" -> {
                            if (selectedIndex > 0) {
                                selectedIndex--
                                renderScreen(terminal, repos, selectedIndex, currentUser, header)
                            }
                        }
                    }
                }
            }

            print(SHOW_CURSOR)
            print(CLEAR_SCREEN)
            terminal.println(header)
            terminal.println(green("  Goodbye! \n"))
            true
        } catch (_: IllegalStateException) {
            print(SHOW_CURSOR)
            false
        }
    }
}
