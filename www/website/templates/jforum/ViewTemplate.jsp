<%@ taglib uri="/WEB-INF/jspwiki.tld" prefix="wiki" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<wiki:BaseURL/>">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="style_jforum.css" rel="stylesheet" type="text/css">
<link href="templates/jforum/wiki.css" rel="stylesheet" type="text/css">
<title><wiki:Variable var="applicationname" />: <wiki:PageName /></title>
<script type="text/javascript" src="swfobject.js"></script>
</head>

<body class="main">
<table border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="5" align="center">
		<div id="flash_contents"></div>
		<script type="text/javascript">
		var flash = new SWFObject("header.swf", "jforum_header", "766", "206", "8");
		flash.write("flash_contents");
		</script>
	</td>
	</tr>
	<tr>
		<td width="11" rowspan="3">&nbsp;</td>
		<td colspan="3">&nbsp;</td>
		<td width="11" rowspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td valign="top" width="214"><table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><table width="214" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td colspan="3"><img src="images/dl_version.gif" width="214" height="48"></td>
						</tr>
					<tr>
						<td colspan="3" background="images/dl_bg.gif" valign="top" width="214">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td valign="top"><div style="padding:10px;"><img src="images/product_box.gif"></div></td>
									<td valign="top">
										<div style="padding:10px;" class="white">
											Many improvements were made, and bugs were fixed.
										</div>
										<div style="padding-top:5px;" class="white"><strong><a href="#" class="white">Details</a> | <a href="#" class="white">Download</a></strong></div>
										<div style="padding-top:5px; padding-bottom: 5px;"><a href="download.htm"><img src="images/dl_now.gif" border="0"></a></div>
									</td>
								</tr>
							</table>

						</td>
					</tr>
					<tr>
						<td width="10"><img src="images/dl_lower_left_corner.gif" width="10" height="11"></td>
						<td><img src="images/dl_lower_line.gif" width="194" height="11"></td>
						<td width="10"><img src="images/dl_lower_right_corner.gif" width="10" height="11"></td>
					</tr>
				</table></td>
			</tr>
			<tr>
				<td><table width="214" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="10"><img src="images/pl_top_left_corner.gif" width="10" height="10"></td>
						<td><img src="images/pl_top_line.gif" width="194" height="10"></td>
						<td width="10"><img src="images/pl_top_right_corner.gif" width="10" height="10"></td>
					</tr>
					<tr>
						<td background="images/pl_left_line.gif">&nbsp;</td>
						<td bgcolor="#3F3F3F">
							<div style="padding:5px 0px;"><img src="images/bt_pl.gif"></div>
							<div style="padding-top:5px; padding-left:10px;" class="white wiki-menu">
								<ul class="ul">
									<li class="li"><a href="/index.jsp" class="white">Home</a>
									<li class="li"><a href="/community.jsp" class="white">Forum</a>
									<li class="li"><a href="/Wiki.jsp" class="white">Documentation</a>
									<li class="li"><a href="#" class="white">How do I install JForum?</a>
									<li class="li"><a href="#" class="white">How to contribute</a>
									<li class="li"><a href="#" class="white">Getting help</a>
									<li class="li"><a href="#" class="white">Support the project</a>

									<wiki:UserCheck status="notAuthenticated">
										<li class="li"><span class="white"><wiki:Link jsp="Login.jsp">Log in - Wiki</wiki:Link></span>
									</wiki:UserCheck>

									<wiki:Permission permission="edit">
										<li class="li"><wiki:EditLink>Edit page - Wiki</wiki:EditLink>
										<li class="li"><a href="<wiki:UploadLink format='url' />">Attach File</a>
										<li class="li"><wiki:Link jsp="Logout.jsp">Log out</wiki:Link>
									</wiki:Permission>
								</ul>
							</div>
						</td>
						<td background="images/pl_right_line.gif">&nbsp;</td>
					</tr>
					<tr>
						<td><img src="images/pl_lower_left_corner.gif" width="10" height="9"></td>
						<td><img src="images/pl_lower_line.gif" width="194" height="9"></td>
						<td><img src="images/pl_lower_right_corner.gif" width="10" height="9"></td>
					</tr>
				</table></td>
			</tr>
			<tr>
				<td><table width="214" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td>&nbsp;</td>
					</tr>
				</table></td>
			</tr>
		</table>
		
		<div class="menu" style="padding-left:10px; ">
			� <b>JForum Team</b><br>
			Latest version is 2.1.7 <br>
			<a href="/wiki/Team" class="white">Meet the team</a> <br>
		</div>
		
		
		</td>
		<td width="4" valign="top">&nbsp;</td>
		<td valign="top" width="80%"><table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="15"><img src="images/cb_left_top_corner.gif" width="15" height="15"></td>
				<td height="15" background="images/cb_top_line.gif"></td>
				<td width="15"><img src="images/cb_right_top_corner.gif" width="15" height="15"></td>
			</tr>
			<tr>
				<td background="images/cb_left_line.gif">&nbsp;</td>
				<td bgcolor="#FFFFFF" width="100%">
					<div id="wiki-contents">
						<wiki:Content/>
					</div>
				</td>
				<td background="images/cb_right_line.gif">&nbsp;</td>
			</tr>
			<tr>
				<td><img src="images/cb_lower_left_corner.gif" width="15" height="15"></td>
				<td height="15" background="images/cb_lower_line.gif"></td>
				<td><img src="images/cb_lower_right_corner.gif" width="15" height="15"></td>
			</tr>
		</table></td>
	</tr>
	<tr>
		<td valign="top" class="menu">&nbsp;</td>
		<td>&nbsp;</td>
		<td align="center" class="menu">
			<div style="padding-top:5px; padding-bottom:30px;">
				<b><a href="/index.jsp" class="white">Home</a></b> | <b><a class="white" href="/download.jsp">Download</a></b> | <b><a class="white" href="/support.jsp">Support</a></b> | <b><a href="/community.jsp" class="white">Forum</a></b> | <b><a href="/development.jsp" class="white">Development</a></b> | <b><a href="/contact.jsp" class="white">Contact</a></b>			</div>
		</td>
	</tr>
</table>
</body>
</html>