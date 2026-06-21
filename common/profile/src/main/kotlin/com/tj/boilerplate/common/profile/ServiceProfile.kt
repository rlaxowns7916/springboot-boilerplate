package com.tj.boilerplate.common.profile

internal enum class ServiceProfile(
    val priority: Int,
    val ymlValue: String,
) {
    LOCAL(0, "local"),
    DEV(1, "dev"),
    LIVE(2, "live"),
    ;

    companion object {
        fun fromYmlValue(from: String): ServiceProfile = entries.first { it.ymlValue == from }
    }
}
