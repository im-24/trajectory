import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "1.9.0"

}

dependencies {
    implementation("io.coil-kt.coil3:coil-compose:3.0.0")
        implementation("com.itextpdf:itext7-core:7.2.5")

        // For Excel export (additional)
        implementation("org.apache.poi:poi-ooxml:5.2.3")

        // For HTML to PDF conversion
        implementation("org.xhtmlrenderer:flying-saucer-pdf-openpdf:9.1.22")

    implementation(projects.shared)
        implementation(compose.desktop.currentOs)
        implementation("org.jetbrains.compose.material3:material3-desktop:1.6.x")
        implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.6.x")
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.desktop.currentOs)
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation("org.apache.poi:poi-ooxml:5.2.5")

        // ... existing dependencies

        // For Word document generation
        implementation("org.apache.poi:poi:5.2.3")
        implementation("org.apache.poi:poi-ooxml:5.2.3")

        // For PDF generation
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.itextpdf:kernel:7.2.5")
    implementation("com.itextpdf:layout:7.2.5")
    implementation("com.itextpdf:io:7.2.5")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation ("org.apache.poi:poi:5.2.3")

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}