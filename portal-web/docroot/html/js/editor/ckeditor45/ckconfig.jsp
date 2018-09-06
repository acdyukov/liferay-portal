<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */
--%>



<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ContentTypes" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.LocaleUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.xuggler.XugglerUtil" %>
<%@page import="com.liferay.portal.service.PortletLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.Portlet"%>

<%@ page import="java.util.Locale" %>

<%
String colorSchemeCssClass = ParamUtil.getString(request, "colorSchemeCssClass");

String contentsLanguageId = ParamUtil.getString(request, "contentsLanguageId");

Locale contentsLocale = LocaleUtil.fromLanguageId(contentsLanguageId);

contentsLanguageId = LocaleUtil.toLanguageId(contentsLocale);

String cssPath = ParamUtil.getString(request, "cssPath");
String cssClasses = ParamUtil.getString(request, "cssClasses");
boolean inlineEdit = ParamUtil.getBoolean(request, "inlineEdit");

String languageId = ParamUtil.getString(request, "languageId");

Locale locale = LocaleUtil.fromLanguageId(languageId);

languageId = LocaleUtil.toLanguageId(locale);

String name = ParamUtil.getString(request, "name");
boolean resizable = ParamUtil.getBoolean(request, "resizable");

response.setContentType(ContentTypes.TEXT_JAVASCRIPT);

Portlet uploadArticlePortlet = null;

try{
	uploadArticlePortlet = PortletLocalServiceUtil.getPortletById(PortalUtil.getCompanyId(request), "1003_WAR_uploadarticleportlet");
}catch(Exception ex){}

boolean isUploadArticleAvailable = false;

if (uploadArticlePortlet != null) {
	isUploadArticleAvailable = uploadArticlePortlet.isActive();
}

%>

;window['<%= HtmlUtil.escapeJS(name) %>Config'] = function() {
	var ckEditor = CKEDITOR.instances['<%= HtmlUtil.escapeJS(name) %>'];
	
	CKEDITOR.dtd.$removeEmpty['span'] = 0;
	CKEDITOR.dtd.$removeEmpty['style'] = 0;
	CKEDITOR.dtd.$removeEmpty['i'] = 0;
	CKEDITOR.dtd.$removeEmpty['br'] = 0;
	
	CKEDITOR.dtd.span.div = 1;
	CKEDITOR.dtd.span.a = 1;
	CKEDITOR.dtd.span.p = 1;
	CKEDITOR.dtd.span.i = 1;
	CKEDITOR.dtd.span.br = 1;

	CKEDITOR.dtd.a.div = 1;
	CKEDITOR.dtd.a.span = 1;
	CKEDITOR.dtd.a.p = 1;
	CKEDITOR.dtd.a.i = 1;
	CKEDITOR.dtd.a.br = 1;
	
	CKEDITOR.dtd.body.meta = 1;
	CKEDITOR.dtd.body.title = 1;
	CKEDITOR.dtd.body.style = 1;
	

	if (!CKEDITOR.stylesSet.get('liferayStyles')) {
		CKEDITOR.addStylesSet(
			'liferayStyles',
			[

			// Block Styles

			{name: 'Normal', element: 'p'},
			{name: 'Heading 1', element: 'h1'},
			{name: 'Heading 2', element: 'h2'},
			{name: 'Heading 3', element: 'h3'},
			{name: 'Heading 4', element: 'h4'},

			// Special classes

			{name: 'Preformatted Text', element:'pre'},
			{name: 'Cited Work', element:'cite'},
			{name: 'Computer Code', element:'code'},

			// Custom styles

			{name: 'Info Message', element: 'div', attributes: {'class': 'portlet-msg-info'}},
			{name: 'Alert Message', element: 'div', attributes: {'class': 'portlet-msg-alert'}},
			{name: 'Error Message', element: 'div', attributes: {'class': 'portlet-msg-error'}}
			]
		);
	}

	var config = ckEditor.config;

	config.autoParagraph = false;

	config.autoSaveTimeout = 3000;

	config.bodyClass = 'html-editor <%= HtmlUtil.escapeJS(colorSchemeCssClass) %> <%= HtmlUtil.escapeJS(cssClasses) %>';

	config.closeNoticeTimeout = 8000;

	config.contentsCss = [
		'<%= HtmlUtil.escapeJS(cssPath) %>/aui.css', 
		'<%= HtmlUtil.escapeJS(cssPath) %>/main.css', 
		themeDisplay.getPortalURL() + themeDisplay.getPathJavaScript() + '/editor/ckeditor45/plugins/fontawesome/font-awesome/css/font-awesome.min.css',
		themeDisplay.getPortalURL() + themeDisplay.getPathJavaScript() + '/editor/ckeditor45/plugins/bootstrapButtons/bootstrap/css/bootstrap.min.css',
		themeDisplay.getPortalURL() + themeDisplay.getPathJavaScript() + '/editor/ckeditor45/plugins/bootstrapAlert/css/bootstrap.min.css'
	];

<%
String contentsLanguageDir = LanguageUtil.get(contentsLocale, "lang.dir");
%>

	config.contentsLangDirection = '<%= HtmlUtil.escapeJS(contentsLanguageDir) %>';

	config.contentsLanguage = '<%= contentsLanguageId.replace("iw_", "he_") %>';
	
	config.skin = 'office2013';

	config.entities = false;
	
	config.allowedContent = true;
	
	config.fillEmptyBlocks = false;
	
	config.tabSpaces = 0;
	
	config.enterMode = CKEDITOR.ENTER_BR;
	
	config.shiftEnterMode = CKEDITOR.ENTER_BR;
	
	config.extraAllowedContent = 'p(*)[*]{*};div(*)[*]{*};li(*)[*]{*};ul(*)[*]{*};span(*)[*]{*};a(*)[*]{*};i(*)[*]{*}';

	config.extraPlugins = 'a11yhelpbtn,media,scayt,wsc,lineheight,uploadArticle,fontawesome,font,bootstrapAlert,bootstrapButtons,texttransform,ecollapse,maximize,tablestickyfilter,pastebase64,base64image';

	<c:if test="<%= inlineEdit %>">
		config.extraPlugins += ',ajaxsave,restore,lineheight,uploadArticle,fontawesome,font,bootstrapAlert,bootstrapButtons,texttransform,ecollapse';
	</c:if>

	config.height = 265;

	config.language = '<%= languageId.replace("iw_", "he_") %>';

	config.pasteFromWordRemoveFontStyles = false;

	config.pasteFromWordRemoveStyles = false;

	config.resize_enabled = <%= resizable %>;

	<c:if test="<%= resizable %>">
		config.resize_dir = 'vertical';
	</c:if>

	config.stylesCombo_stylesSet = 'liferayStyles';

	config.title = false;

	config.toolbar_editInPlace = [
		['Styles'],
		['Bold', 'Italic', 'Underline', 'Strike'],
		['Subscript', 'Superscript', 'SpecialChar'],
		['Undo', 'Redo'],
		['SpellChecker', 'Scayt'],
		['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent'], ['Source', 'RemoveFormat'],
		['A11YBtn']
	];

	config.toolbar_email = [
		['FontSize', 'TextColor', 'BGColor', '-', 'Bold', 'Italic', 'Underline', 'Strike'],
		['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
		['SpellChecker', 'Scayt'],
		'/',
		['Undo', 'Redo', '-', 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'SelectAll', 'RemoveFormat'],
		['Source'],
		['Link', 'Unlink'],
		['Image'],
		['A11YBtn']
	];

	config.toolbar_liferay = [
		['Bold', 'Italic', 'Underline', 'Strike'],

		<c:if test="<%= inlineEdit %>">
			['AjaxSave', '-', 'Restore'],
		</c:if>

		['Undo', 'Redo', '-', 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord'],
		['Styles', 'Font', 'FontSize', '-', 'lineheight', '-', 'TextColor', 'BGColor'],
		'/',
		['FontAwesome'],['ECollapse'],
		['BootstrapAlert','BootstrapButtons'],
		['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent'],
		['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
		['TransformTextSwitcher','TransformTextToLowercase','TransformTextToUppercase','TransformTextCapitalize'],
		['Image', 'base64image', 'Link', 'Unlink', 'Anchor'],
		['Flash', <c:if test="<%= XugglerUtil.isEnabled() %>"> 'Audio', 'Video',</c:if> 'Table', '-', 'Smiley', 'SpecialChar'],
		['Find', 'Replace', 'SpellChecker', 'Scayt'],
		['SelectAll', 'RemoveFormat'],
		['Subscript', 'Superscript']

		<c:if test="<%= !inlineEdit %>">
			,['Source']
		</c:if>
		<c:if test="<%= isUploadArticleAvailable %>">
			,['UploadArticle']
		</c:if>
		,['Maximize']
		,['A11YBtn']
	];

	config.toolbar_liferayArticle = [
		['Styles', 'Font', 'FontSize', '-', 'lineheight', '-', 'TextColor', 'BGColor'],
		['FontAwesome'],['ECollapse'],
		['BootstrapAlert','BootstrapButtons'],
		['Bold', 'Italic', 'Underline', 'Strike'],
		['Subscript', 'Superscript'],
		'/',
		['Undo', 'Redo', '-', 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'SelectAll', 'RemoveFormat'],
		['Find', 'Replace', 'SpellChecker', 'Scayt'],
		['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
		['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
		['TransformTextSwitcher','TransformTextToLowercase','TransformTextToUppercase','TransformTextCapitalize'],
		'/',
		['Source'],
		<c:if test="<%= isUploadArticleAvailable %>">
			['UploadArticle'],
		</c:if>
		['Link', 'Unlink', 'Anchor'],
		['Image', 'base64image', 'Flash', <c:if test="<%= XugglerUtil.isEnabled() %>">'Audio', 'Video',</c:if> 'Table', '-', 'Smiley', 'SpecialChar', 'LiferayPageBreak'],
		['Maximize'],
		['A11YBtn']
	];

	config.toolbar_phone = [
		['Bold', 'Italic', 'Underline'],
		['NumberedList', 'BulletedList'],
		['Image', 'Link', 'Unlink']
	];

	config.toolbar_simple = [
		['Bold', 'Italic', 'Underline', 'Strike'],
		['NumberedList', 'BulletedList'],
		['Image', 'Link', 'Unlink', 'Table']
	];

	config.toolbar_tablet = [
		['Bold', 'Italic', 'Underline', 'Strike'],
		['NumberedList', 'BulletedList'],
		['Image', 'Link', 'Unlink'],
		['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
		['Styles', 'FontSize']
	];

	//обработчик закомментирован в рамках INGOSDEV-763, в результате его
	//работы редактор при редактировании во всплывающем окне (не в админке)
	//размещался неверно (слишком низко), не ясна необходимость этого
	//обработчика, поэтому он не удалён полностью
	/*CKEDITOR.on(
		'dialogDefinition',
		function(event) {
			var dialogDefinition = event.data.definition;

			var onShow = dialogDefinition.onShow;

			dialogDefinition.onShow = function() {
				if (typeof onShow === 'function') {
					onShow.apply(this, arguments);
				}

				if (window.top != window.self) {
					var editorElement = this.getParentEditor().container;

					var documentPosition = editorElement.getDocumentPosition();

					var dialogSize = this.getSize();

					var x = documentPosition.x + ((editorElement.getSize('width', true) - dialogSize.width) / 2);
					var y = documentPosition.y + ((editorElement.getSize('height', true) - dialogSize.height) / 2);

					this.move(x, y, false);
				}
			}
		}
	);*/
	CKEDITOR.addCss("body { padding: 10px 20px !important; } td, th { padding: 5px; }");
};

window['<%= HtmlUtil.escapeJS(name) %>Config']();
