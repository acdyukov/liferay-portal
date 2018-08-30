<#include "../init.ftl">

<#assign DATE = staticUtil["java.util.Calendar"].DATE>
<#assign MONTH = staticUtil["java.util.Calendar"].MONTH>
<#assign YEAR = staticUtil["java.util.Calendar"].YEAR>
<#assign HOUR = staticUtil["java.util.Calendar"].HOUR_OF_DAY>
<#assign MINUTE = staticUtil["java.util.Calendar"].MINUTE>

<#assign calendar = calendarFactory.getCalendar(timeZone)>
<#assign fieldValue = calendarFactory.getCalendar(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DATE))>

<#assign hourValue = paramUtil.getInteger(request, "${namespacedFieldName}Hour", fieldValue.get(HOUR))>
<#assign minuteValue = paramUtil.getInteger(request, "${namespacedFieldName}Minute", fieldValue.get(MINUTE))>

<@aui["field-wrapper"] data=data helpMessage=escape(fieldStructure.tip) label=escape(label) name=namespacedFieldName required=required>
	<@liferay_ui["input-time"]
		hourParam="${namespacedFieldName}Hour"
		hourValue=hourValue
		minuteParam="${namespacedFieldName}Minute"
		minuteValue=minuteValue
		name="${namespacedFieldName}"
	/>

	${fieldStructure.children}
</@>