package com.afoxplus.emergency.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.afoxplus.emergency.R

/**
 * Builds the localized onboarding pages from string resources, so the copy shown to the
 * user (unlike the default [onboardingPages] used for previews/tests) can be translated.
 */
@Composable
fun rememberOnboardingPages(): List<OnboardingPage> {
    val brand = stringResource(R.string.onboarding_brand)
    val page1Tagline = stringResource(R.string.onboarding_page1_tagline)
    val page1Title = stringResource(R.string.onboarding_page1_feature_title)
    val page1Description = stringResource(R.string.onboarding_page1_feature_description)
    val page2Tagline = stringResource(R.string.onboarding_page2_tagline)
    val page2Title = stringResource(R.string.onboarding_page2_feature_title)
    val page2Description = stringResource(R.string.onboarding_page2_feature_description)
    val page3Tagline = stringResource(R.string.onboarding_page3_tagline)
    val page3Title = stringResource(R.string.onboarding_page3_feature_title)
    val page3Description = stringResource(R.string.onboarding_page3_feature_description)

    return remember(
        brand,
        page1Tagline, page1Title, page1Description,
        page2Tagline, page2Title, page2Description,
        page3Tagline, page3Title, page3Description
    ) {
        listOf(
            OnboardingPage(
                icon = "\uD83D\uDEE1\uFE0F",
                brand = brand,
                tagline = page1Tagline,
                featureTitle = page1Title,
                featureDescription = page1Description
            ),
            OnboardingPage(
                icon = "\uD83D\uDCDE",
                brand = brand,
                tagline = page2Tagline,
                featureTitle = page2Title,
                featureDescription = page2Description
            ),
            OnboardingPage(
                icon = "\uD83D\uDD14",
                brand = brand,
                tagline = page3Tagline,
                featureTitle = page3Title,
                featureDescription = page3Description
            )
        )
    }
}
