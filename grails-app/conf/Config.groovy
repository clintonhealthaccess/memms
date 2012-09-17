/**
 * Copyright (c) 2012, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

//RESOURCES
environments {
	production {
		grails.resources.cdn.enabled = true
		// grails.resources.cdn.url = "http://static.mydomain.com/"
		// grails.resources.work.dir="/static/directory/"
		grails.resources.processing.enabled = true
	}
	development {
		grails.resources.cdn.enabled = false
		grails.resources.processing.enabled = false
		grails.resources.mappers.yuicssminify.disable=true
		grails.resources.mappers.yuijsminify.disable=true
	}
	test {
		grails.resources.cdn.enabled = false
		grails.resources.processing.enabled = false
		grails.resources.mappers.yuicssminify.disable=true
		grails.resources.mappers.yuijsminify.disable=true
	}
}

security.shiro.authc.required = false

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
	
	error  'grails.app.services.org.grails.plugin.resource',
			'grails.app.resourceMappers.org.grails.plugin.resource',
			'grails.app.taglib.org.grails.plugin.resource',
			'grails.app.resourceMappers.org.grails.plugin.cachedresources',
			'grails.app.services.grails.plugin.springcache',
			'grails.plugin.springcache'

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
		   
	debug  'grails.app',
		   'org.chai.memms'
	//trace 'org.hibernate.type'
}


/**
* Application specific config
*/

//MAIL
environments {
	production {
		//Mail
		grails {
			mail {
			  host = "smtp.gmail.com"
			  port = 465
			  // configuration defined in ${home}/.grails/memms-config.groovy
//			  username = "memms.rwanda@gmail.com"
//			  password = "root?user?96"
			  props = ["mail.smtp.auth":"true",
					   "mail.smtp.socketFactory.port":"465",
					   "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
					   "mail.smtp.socketFactory.fallback":"false"]
			}
		 }
		grails.mail.default.from="memms.rwanda@gmail.com"
	}
	development {
		//grails.mail.disabled = true
		//Mail
		grails {
			mail {
			  host = "smtp.gmail.com"
			  port = 465
			  // configuration defined in ${home}/.grails/memms-config.groovy
			  username = "memms.rwanda@gmail.com"
			  password = "root?user?96"
				props = [
							"mail.debug": "true",
							"mail.smtp.auth":"true",
							"mail.smtp.socketFactory.port":"465",
							"mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
							"mail.smtp.socketFactory.fallback":"false"
							]
			}
		 }
		grails.mail.default.from="memms.rwanda@gmail.com"
	}
	test {
		grails.mail.disabled = true
	}
}

// rabbitmq
rabbitmq {
	connectionfactory {
		username = 'guest'
		password = 'guest'
		hostname = '127.0.0.1'
		consumers = 5
	}
	queues = {
		adminQueue durable: true, autoDelete: false
	}
}
rabbitmq.concurrentConsumers = 1
rabbitmq.retryPolicy.maxAttempts = 1

site.fallback.language="en"
site.contact.email="memms.rwanda@gmail.com"
site.entity.list.max=40
site.from.email="memms.rwanda@gmail.com"

site.datalocationtype.checked=["District Hospital","Health Center"]
location.sector.skip.level=["Sector"]
site.possible.currency = ["":"NONE","RWF":"RWF","USD":"USD","EUR":"EUR"]

status.to.display.on.equipment.form = 5

i18nFields{
	locales = ["en","fr"]//,"rw"]
//	extraLocales = ["rw"]
}

file.upload.available.charset=["UTF-8", "ISO-8859-1"]
file.upload.delimiter=","

task.temp.folder='files/'

/**
* Configuration file override
*/
def locations = ["file:${userHome}/.grails/${appName}-config.groovy"]
if (System.properties['config']) locations.add("file:"+System.properties['config'])
environments {
   production {
	   grails.config.locations = locations
   }
}