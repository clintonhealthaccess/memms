/**
 * 
 */
package org.chai.memms

import org.chai.location.DataLocation;

/**
 * @author aphrorwa
 *
 */
class Part {
	DataLocation dataLocation
	Integer sparePartCount
	String toString() {
		return "DataLocation = " + dataLocation + " , sparePartCount = " + sparePartCount
	}

}
