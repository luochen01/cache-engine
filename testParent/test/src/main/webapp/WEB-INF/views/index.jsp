<%@ page trimDirectiveWhitespaces="true" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="res" uri="http://www.ebay.com/webres"%>

<!doctype html>
<html>
<res:head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="main" />
	<title>eBay</title>
</res:head>
<res:body>
	<div class="bg-wrapper"></div>
	<div class="page">

		<res:useCss value="${res.css.local.sample_css}"/>
		
		<div><res:img value="${res.img.local.raptor_gif}"></res:img></div>
		<div class="ebay-logo"></div>
		
		<h1 class="title"><res:content value="${res.content.global.test.Sample.welcome}"/></h1>
		
        <h3>${greeting}</h3>
        
        <p>This is a sample web application generated from Raptor wizard. To change the content of this page, modify <b>/webapp/WEB-INF/views/index.jsp</b></p>
        <div>This app demonstrates:
        	<ul>
        		<li>Directory structure of Raptor web app</li>
        		<li>How to include JS, CSS and Image resources</li>
        		<li>JS and CSS Resource Aggregation using slots</li>
        		<li>Adding static content</li>
        	</ul>
        </div>

		<%-- Slots are locations to place resources such as js and css.
			The res:useCss and res:useJs can then refer to the slot as the
			location to place the js/css resources --%>
		<res:jsSlot id="js-slot1" />
		<res:useJs value="${res.js.local.sample1_js}" target="js-slot1"/>

		
		<res:jsSlot id="js-slot2" />		
		<res:useJs value="${res.js.local.sample2_js}" target="js-slot2" />
		<res:useJs value="${res.js.local.sample3_js}" target="js-slot2" />

		
		<!-- Page Data --->
		<div class="mt30">
			<a href="/admin/v3console/ValidateInternals" target="_blank">Validate Internals</a>
		</div>
		
		
		<div class="mt30">
			To learn more about Raptor, please visit:
			<p><a href="http://raptor" target="_blank">http://raptor</a></p>
		</div>
		<!-- Page Data --->
		
		<div class="footer">
			<a href="http://www.ebay.com">eBay</a> | 
			<a href="http://pulse.ebay.com">eBay Pulse</a> | 
			<res:link value="${res.link.pages.mobile.index_html }">eBay Mobile</res:link> | 
			<a href="http://deals.ebay.com">eBay Daily Deals</a>
		</div>
	</div>
	
</res:body>
</html>