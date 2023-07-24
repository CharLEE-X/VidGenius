package com.charleex.autoytvid.feature.router

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.navigation.routing.RoutingTable
import com.copperleaf.ballast.navigation.routing.fromEnum
import com.copperleaf.ballast.navigation.vm.BasicRouter
import com.copperleaf.ballast.navigation.vm.withRouter
import com.copperleaf.ballast.plusAssign
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent

class RouterViewModel(
    viewModelScope: CoroutineScope,
    initialRoute: RouterScreen,
) : KoinComponent, BasicRouter<RouterScreen>(
    config = BallastViewModelConfiguration.Builder()
//        .apply {
//            this += LoggingInterceptor()
//            logger = { PrintlnLogger() }
//        }
        .withRouter(
            routingTable = RoutingTable.fromEnum(RouterScreen.values()),
            initialRoute = initialRoute
        )
        .build(),
    eventHandler = RouterEventHandler(),
    coroutineScope = viewModelScope
)
