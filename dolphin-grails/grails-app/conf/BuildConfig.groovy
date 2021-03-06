grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        mavenLocal()
        mavenCentral()

        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        //mavenCentral()


        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        compile 'org.codehaus.gpars:gpars:1.0.0'

        def dolphinVersion = '1.0-SNAPSHOT'

        compile "org.open-dolphin:dolphin-shared:$dolphinVersion"
        compile "org.open-dolphin:dolphin-server:$dolphinVersion"
        compile "org.open-dolphin:dolphin-demo-javafx-server:$dolphinVersion"
        compile "org.open-dolphin:dolphin-demo-javafx-shared:$dolphinVersion"
    }

    plugins {
        runtime ":hibernate:$grailsVersion"

//        runtime ":cors:1.1.1"

        // runtime ":jquery:1.7.1"
        // runtime ":resources:1.1.6"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"
    }
}
