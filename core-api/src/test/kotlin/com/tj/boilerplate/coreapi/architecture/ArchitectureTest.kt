package com.tj.boilerplate.coreapi.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.jupiter.api.Test

class ArchitectureTest {
    private val importedClasses: JavaClasses =
        ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(ROOT_PACKAGE)

    @Test
    fun `레이어 의존은 정방향(상위 → 하위)만 허용한다`() {
        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer(CORE_API)
            .definedBy("$ROOT_PACKAGE.coreapi..")
            .layer(DOMAIN)
            .definedBy("$ROOT_PACKAGE.domain..")
            .layer(STORAGE)
            .definedBy("$ROOT_PACKAGE.storage..")
            .layer(CLIENTS)
            .definedBy("$ROOT_PACKAGE.clients..")
            .layer(MODULES)
            .definedBy("$ROOT_PACKAGE.modules..", "$ROOT_PACKAGE.lock..", "$ROOT_PACKAGE.pagination..")
            .layer(COMMON)
            .definedBy("$ROOT_PACKAGE.common..", "$ROOT_PACKAGE.profile..")
            // 진입 모듈은 최상위 — 아무도 의존하지 못한다
            .whereLayer(CORE_API)
            .mayNotBeAccessedByAnyLayer()
            // domain 은 진입 모듈에서만 접근(storage·clients 는 domain 을 역참조 ❌)
            .whereLayer(DOMAIN)
            .mayOnlyBeAccessedByLayers(CORE_API)
            // storage 는 domain 을 통해서만(진입 모듈의 직접 참조 ❌)
            .whereLayer(STORAGE)
            .mayOnlyBeAccessedByLayers(DOMAIN)
            // 외부 API 는 진입 측 조율층에서만(domain 의 외부 I/O ❌)
            .whereLayer(CLIENTS)
            .mayOnlyBeAccessedByLayers(CORE_API)
            // 횡단 기술 기능은 진입 모듈·domain 이 사용(storage·clients 는 ❌)
            .whereLayer(MODULES)
            .mayOnlyBeAccessedByLayers(CORE_API, DOMAIN)
            // common 은 leaf — 모든 레이어가 참조 가능하나 common 자신은 아무도 의존하지 않는다
            .whereLayer(COMMON)
            .mayOnlyBeAccessedByLayers(CORE_API, DOMAIN, STORAGE, CLIENTS, MODULES)
            .withOptionalLayers(true)
            .check(importedClasses)
    }

    @Test
    fun `JPA 엔티티는 storage 레이어에만 존재한다`() {
        classes()
            .that()
            .areAnnotatedWith("jakarta.persistence.Entity")
            .or()
            .areAnnotatedWith("jakarta.persistence.MappedSuperclass")
            .should()
            .resideInAPackage("..storage..")
            .allowEmptyShould(true)
            .check(importedClasses)
    }

    @Test
    fun `Spring Data Repository 는 storage 레이어에만 존재한다`() {
        classes()
            .that()
            .areAssignableTo("org.springframework.data.repository.Repository")
            .should()
            .resideInAPackage("..storage..")
            .allowEmptyShould(true)
            .check(importedClasses)
    }

    @Test
    fun `레이어 간 순환 의존이 없다`() {
        slices()
            .matching("$ROOT_PACKAGE.(*)..")
            .should()
            .beFreeOfCycles()
            .check(importedClasses)
    }

    companion object {
        private const val ROOT_PACKAGE = "com.tj.boilerplate"
        private const val CORE_API = "CoreApi"
        private const val DOMAIN = "Domain"
        private const val STORAGE = "Storage"
        private const val CLIENTS = "Clients"
        private const val MODULES = "Modules"
        private const val COMMON = "Common"
    }
}
