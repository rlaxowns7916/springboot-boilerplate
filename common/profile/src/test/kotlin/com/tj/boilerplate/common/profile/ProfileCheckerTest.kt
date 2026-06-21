package com.tj.boilerplate.common.profile

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.core.env.Environment

class ProfileCheckerTest {
    @Test
    internal fun `여러_phase가_겹쳐있다면_가장_우선순위가_높은것으로_결정한다`() {
        val env =
            mockk<Environment>().apply {
                every { activeProfiles } answers { listOf("local", "dev", "live").toTypedArray() }
            }
        val sut = ProfileChecker(env)
        val actual = sut.getCurrentProfile()

        assertThat(actual).isEqualTo(ServiceProfile.LIVE)
    }

    @ParameterizedTest
    @EnumSource(value = ServiceProfile::class, names = ["LOCAL", "DEV"])
    internal fun `local과_dev_phase는_DEV환경으로_취급한다`(profile: ServiceProfile) {
        val env =
            mockk<Environment>().apply {
                every { activeProfiles } answers { listOf(profile.ymlValue).toTypedArray() }
            }

        val sut = ProfileChecker(env)
        assertThat(sut.isDevProfile()).isTrue()
    }

    @ParameterizedTest
    @EnumSource(value = ServiceProfile::class, names = ["LIVE"])
    internal fun `live는_phase는LIVE환경으로_취급한다`(profile: ServiceProfile) {
        val env =
            mockk<Environment>().apply {
                every { activeProfiles } answers { listOf(profile.ymlValue).toTypedArray() }
            }

        val sut = ProfileChecker(env)
        assertThat(sut.isLiveProfile()).isTrue()
    }
}
