/*
 * Smoke test for the kt-issue CLI application.
 */
package org.example

import kotlin.test.Test
import kotlin.test.assertNotNull

class AppTest {
    @Test fun modelsCanBeInstantiated() {
        val issue = GitHubIssue(
            number    = 1,
            title     = "Test issue",
            state     = "open",
            body      = null,
            assignee  = null,
            labels    = emptyList(),
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )
        assertNotNull(issue, "GitHubIssue data class should instantiate correctly")
    }
}
