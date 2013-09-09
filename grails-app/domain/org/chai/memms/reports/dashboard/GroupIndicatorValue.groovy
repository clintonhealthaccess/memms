/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.chai.memms.reports.dashboard
import i18nfields.I18nFields

/**
 *
 * @author donatien
 */
@i18nfields.I18nFields
class GroupIndicatorValue {
	
    Date generatedAt
    String names
    Double value

    static belongsTo = [indicatorValue:IndicatorValue]

    static mapping ={
        table "memms_report_group_indicator_value"
        version false
        content type:"text"
    }

    static i18nFields = ["names"]

     static constraints = {
        names (blank:true, nullable:true, size:3..250)
    }

    @Override
    public String toString() {
        return "GroupIndicatorValue[#id = " + id + ", @" + generatedAt?.getTime() + ", names = " + names + ", value = " + value + "]"
    }
}