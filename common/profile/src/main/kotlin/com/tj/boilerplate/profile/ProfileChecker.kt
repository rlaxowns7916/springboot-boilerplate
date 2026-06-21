package com.tj.boilerplate.profile

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class ProfileChecker(
    private val env: Environment,
) {
    fun isDevProfile(): Boolean = getCurrentProfile() in listOf(ServiceProfile.LOCAL, ServiceProfile.DEV)

    fun isLiveProfile(): Boolean = getCurrentProfile() == ServiceProfile.LIVE

    internal fun getCurrentProfile(): ServiceProfile {
        val activeProfiles = env.activeProfiles

        return activeProfiles
            .filter { activeProfile -> activeProfile in ServiceProfile.entries.map { it.ymlValue } }
            .map { ServiceProfile.fromYmlValue(it) }
            .maxBy { it.priority }
    }
}
