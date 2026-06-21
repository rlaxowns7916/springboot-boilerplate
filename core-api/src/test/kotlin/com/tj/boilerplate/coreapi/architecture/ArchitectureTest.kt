package com.tj.boilerplate.coreapi.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

class ArchitectureTest {
    private val classes: JavaClasses =
        ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.tj.boilerplate")

    @Test
    fun `레이어 의존 방향 core-api → domain → storage`() {
        layeredArchitecture().consideringOnlyDependenciesInLayers()
            // 도메인/스토리지 기능 코드가 아직 없어 레이어가 비어있을 수 있다(가드레일 선설치).
            .withOptionalLayers(true)
            .layer("CoreApi").definedBy("..coreapi..")
            .layer("Domain").definedBy("..domain..")
            .layer("Storage").definedBy("..storage..")
            .whereLayer("CoreApi").mayNotBeAccessedByAnyLayer()
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("CoreApi")
            .whereLayer("Storage").mayOnlyBeAccessedByLayers("Domain")
            .check(classes)
    }

    @Test
    fun `@Entity는 storage에만`() {
        classes().that().areAnnotatedWith("jakarta.persistence.Entity")
            .should().resideInAPackage("..storage..")
            .check(classes)
    }

    @Test
    fun `JPA 연관관계 애노테이션 금지`() {
        listOf("OneToMany", "ManyToOne", "OneToOne", "ManyToMany").forEach { rel ->
            noClasses().should().dependOnClassesThat()
                .areAssignableTo("jakarta.persistence.$rel")
                .because("연관관계 대신 ref_xxx_id + 명시 조회")
                .check(classes)
        }
    }

    @Test
    fun `클래스 레벨 @Transactional 금지 (메서드 레벨만)`() {
        noClasses().should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
            .check(classes)
    }

    @Test
    fun `@RestController는 core-api에만`() {
        classes().that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .should().resideInAPackage("..coreapi..")
            .check(classes)
    }

    @Test
    fun `Repository는 storage에만`() {
        classes().that().haveSimpleNameEndingWith("Repository")
            .should().resideInAPackage("..storage..")
            .check(classes)
    }

    @Test
    fun `조율은 ApplicationService(core-api), 도메인 작업은 Reader-Writer-Registrar(domain)`() {
        classes().that().haveSimpleNameEndingWith("ApplicationService")
            .should().resideInAPackage("..coreapi..")
            .check(classes)
        classes().that().haveSimpleNameEndingWith("Reader")
            .or().haveSimpleNameEndingWith("Writer")
            .or().haveSimpleNameEndingWith("Registrar")
            .should().resideInAPackage("..domain..")
            .check(classes)
    }

    @Test
    fun `필드 주입 금지 (생성자 주입만)`() {
        noClasses().should().dependOnClassesThat()
            .areAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
            .check(classes)
    }
}
