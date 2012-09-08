package org.chai.memms

import java.nio.charset.Charset;

class FormTagLib {

	def languageService
	def grailsApplication
		
	def file = { attrs, body ->
		attrs["requestCharset"] = request?.characterEncoding==null?Charset.defaultCharset():Charset.forName(request.characterEncoding)
		attrs["availableCharsets"] = grailsApplication.config.file.upload.available.charset.collect {Charset.forName(it)}
		attrs["delimiter"] = grailsApplication.config.file.upload.delimiter
		out << render(template:"/tags/form/file", model: attrs)
	}
	
	def input = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		out << render(template:"/tags/form/input", model: attrs)
	}
	def inputBox = { attrs, body ->
		out << render(template:"/tags/form/checkBox", model: attrs)
	}
	
	def i18nInput = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		attrs["locales"] = languageService.getAvailableLanguages();
		out << render(template:"/tags/form/i18nInput", model: attrs)
	}
	
	def textarea = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		if (attrs["rows"] == null) attrs["rows"] = '1'
		out << render(template:"/tags/form/textarea", model: attrs)
	}
	
	def i18nTextarea = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		if (attrs["rows"] == null) attrs["rows"] = '4'
		if (attrs["width"] == null) attrs["width"] = '300'
		if (attrs["readonly"] == null) attrs["readonly"] = false
		attrs["locales"] = languageService.getAvailableLanguages();
		out << render(template:"/tags/form/i18nTextarea", model: attrs)
	}
	
	def inputDate = { attrs, body ->
		if (attrs["type"] == null) attrs["type"] = 'text'
		if (attrs["id"] == null) attrs["id"] = 'date-field-one'
		out << render(template:"/tags/form/inputDate", model: attrs)
	}
	
	def listCheckBox = { attrs, body ->
		out << render(template:"/tags/form/listCheckBox", model: attrs)
	}
		
	def selectFromEnum = { attrs, body ->
		out << render(template:"/tags/form/selectFromEnum", model: attrs)
	}
	
	def selectFromList = { attrs, body ->
		out << render(template:"/tags/form/selectFromList", model: attrs)
	}
}
