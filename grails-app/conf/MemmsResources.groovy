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

/**
 * @author Jean Kahigiso M.
 *
 */
modules = {

	// overrides, let's put jquery in the core bundle
	overrides {
		jquery {
			defaultBundle 'core'
		}
	}

	// modules
	core {
		dependsOn 'jquery,foldable'

		resource url: '/css/screen.css', bundle: 'core'
	}
	error {
		resource url: '/css/errors.css'
	}

	chosen {
		dependsOn 'jquery'

		resource url: '/js/jquery/chosen/chosen.js', bundle: 'core'
		// resource url: '/js/jquery/chosen/chosen.jquery.js', bundle: 'core'
		resource url: '/js/jquery/chosen/ajax-chosen.js', bundle: 'core'
		resource url: '/js/jquery/chosen/chosen.css', bundle: 'core'
	}

	jqueryui {
		dependsOn 'jquery'

		resource url: '/js/jquery/jquery-ui/css/cupertino/jquery-ui-1.9.2.custom.min.css', bundle: 'core'
		resource url: '/js/jquery/jquery-ui/css/timepicker.css', bundle: 'core'
		resource url: '/js/jquery/jquery-ui/js/jquery-ui-1.9.2.custom.min.js', bundle: 'core'
		resource url: '/js/jquery/jquery-ui/js/jquery-ui-timepicker-addon.js', bundle: 'core'
		resource url: '/js/jquery/jquery-ui/js/jquery-ui-sliderAccess.js', bundle: 'core'
	}

	fullCalendar{
		dependsOn 'jquery'
		resource url: '/js/jquery/jquery-ui/css/fullcalendar.css', bundle: 'core'
		resource url: '/js/jquery/jquery-ui/js/fullcalendar.min.js', bundle: 'core'
	}

	calendar{
		dependsOn 'fullCalendar'
		resource url: '/css/calendar.css', bundle: 'core'
		resource url: '/js/calendar.js', bundle: 'core'
	}

	fieldselection {
		dependsOn 'jquery'
		resource url: '/js/jquery/fieldselection/jquery.fieldselection.js', bundle: 'core'
	}

	cluetip {
		dependsOn 'jquery'

		resource url: '/js/jquery/cluetip/jquery.cluetip.js', bundle: 'core'
		resource url: '/js/jquery/cluetip/lib/jquery.hoverIntent.js', bundle: 'core'
		resource url: '/js/jquery/cluetip/jquery.cluetip.css', bundle: 'core'
		resource url: '/js/cluetip_init.js', bundle: 'core'
	}

	tipsy {
		dependsOn 'jquery'

		resource url: '/js/jquery/tipsy/src/javascripts/jquery.tipsy.js', bundle: 'core'
		resource url: '/js/tipsy_init.js', bundle: 'core'
	}

	form {
		dependsOn 'jquery,cluetip,fieldselection,jqueryui'

		resource url: '/js/jquery/form/jquery.form.js', bundle: 'core'
		resource url: '/js/form-util.js', bundle: 'core'
		resource url: '/js/form_init.js', bundle: 'core'
	}

	foldable {
		dependsOn 'jquery'

		resource url: '/js/foldable_init.js', bundle: 'core'
	}

	dropdown {
		dependsOn 'jquery'

		resource url: '/js/dropdown_init.js', bundle: 'core'
	}

	progressbar {
		dependsOn 'jquery'

		resource url: '/js/jquery/progressbar/jquery.progressbar.js', bundle: 'core'
		resource url: '/js/progressbar_init.js', bundle: 'core'
	}

	list {
		dependsOn 'core,form,fieldselection,cluetip,dropdown,chosen'
	}

	reports {
		dependsOn 'core,form,tipsy,jquery,jqueryui,chosen,fieldselection'

		// TODO ?
		// Load Droid Sans remotely from Google Webfonts
		// resource url: 'http://fonts.googleapis.com/css?family=Droid+Sans:400,700&subset=latin'
		resource url: '/js/reports/reports_init.js'
		resource url: '/js/reports/listing_init.js'
		resource url: '/js/reports/customizedlisting_init.js'
	}
}
