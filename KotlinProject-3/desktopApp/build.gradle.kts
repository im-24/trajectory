import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
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