package com.keak.aishou.screens.paywall

import aishou.composeapp.generated.resources.Res
import aishou.composeapp.generated.resources.crown_premium
import aishou.composeapp.generated.resources.hearth_desc
import aishou.composeapp.generated.resources.monthly_premium_paywall
import aishou.composeapp.generated.resources.monthly_premium_per_month
import aishou.composeapp.generated.resources.monthly_premium_per_week
import aishou.composeapp.generated.resources.paywall_cancel_anytime
import aishou.composeapp.generated.resources.paywall_cancel_paywall
import aishou.composeapp.generated.resources.paywall_choose_your_sign
import aishou.composeapp.generated.resources.paywall_go_premium
import aishou.composeapp.generated.resources.paywall_join_discover
import aishou.composeapp.generated.resources.paywall_restore_purchase_text
import aishou.composeapp.generated.resources.paywall_unlock_your_cosmic_personality
import aishou.composeapp.generated.resources.paywall_weekly_features_daily_horoscope
import aishou.composeapp.generated.resources.paywall_weekly_features_daily_love_compatibility
import aishou.composeapp.generated.resources.paywall_weekly_features_daily_personality_insights
import aishou.composeapp.generated.resources.personality_desc
import aishou.composeapp.generated.resources.smile_star
import aishou.composeapp.generated.resources.star
import aishou.composeapp.generated.resources.star_dec
import aishou.composeapp.generated.resources.weekly_premium_paywall
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keak.aishou.components.NeoBrutalistCardViewWithFlexSize
import com.keak.aishou.misc.CommonLinks
import com.keak.aishou.navigation.Router
import com.keak.aishou.purchase.PremiumPresenter
import com.keak.aishou.purchase.ProductsRepository
import com.keak.aishou.purchase.ProductsState
import com.keak.aishou.purchase.model.Periods.MONTHLY_PERIOD
import com.keak.aishou.purchase.model.Periods.WEEKLY_PERIOD
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetSize
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject


@Composable
fun PaywallScreen(router: Router) {
    val repo: ProductsRepository = koinInject()
    val presenter: PremiumPresenter = koinInject()
    var bottomSheetChosenScreen by remember { mutableStateOf(PremiumBottomSheetScreen.None) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val state by repo.state.collectAsState()

    //print("AKN-PAYWALL-->>${s.packages}")
    var start by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        repo.getProducts()
        start = true
    }

    Box(modifier = Modifier.fillMaxSize().background(
        Color(0xFF49DC9C)
    )) {
        if (showBottomSheet) {
            val sheetUrl = when (bottomSheetChosenScreen) {
                PremiumBottomSheetScreen.Terms -> CommonLinks.TermsAndConditions
                PremiumBottomSheetScreen.Policy -> CommonLinks.PrivacyPolicy
                PremiumBottomSheetScreen.None -> null
            }

            if (sheetUrl != null) {
                FlexibleBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = rememberFlexibleBottomSheetState(
                        flexibleSheetSize = FlexibleSheetSize(
                            fullyExpanded = 0.9f
                        ),
                        skipIntermediatelyExpanded = true,
                        isModal = true,
                        skipSlightlyExpanded = true,
                    ),
                    containerColor = Color.Black,
                    dragHandle = { },
                    scrimColor = Color.Transparent
                ) {
                    PaywallLegalContent(
                        url = sheetUrl,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize().background(
                Color(0xFF49DC9C)
            ).padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            when (val s = state) {
                ProductsState.Loading -> {
                    print("AKN-PAYWALL-->>Loading")

                }

                ProductsState.Empty -> {
                    print("AKN-PAYWALL-->>Empty")
                }

                is ProductsState.Error -> {
                    print("AKN-PAYWALL-->>Error ${s.message}")
                }

                is ProductsState.Loaded -> {
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.fillMaxWidth().rotate(2f),
                        backgroundColor = Color(0xFFFFF176),

                        ) {
                        Text(
                            text = stringResource(Res.string.paywall_unlock_your_cosmic_personality),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier

                        )
                    }
                    Spacer(Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        NeoBrutalistCardViewWithFlexSize(
                            modifier = Modifier.weight(1f).clickable(role = Role.Button) {
                                presenter.buyProduct(
                                    product = s.packages[0].productPackage,
                                    onSuccessEvent = {
                                        presenter.onRestoreOrPurchase()
                                    },
                                    onErrorEvent = { error, userCancelled ->

                                    }
                                )
                            },
                            backgroundColor = Color(0xFF4DD0E1),

                            ) {
                            Column {
                                Image(
                                    painter = painterResource(Res.drawable.star),
                                    modifier = Modifier.size(35.dp)
                                        .align(Alignment.CenterHorizontally),
                                    contentDescription = null
                                )

                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(Res.string.weekly_premium_paywall),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)

                                )
                                Spacer(Modifier.height(8.dp))
                                NeoBrutalistCardViewWithFlexSize(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    backgroundColor = Color(0xFFFFF176),
                                ) {
                                    Column {
                                        Text(
                                            text = s.packages.find { it.periodLabel == WEEKLY_PERIOD }?.priceFormatted.orEmpty(),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)

                                        )
                                        Text(
                                            text = stringResource(Res.string.monthly_premium_per_week),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)

                                        )
                                    }

                                }
                                Spacer(Modifier.height(8.dp))
                                PremiumFeaturesRow(
                                    image = Res.drawable.star_dec,
                                    text = stringResource(Res.string.paywall_weekly_features_daily_horoscope)
                                )
                                Spacer(Modifier.height(8.dp))
                                PremiumFeaturesRow(
                                    image = Res.drawable.hearth_desc,
                                    text = stringResource(Res.string.paywall_weekly_features_daily_love_compatibility)
                                )
                                Spacer(Modifier.height(8.dp))
                                PremiumFeaturesRow(
                                    image = Res.drawable.personality_desc,
                                    text = stringResource(Res.string.paywall_weekly_features_daily_personality_insights)
                                )
                                Spacer(Modifier.height(8.dp))
                                NeoBrutalistCardViewWithFlexSize(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                                        .clickable(role = Role.Button) {
                                            presenter.buyProduct(
                                                product = s.packages[0].productPackage,
                                                onSuccessEvent = {
                                                    presenter.onRestoreOrPurchase()
                                                },
                                                onErrorEvent = { error, userCancelled ->

                                                }
                                            )
                                        },
                                    backgroundColor = Color(0xFFEC407A),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.paywall_go_premium),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }

                        }
                        Spacer(Modifier.width(12.dp))
                        NeoBrutalistCardViewWithFlexSize(
                            modifier = Modifier.weight(1f),
                            backgroundColor = Color(0xFFFFF176),
                            showBadge = true
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(Res.drawable.crown_premium),
                                    modifier = Modifier.size(35.dp)
                                        .align(Alignment.CenterHorizontally),
                                    contentDescription = null
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(Res.string.monthly_premium_paywall),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Spacer(Modifier.height(8.dp))
                                NeoBrutalistCardViewWithFlexSize(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    backgroundColor = Color(0xFF4DD0E1),
                                ) {
                                    Column {
                                        Text(
                                            text = s.packages.find { it.periodLabel == MONTHLY_PERIOD }?.priceFormatted.orEmpty(),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)

                                        )
                                        Text(
                                            text = stringResource(Res.string.monthly_premium_per_month),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)

                                        )
                                    }

                                }
                                Spacer(Modifier.height(8.dp))
                                PremiumFeaturesRow(
                                    image = Res.drawable.star_dec,
                                    text = stringResource(Res.string.paywall_weekly_features_daily_horoscope)
                                )
                                Spacer(Modifier.height(8.dp))
                                PremiumFeaturesRow(
                                    image = Res.drawable.hearth_desc,
                                    text = stringResource(Res.string.paywall_weekly_features_daily_love_compatibility)
                                )
                                Spacer(Modifier.height(8.dp))
                                PremiumFeaturesRow(
                                    image = Res.drawable.personality_desc,
                                    text = stringResource(Res.string.paywall_weekly_features_daily_personality_insights)
                                )
                                Spacer(Modifier.height(8.dp))
                                NeoBrutalistCardViewWithFlexSize(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                                        .clickable(role = Role.Button) {
                                            println("AKN--PAYWALL---> ${s.packages[1].productPackage.identifier}")
                                            presenter.buyProduct(
                                                product = s.packages[1].productPackage,
                                                onSuccessEvent = {
                                                    presenter.onRestoreOrPurchase()
                                                },
                                                onErrorEvent = { error, userCancelled ->

                                                }
                                            )
                                        },
                                    backgroundColor = Color(0xFF66BB6A),
                                ) {
                                    Text(
                                        text = stringResource(Res.string.paywall_go_premium),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }

                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = stringResource(Res.string.paywall_restore_purchase_text),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                presenter.restorePuchases()
                            }
                        )
                        Text(
                            text = "Terms",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                bottomSheetChosenScreen = PremiumBottomSheetScreen.Terms
                                showBottomSheet = true
                                //uriHandler.openUri("https://docs.google.com/document/d/1XHmJNZ8xCSsjn_XV_8aXQKGWZAw8Ao7corvBQMTokVE/edit?usp=sharing")
                            }
                        )
                        Text(
                            text = "Policy",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                bottomSheetChosenScreen = PremiumBottomSheetScreen.Policy
                                showBottomSheet = true
                                //uriHandler.openUri("https://www.termsfeed.com/live/f42b832a-1b27-4502-a973-960330cf63da")
                            }
                        )
                    }
                    //End of The Cards Block
//                HeartFillMeter(
//                    modifier = Modifier.size(220.dp).align(Alignment.CenterHorizontally),
//                    targetPercent = if (start) 100 else 0,
//                    fillDurationMillis = 3500
//                )

                    Spacer(Modifier.weight(1f))
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.fillMaxWidth().rotate(2f),
                        backgroundColor = Color(0xFFAB47BC),

                        ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.smile_star),
                                modifier = Modifier.size(18.dp),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.paywall_join_discover),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier,
                                color = Color.White

                            )
                            Spacer(Modifier.width(8.dp))
                            Image(
                                painter = painterResource(Res.drawable.smile_star),
                                modifier = Modifier.size(18.dp),
                                contentDescription = null
                            )
                        }

                    }
                    Spacer(Modifier.height(16.dp))
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.fillMaxWidth().rotate(-2f).clickable() {
                            router.goToHome()
                        },
                        backgroundColor = Color(0xFFFFFFFF),

                        ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.paywall_cancel_anytime),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier,
                                color = Color.Black

                            )
                        }

                    }
                    Spacer(Modifier.height(16.dp))
                    NeoBrutalistCardViewWithFlexSize(
                        modifier = Modifier.fillMaxWidth().clickable() {
                            router.goToHome()
                        },
                        backgroundColor = Color(0xFF66BB6A),

                        ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.paywall_cancel_paywall),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier,
                                color = Color.White

                            )
                        }

                    }
                    Spacer(Modifier.height(16.dp))
                    //End of the bottom banners

                }
            }
        }
    }

}

@Composable
private fun PremiumFeaturesRow(
    image: DrawableResource,
    imageSize: Int = 12,
    textSize: Int = 12,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(image),
            modifier = Modifier.size(imageSize.dp),
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = textSize.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
        )
    }
}

enum class PremiumBottomSheetScreen {
    Terms, Policy, None
}
