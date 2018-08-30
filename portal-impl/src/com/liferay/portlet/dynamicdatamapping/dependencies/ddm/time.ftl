<#include "../init.ftl">

<#assign defaultHourValue = -1>
<#assign defaultMinuteValue = -1>

<#if (validator.isNotNull(fieldValue))>
    <#assign time = fieldValue?split(":")>
    <#assign defaultHourValue = getterUtil.getInteger(time[0])>
    <#assign defaultMinuteValue = getterUtil.getInteger(time[1])>
</#if>

<#assign hourValue = paramUtil.getInteger(request, "${namespacedFieldName}Hour", defaultHourValue) />
<#assign minuteValue = paramUtil.getInteger(request, "${namespacedFieldName}Minute", defaultMinuteValue) />

<@aui["field-wrapper"] data=data helpMessage=escape(fieldStructure.tip) label=escape(label) name=namespacedFieldName required=required>
	<@liferay_ui["input-time"]
		hourParam="${namespacedFieldName}Hour"
		hourValue=hourValue
		minuteParam="${namespacedFieldName}Minute"
		minuteValue=minuteValue
		name="${namespacedFieldName}"
	>
        <#if required>
            <@aui.validator name="required" />
        </#if>
    </@>

	${fieldStructure.children}
</@>