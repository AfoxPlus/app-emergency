package com.afoxplus.emergency.presentation.onboarding

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import org.junit.Rule
import org.junit.Test

class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        uiState: OnboardingUiState = OnboardingUiState(),
        onPageChanged: (Int) -> Unit = {},
        onNextClicked: () -> Unit = {},
        onSkipClicked: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AppemergencyTheme {
                OnboardingScreen(
                    uiState = uiState,
                    onPageChanged = onPageChanged,
                    onNextClicked = onNextClicked,
                    onSkipClicked = onSkipClicked
                )
            }
        }
    }

    @Test
    fun firstPageContentIsDisplayed() {
        setContent()

        composeTestRule.onNodeWithText(onboardingPages.first().featureTitle).assertExists()
        composeTestRule.onNodeWithText(onboardingPages.first().tagline).assertExists()
    }

    @Test
    fun skipActionIsVisibleOnNonLastPagesAndTriggersCallback() {
        var skipped = false
        setContent(onSkipClicked = { skipped = true })

        composeTestRule.onNodeWithTag("onboarding_skip_action").performClick()

        assert(skipped)
    }

    @Test
    fun nextActionTriggersCallbackOnNonLastPage() {
        var nextClicked = false
        setContent(onNextClicked = { nextClicked = true })

        composeTestRule.onNodeWithTag("onboarding_primary_action").performClick()

        assert(nextClicked)
    }

    @Test
    fun lastPageShowsFinishActionAndHidesSkip() {
        val lastPageState = OnboardingUiState(currentPage = onboardingPages.lastIndex)
        var finished = false
        setContent(uiState = lastPageState, onNextClicked = { finished = true })

        composeTestRule.onNodeWithTag("onboarding_skip_action").assertDoesNotExist()
        composeTestRule.onNodeWithTag("onboarding_primary_action").performClick()

        assert(finished)
    }
}
