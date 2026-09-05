package com.afoxplus.emergency.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.afoxplus.emergency.ui.theme.AppShapes
import com.afoxplus.emergency.ui.theme.AppSpacing
import com.afoxplus.emergency.ui.theme.AppemergencyTheme
import com.afoxplus.emergency.ui.theme.EmergencyButton
import com.afoxplus.emergency.ui.theme.EmergencyCard
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Stateful entry point: wires the [OnboardingViewModel] to the stateless [OnboardingScreen].
 */
@Composable
fun OnboardingRoute(
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(
            OnboardingPreferencesImpl(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onOnboardingFinished()
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onPageChanged = viewModel::onPageChanged,
        onNextClicked = viewModel::onNextClicked,
        onSkipClicked = viewModel::onSkipClicked,
        modifier = modifier
    )
}

/**
 * Stateless onboarding UI: displays the onboarding pages in a swipeable pager together
 * with a progress indicator and the skip/next/finish actions.
 */
@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onPageChanged: (Int) -> Unit,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { uiState.pages.size }
    )

    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> onPageChanged(page) }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("onboarding_pager")
            ) { page ->
                OnboardingPageContent(page = uiState.pages[page])
            }

            OnboardingPageIndicator(
                pageCount = uiState.pages.size,
                currentPage = uiState.currentPage
            )

            Spacer(modifier = Modifier.height(AppSpacing.xxl))

            OnboardingActions(
                isLastPage = uiState.isLastPage,
                onNextClicked = onNextClicked,
                onSkipClicked = onSkipClicked
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(AppShapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(text = page.icon, fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        Text(
            text = page.brand,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        Text(
            text = page.tagline,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppSpacing.xxl))

        EmergencyCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = page.featureTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = page.featureDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("onboarding_indicator"),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (isSelected) 24.dp else 8.dp)
                    .clip(AppShapes.extraSmall)
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            )
        }
    }
}

@Composable
private fun OnboardingActions(
    isLastPage: Boolean,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        EmergencyButton(
            text = if (isLastPage) "Comenzar →" else "Siguiente →",
            onClick = onNextClicked,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_primary_action")
        )

        if (!isLastPage) {
            Spacer(modifier = Modifier.height(AppSpacing.sm))
            TextButton(
                onClick = onSkipClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_skip_action")
            ) {
                Text(text = "Saltar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    AppemergencyTheme {
        OnboardingScreen(
            uiState = OnboardingUiState(),
            onPageChanged = {},
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}
