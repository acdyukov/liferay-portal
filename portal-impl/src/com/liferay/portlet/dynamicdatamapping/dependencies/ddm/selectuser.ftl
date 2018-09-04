<#include "../init.ftl">

<@aui["field-wrapper"] data=data required=required>
	<@aui.input cssClass=cssClass dir=requestedLanguageDir helpMessage=escape(fieldStructure.tip) label=escape(label) name=(namespacedFieldName + "_name") type="text" >
        <#if required>
            <@aui.validator name="required" />
        </#if>
    </@aui.input>
    <@aui.input name=namespacedFieldName type="hidden" value=fieldValue/>
	${fieldStructure.children}
</@>

<@aui.script use="autocomplete-list,aui-base,aui-io-request-deprecated,autocomplete-filters,autocomplete-highlighters,datasource,datasource-get,datatable-datasource">

	var field = A.one('#${portletNamespace}${namespacedFieldName}_name');
	var idField = A.one('#${portletNamespace}${namespacedFieldName}');

	if (idField.val()) {
	    Liferay.Service(
          '/user/get-user-by-id',
          {
            userId: idField.val()
          },
          function(obj) {
            var fullName = obj.lastName + ' ' + obj.firstName + ' ' + obj.middleName;
            field.val(fullName);
          }
        );
	}

	var contactSearchDS = new A.DataSource.IO(
	{
		source: '/c/dynamic_data_mapping/get_site_users_action'
	});

	var contactSearchQueryTemplate = function(query)
	{
		var output = '?inputVal=' + query.trim() ;
		return output;
	};

	var contactSearchLocator = function(response)
	{
		var responseData = A.JSON.parse(response[0].responseText);
		return responseData;
	};

	var contactSearchFormatter = function(query, results)
	{
		return A.Array.map(results, function(result)
		{

			var resultStr = '<span>' + result.raw.name + '</span></br/>'
                + result.raw.screenName + ' ' + result.raw.emailAddress;
            if (result.raw.jobTitle != '') {
                resultStr =  resultStr + ' (' + result.raw.jobTitle + ')';
            }
			return resultStr;
		});
	};

	var contactSearchTextLocator = function(result)
	{
		idField.val(result.id);
		return result.name;
	};

	var contactSearchInput  = new A.AutoCompleteList(
	{
		allowBrowserAutocomplete: false,
		activateFirstItem: false,
		inputNode: field ,
		render: 'true',
		source: contactSearchDS,
		requestTemplate: contactSearchQueryTemplate,
		resultListLocator: contactSearchLocator,
		resultFormatter: contactSearchFormatter,
		resultTextLocator: contactSearchTextLocator,
		scrollIntoView: true
	}).render();
</@>
