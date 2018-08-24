<#include "../init.ftl">

<#assign multiple = false>

<#if fieldStructure.multiple?? && (fieldStructure.multiple == "true")>
	<#assign multiple = true>
</#if>
<#assign label = escape(label)>
<#if required>
	<#assign label = label + ' <span class="label-required">' + languageUtil.get(requestedLocale, "required-field") + '</span>'>
</#if>

<@aui["field-wrapper"] data=data>
	<@aui.select cssClass=cssClass helpMessage=escape(fieldStructure.tip) label=label multiple=multiple name=namespacedFieldName>
		<#if multiple && required>
			<@aui.validator name="required" />
		</#if>
		${fieldStructure.children}
	</@aui.select>
</@>