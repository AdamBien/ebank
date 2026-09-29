package airhacks.ebank;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import jakarta.transaction.Transactional;

/// Enforces the architecture documented in the `airhacks.ebank` `package-info`:
/// the BCE layering (boundary -> control -> entity) and the component
/// dependency graph, including the deliberate `accounting <-> customers`
/// exception (decision D1).
///
/// Production classes only (`DoNotIncludeTests`), so this test and the `*IT`
/// classes are out of scope.
@AnalyzeClasses(packages = "airhacks.ebank", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    /// Protocol-specific technologies that belong exclusively in the boundary
    /// layer: JAX-RS/REST, the Servlet API, and the MCP server.
    static final String[] PROTOCOL_PACKAGES = {
            "jakarta.ws.rs..",
            "jakarta.servlet..",
            "io.quarkiverse.mcp.."
    };

    // --- Protocol isolation (boundary is the only place for REST/HTTP/MCP) ---

    @ArchTest
    static final ArchRule P1_control_is_free_of_protocol = noClasses()
            .that().resideInAPackage("..control..")
            .should().dependOnClassesThat().resideInAnyPackage(PROTOCOL_PACKAGES)
            .because("control holds protocol-agnostic business logic; REST/HTTP/MCP types belong in the boundary");

    @ArchTest
    static final ArchRule P2_entities_are_free_of_protocol = noClasses()
            .that().resideInAPackage("..entity..")
            .should().dependOnClassesThat().resideInAnyPackage(PROTOCOL_PACKAGES)
            .because("entities are the domain core; no REST/HTTP/MCP type may leak into them");

    // --- BCE layering (application-wide) ---

    @ArchTest
    static final ArchRule L1_entities_do_not_depend_on_control = noClasses()
            .that().resideInAPackage("..entity..")
            .should().dependOnClassesThat().resideInAPackage("..control..")
            .because("entities are the innermost BCE layer and must not reach outward into control");

    @ArchTest
    static final ArchRule L2_control_does_not_depend_on_boundary = noClasses()
            .that().resideInAPackage("..control..")
            .should().dependOnClassesThat().resideInAPackage("..boundary..")
            .because("control implements workflows behind a boundary and must not depend back on it");

    // --- Component dependency rules (see package-info '## Components') ---

    @ArchTest
    static final ArchRule C1_nobody_depends_on_health = noClasses()
            .that().resideOutsideOfPackage("airhacks.ebank.health..")
            .should().dependOnClassesThat().resideInAPackage("airhacks.ebank.health..")
            .because("health is a technical sink: any BC may use logging, none may depend on health");

    @ArchTest
    static final ArchRule C2_logging_is_standalone = noClasses()
            .that().resideInAPackage("airhacks.ebank.logging..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "airhacks.ebank.accounting..",
                    "airhacks.ebank.transactions..",
                    "airhacks.ebank.reporting..",
                    "airhacks.ebank.customers..",
                    "airhacks.ebank.health..",
                    "airhacks.ebank.http..")
            .because("logging is a technical component that no business component may pull along");

    @ArchTest
    static final ArchRule C3_http_is_standalone = noClasses()
            .that().resideInAPackage("airhacks.ebank.http..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "airhacks.ebank.accounting..",
                    "airhacks.ebank.transactions..",
                    "airhacks.ebank.reporting..",
                    "airhacks.ebank.customers..",
                    "airhacks.ebank.health..",
                    "airhacks.ebank.logging..")
            .because("http holds business-neutral Response plumbing and must stay free of BC dependencies");

    @ArchTest
    static final ArchRule C4_accounting_dependencies = noClasses()
            .that().resideInAPackage("airhacks.ebank.accounting..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("airhacks.ebank.transactions..", "airhacks.ebank.reporting..")
            .because("accounting may only reach customers (D1); never transactions or reporting");

    @ArchTest
    static final ArchRule C5_transactions_dependencies = noClasses()
            .that().resideInAPackage("airhacks.ebank.transactions..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("airhacks.ebank.reporting..", "airhacks.ebank.customers..")
            .because("transactions may only call accounting; never reporting or customers");

    @ArchTest
    static final ArchRule C6_reporting_dependencies = noClasses()
            .that().resideInAPackage("airhacks.ebank.reporting..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("airhacks.ebank.transactions..", "airhacks.ebank.customers..")
            .because("reporting reads accounting only; never transactions or customers");

    @ArchTest
    static final ArchRule C7_customers_dependencies = noClasses()
            .that().resideInAPackage("airhacks.ebank.customers..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("airhacks.ebank.transactions..", "airhacks.ebank.reporting..")
            .because("customers may only call accounting (D1); never transactions or reporting");

    // --- Stereotype invariant ---

    @ArchTest
    static final ArchRule S1_transactional_only_on_boundary = classes()
            .that().areAnnotatedWith(Transactional.class)
            .and().areNotAnnotations()
            .should().beAnnotatedWith(Boundary.class)
            .because("a transaction starts only at a @Boundary (see the Boundary stereotype)")
            .allowEmptyShould(true);
}
