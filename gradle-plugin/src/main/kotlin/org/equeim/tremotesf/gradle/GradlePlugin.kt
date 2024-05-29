// SPDX-FileCopyrightText: 2017-2024 Alexey Rochev <equeim@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package org.equeim.tremotesf.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.equeim.tremotesf.gradle.utils.compileSdk
import org.equeim.tremotesf.gradle.utils.libs
import org.equeim.tremotesf.gradle.utils.minSdk
import org.equeim.tremotesf.gradle.utils.targetSdk
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class GradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureAndroidProject()
        target.configureGradleVersionsPlugin()
    }

    private fun Project.configureAndroidProject() {
        plugins.withType<AndroidBasePlugin> {
            val androidExtension = extensions.getByType(CommonExtension::class)
            androidExtension.configureAndroidProject(libs)
            @Suppress("DEPRECATION")
            (androidExtension as ExtensionAware).extensions.getByType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions::class)
                .configureKotlin()
        }
    }

    private fun CommonExtension<*, *, *, *, *>.configureAndroidProject(libs: VersionCatalog) {
        compileSdk = libs.compileSdk
        defaultConfig.minSdk = libs.minSdk
        lint.apply {
            informational.add("MissingTranslation")
            quiet = false
            checkAllWarnings = true
            disable.addAll(listOf("InvalidPackage", "SyntheticAccessor", "TypographyQuotes"))
        }
        when (this) {
            is LibraryExtension -> configureLibraryProject()
            is ApplicationExtension -> configureApplicationProject(libs)
        }
    }

    private fun LibraryExtension.configureLibraryProject() {
        defaultConfig.consumerProguardFile("consumer-rules.pro")
    }

    private fun ApplicationExtension.configureApplicationProject(libs: VersionCatalog) {
        defaultConfig.targetSdk = libs.targetSdk
    }

    @Suppress("DEPRECATION")
    private fun org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions.configureKotlin() {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}
