import org.codehaus.groovy.grails.resolve.GrailsRepoResolver;

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
        excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        mavenCentral()
        mavenLocal()
		mavenRepo "http://m2repo.spockframework.org/snapshots"
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
		

		/**
		* Configure our resolver.
		*/
		def libResolver = new GrailsRepoResolver(null, null);
		libResolver.addArtifactPattern("https://github.com/fterrier/repository/raw/master/[organisation]/[module]/[type]s/[artifact]-[revision].[ext]")
		libResolver.addIvyPattern("https://github.com/fterrier/repository/raw/master/[organisation]/[module]/ivys/ivy-[revision].xml")
		libResolver.name = "github"
        //libResolver.settings = ivySettings
		resolver libResolver
		
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		// because of GRAILS-6147, this dependency is in lib instead of here
		//compile group: "net.sf.json-lib", name: "json-lib", version: "2.4", classifier: "jdk15"
		
//		test "org.codehaus.geb:geb-spock:0.7.1"
//		test("org.seleniumhq.selenium:selenium-firefox-driver:latest.release")
//		test("org.seleniumhq.selenium:selenium-chrome-driver:latest.release")
//		test("org.seleniumhq.selenium:selenium-htmlunit-driver:latest.release") {
//			excludes "xml-apis"
//		}
		
		compile 'net.sf.ezmorph:ezmorph:1.0.6'
		runtime 'mysql:mysql-connector-java:5.1.5'
		compile 'commons-lang:commons-lang:2.6'
    }

    plugins {
        compile ":hibernate:$grailsVersion"
		compile ":mail:1.0"
        compile ":jquery:1.7.1"
        compile ":resources:1.2-RC1"
		compile ":cached-resources:1.0"
		compile ":cache-headers:1.1.5"
		compile ":shiro:1.1.5"
		compile ":springcache:1.3.1"
		compile ":compass-sass:0.7"
		compile ":mail:1.0"
		compile ":yui-minify-resources:0.1.5"
        build ":tomcat:$grailsVersion"
		
//		test ":geb:0.7.1"
		test (":spock:0.6") {changing = false}
    }
}
