package org.chai.memms

import java.nio.charset.Charset;
import org.chai.memms.util.Utils;
import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.TUESDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;
import static org.joda.time.DateTimeConstants.THURSDAY;
import static org.joda.time.DateTimeConstants.FRIDAY;
import static org.joda.time.DateTimeConstants.SATURDAY;

class FormTagLib {

	def languageService
	def grailsApplication
	def now = Utils.now()
		
	def file = { attrs, body ->
		attrs["requestCharset"] = request?.characterEncoding==null?Charset.defaultCharset():Charset.forName(request.characterEncoding)
		attrs["availableCharsets"] = grailsApplication.config.file.upload.available.charset.collect {Charset.forName(it)}
		attrs["delimiter"] = grailsApplication.config.file.upload.delimiter
		out << render(template:"/tags/form/file", model: attrs)
	}

	def weekDays = { attrs, body ->
		out << render(template:"/tags/form/weekDays", model: attrs)
	}

	def daysOfWeek = {attrs, body ->

        def days = [
            [key: MONDAY, value: 'Monday'],
            [key: TUESDAY, value: 'Tuesday'],
            [key: WEDNESDAY, value: 'Wednesday'],
            [key: THURSDAY, value: 'Thursday'],
            [key: FRIDAY, value: 'Friday'],
            [key: SATURDAY, value: 'Saturday'],
            [key: SUNDAY, value: 'Sunday']
        ]
        
        days.eachWithIndex { def day, int index ->            
            out << g.checkBox(name: attrs.name, id: "${attrs.name}_${index}", value: day.key, checked: (attrs.selectedDays?.contains(day.key)), title: day.value)
            out << "<span>${day.value[0..0]}</span>"
        }
    }

    def occurInterval = { attrs, body ->
    	if (attrs["range"] == null) attrs["range"] = 10
		out << render(template:"/tags/form/occurInterval", model: attrs)
	}

	def inputYearMonth = { attrs, body ->
		if (attrs["maxlength"] == null) attrs["maxlength"] = 2
		out << render(template:"/tags/form/inputYearMonth", model: attrs)
	}
	def inputHourMinute = { attrs, body ->
		if (attrs["maxlength"] == null) attrs["maxlength"] = 2
		out << render(template:"/tags/form/inputHourMinute", model: attrs)
	}

	def inputTimeDate = { attrs, body ->
		out << render(template:"/tags/form/inputTimeDate", model: attrs)
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
		if (attrs["width"] == null) attrs["width"] = '300'
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
		if (attrs["id"] == null) attrs["id"] = 'date-field-one'
		if (attrs["maxRangeYear"] == null) attrs["maxRangeYear"] = now.year+1900
		if (attrs["minRangeYear"] == null) attrs["minRangeYear"] = 1970
		out << render(template:"/tags/form/inputDate", model: attrs)
	}

	def timeDate = { attrs, body ->
		out << render(template:"/tags/form/timeDate", model: attrs)
	}
	
	def listCheckBox = { attrs, body ->
		out << render(template:"/tags/form/listCheckBox", model: attrs)
	}
	
	def currency = { attrs, body ->
		out << render(template:"/tags/form/currency", model: attrs)
	}

	def inputPlusSelect = { attrs, body ->
		out << render(template:"/tags/form/inputPlusSelect", model: attrs)
	}
	
	def address = { attrs, body ->
		out << render(template:"/tags/form/address", model: attrs)
	}
		
	def selectFromEnum = { attrs, body ->
		out << render(template:"/tags/form/selectFromEnum", model: attrs)
	}
	
	def selectFromList = { attrs, body ->
		out << render(template:"/tags/form/selectFromList", model: attrs)
	}
}
