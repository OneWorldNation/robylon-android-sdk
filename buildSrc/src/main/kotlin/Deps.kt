import org.codehaus.groovy.runtime.ProcessGroovyMethods
import java.util.Locale

object Deps {

    //Sdk Details
    const val SDK_PACKAGE_NAME = "com.own"
    private const val MAJOR_VERSION = 1
    private const val MINOR_VERSION = 0
    private const val PATCH_VERSION = 0
    const val SNAPSHOT = -1
    val BUILD_TYPE = BuildType.NATIVE
    private const val ISPROD = false
    const val RUN_LIB = true

    const val SDK_VERSION_CODE = MAJOR_VERSION * 1000 + (MINOR_VERSION * 100) + PATCH_VERSION
    var SDK_VERSION_NAME = if (SNAPSHOT != -1) {
        if (RUN_LIB)
            "$MAJOR_VERSION.${MINOR_VERSION}.${PATCH_VERSION}.${SNAPSHOT}" + "-LOCAL"
        else
            "$MAJOR_VERSION.${MINOR_VERSION}.${PATCH_VERSION}.${SNAPSHOT}" + "-SNAPSHOT"
    } else
        "$MAJOR_VERSION.${MINOR_VERSION}.${PATCH_VERSION}"

    //App Details
    var APP_VERSION_CODE = (10000 * MAJOR_VERSION) + (1000 * MINOR_VERSION) + (100 * PATCH_VERSION) + SNAPSHOT

    var APP_VERSION_NAME = if (ISPROD) "$SDK_VERSION_NAME-Prod" else "$SDK_VERSION_NAME-Stag"

    const val OWN_CONFIG_BASE_URL_PROD = "https://api.robylon.ai"
    const val OWN_CONFIG_BASE_URL_STAGE = "https://stage-api.robylon.ai"
    const val OWN_API_KEY = "30e4fab6-cadb-4b99-b1e7-30fca6e147ac"

    object Android {
        const val minSdk = 19
        const val targetSdk = 33
        const val compileSdk = 33
        const val buildToolsVersion = "33.0.0"
    }

    object Publication {
        //TODO : Niks revisit
        const val GROUP = "com.own"
        var VERSION = SDK_VERSION_NAME

        const val PUBLISH_GROUP_ID = "com.own"
        var PUBLISH_ARTIFACT_ID = BUILD_TYPE.name.lowercase()
        var PUBLISH_ARTIFACT_VERSION = SDK_VERSION_NAME
        const val POM_NAME = "own"
        var POM_DESCRIPTION = "OWN Android SDK release from commit id : ${"git rev-parse HEAD".execute().text().trim()}"
        const val POM_URL = "https://github.com/own/own-android-sdk"
        const val POM_LICENCE_NAME = "The Apache Software License, Version 2.0"
        const val POM_LICENCE_URL = "http://www.apache.org/licenses/LICENSE-2.0.txt"
        const val POM_DEVELOPER_NAME = "OWN Team"
        const val POM_DEVELOPER_EMAIL = "developers@own.com"
        const val POM_SCM_CONNECTION = "scm:git@github.com:own/own-android-sdk.git"
        const val POM_SCM_DEV_CONNECTION = "scm:git@github.com:own/own-android-sdk.git"
        const val POM_SCM_URL = "https://github.com/own/own-android-sdk"

        const val OSSRH_USERNAME = "XXX"
        const val OSSRH_PASSWORD = "XXXX"
    }

    object JetBrains {
        object Kotlin {
            // https://kotlinlang.org/docs/releases.html#release-details
            const val VERSION = "1.9.22"
        }
    }

    const val material = "1.4.0"
}

fun String.execute(): Process = ProcessGroovyMethods.execute(this)
fun Process.text(): String = ProcessGroovyMethods.getText(this)
