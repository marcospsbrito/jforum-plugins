<html>
<head>
<title>JForum - Installation & Configuration</title>
<link href="jforum.css" rel="stylesheet" type="text/css" />
</head>
<body>

<jsp:include page="header.htm"/>

<table width="792" align="center" border="0">
	<tr height="10">
		<td colspan="2">&nbsp;</td>
	</tr>

	<tr>
		<td valign="top" rowspan="3" width="12%">
		<img src="dot.gif"> <a href="index.jsp">Main page</a><br/>
		<img src="dot.gif"> <a href="features.jsp">Features</a><br/>
		<img src="dot.gif"> <a href="download.jsp">Download</a>
		</td>
	</tr>

	<tr>
		<td colspan="2" valign="top">
			<font style="font-family: verdana; font-size: 22px;"><b>Installation & Configuration - Manual Install</b></font><br/>
			<img src="h_bar.gif" width="100%" height="2">
		</td>
	</tr>

	<tr>
		<td valign="top">
<p>
Here will be showed how to manually configure and install JForum. It is assumed that the you has some 
knowledge on how to install / configure a Java servlet Container ( or already has one up and running ), and the database is properly configured.
</p>

<p>
<br/>
<b>For automated installation, <a href="install.jsp">Click Here</a></b>
<br/></br>
</p>

<p class="note">Note: These instructions are for the installation of JForum, release version 2.0 Some of the steps here may not be valid for 
older versions, which are no longer supported.</p>
</p>

<!-- Options -->
<br><a href="#downloading">Downloading</a>
<br><a href="#installing">Installing</a>
<br><a href="#databaseConfig">Database Configuration</a>
<br><a href="#createTables">Creating the database tables</a>
<br><a href="#populating">Populating the tables</a>
<br><br><a href="#misc">Security Information and Considerations</a>


<!-- Downloadig -->
<p><img src="info.jpg" align="middle" border="0"> <a name="downloading"></a><span class="install_subtitle">Downloading JForum</span>
<p>
To get JForum, go to the <a href="download.htm">download page</a> and get the latest version.
</p>

<!-- Installing -->
<br><img src="info.jpg" align="middle" align="middle"> <a name="installing"></a><span class="install_subtitle">Installing</span>
<p>
After the download, unpack the .ZIP file into your webapp's directory (or anyplace you want to put it). A directory named
<br>
<br><i>JForum-&lt;release&gt;</i>
<br>
<br>will be created, where <i>&lt;release&gt;</i> is the version, which may be "2.0", "2.7.1" etc.. it is just to easily identify the version. You can rename the directory if you want. The next step you should do is register the JForum application within your Servlet Container, like Tomcat. This document will use the context name "jforum", but of course you can give the name you want.
</p>

<!-- Database Configuration -->
<br><a name="databaseConfig"></a><img src="info.jpg" align="middle" border="0"> <span class="install_subtitle">Database Configuration</span>
<p>
First of all, you must have <a href="http://www.mysql.com" target="_new">MySQL</a> or <a href="http://www.postgresql.org" target="_new">PostgreSQL</a>
installed and properly configured. <a href="http://hsqldb.sourceforge.net/">HSQLDB</a> is supported as well, and has built-in support, so you don't
need to download it.
<br/><br/>
Open the file <i>WEB-INF/config/SystemGlobals.properties</i>. Now search for a key named <i>database.driver.name</i> and configure it according
to the following table: 

<br/><br/>
<table width="70%" align="center">
	<tr>
		<td>
			<table width="100%" bgcolor="#ff9900" cellspacing="2">
				<tr class="fields">
					<th>Database</th>
					<th>key Value</th>
				</tr>

				<tr class="fields">
					<td><b>MySQ</b>L</td>
					<td><i>mysql</i></td>
				</tr>

				<tr class="fields">
					<td><b>PostgeSQL</b></td>
					<td><i>postgresql</i></td>
				</tr>

				<tr class="fields">
					<td><b>HSQLDB</b></td>
					<td><i>hsqldb</i></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td align="center">Key: <i>database.driver.name</i></td>
	</tr>
</table>
<br/>

The default value is <i><b>mysql</b></i>, which means JForum will try to use MySQL. 

<br>
<br>Next, you can tell JForum whether to use a Connection Pool or not. A connection pool will increase the performance of your application, 
but there are some situations where the use of a connection pool is not recommended or even possible, so you can change it according to your
needs. 
</p>

<p>
<br>By default JForum <b>uses</b> a connection pool, option which is specified by the key <i>database.connection.implementation</i>.
The following table shows the possible values for this key:

<br/><br/>
<table width="70%" align="center">
	<tr>
		<td>
			<table width="100%" bgcolor="#ff9900" cellspacing="2">
				<tr class="fields">
					<th>Connection Storage Type</th>
					<th>key Value</th>
				</tr>

				<tr class="fields">
					<td><b>Pooled Connections</b></td>
					<td><i>net.jforum.PooledConnection</i></td>
				</tr>

				<tr class="fields">
					<td><b>Simple Connections</b></td>
					<td><i>net.jforum.SimpleConnection</i></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td align="center">Key: <i>database.connection.implementation</i></td>
	</tr>
</table>
<br/>

<br> <img src="info.jpg" align="middle" border="0"> Edit the file <i>WEB-INF/config/database/&lt;<b>DBNAME</b>&gt;/&lt;<b>DBNAME</b>&gt;.properties</i>, 
where &lt;<b>DBNAME</b>&gt; is the database name you are using - for instance, <i>mysql</i>, <i>postgresql</i> or <i>hsqldb</i>. 
In this file there are some options you should change, according to the table below:

<br/><br/>
<table width="70%" align="center">
	<tr>
		<td>
			<table width="100%" bgcolor="#ff9900" cellspacing="2">
				<tr class="fields">
					<th>Key Name</th>
					<th>key Value description</th>
				</tr>

				<tr class="fields">
					<td><b>database.connection.username</b></td>
					<td>Database username</td>
				</tr>

				<tr class="fields">
					<td><b>database.connection.password</b></td>
					<td>Database password</td>
				</tr>

				<tr class="fields">
					<td><b>database.connection.host</b></td>
					<td>The host where the database is located</td>
				</tr>

				<tr class="fields">
					<td><b>dbname</b></td>
					<td>The database name. The default value is jforum. All JForum tables are preceded by "jforum_", 
					so you don't need to worry about conflicting table names.</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td align="center">File: <i>WEB-INF/config/database/&lt;DBNAME&gt;/&lt;DBNAME&gt;.properties</i></td>
	</tr>
</table>
<br/>

<br>The other properties you may leave with the default values if you don't know what to put. 
</p>


<!-- Creating Database Tables -->
<br><a name="createTables"></a><img src="info.jpg" align="middle" align="middle"> <span class="install_subtitle">Creating the database tables</span>
<p>The next step is to create the tables. To do that, use the import script named "&lt;<b>DBNAME</b>&gt;_db_struct.sql", placed at <i>WEB-INF/config/database/&lt;<b>DBNAME</b>&gt;</i>. This script will create all necessary tables to run JForum. The script were tested and should work with no problem at all. 
<br>Also, please keep in mind that if you are upgrading JForum you need to take a look to see if a migration script exists. Look in the file named "Readme.txt" in the root directory to see.
</p>

<p>
<a name="populating"></a><img src="info.jpg" align="middle" border="0"> <span class="install_subtitle"><b>Populating the tables</b></span><br>
 Now it is time to run the script to populate the database tables. To do that, use the script named "&lt;<b>DBNAME</b>&gt;_data_dump.sql", also located at <i>WEB-INF/config/database/&lt;<b>DBNAME</b>&gt;</i>. One more time, you should have no problems with this step. If you do, please remember to inform the error message, as well the database name and version you're using. 
<br>
<br>

<!-- Misc -->
<a name="misc"></a>
<img src="info.jpg" align="middle" border="0"> 
<span style="font-size: 16px; line-height: normal"><b><u>Security Information and Considerations</u></b></span>
<br><li><b><font color="#ff0000">Remove the line <font color="#006699"><i>"install = net.jforum.view.install.InstallAction"</i></font> from the file
<i>WEB-INF/config/modulesMapping.properties</i></font></b><br>

<br><li> JForum uses a servlet mapping to invoke the pages. This mapping is <b>*.page</b>, and is already properly configured at WEB-INF/web.xml. If you are running JForum on certain ISPs, you may need to contact their Technical Support and ask them to explicity enable these mapping for you.

<br>
<br><li> The directory "images", "tmp" and "WEB-INF" ( e its sub-directories ) should have write permission 
to the user who runs the web server. You'll get nasty exceptions if there is no write permission.
<br>
<br><li> The administration interface is accessible via the link <i>Admin Control Panel</i>, located in the bottom of the main page. You will only see this link if you are logged as Administrator. See above the default password for the admin user: 
<br>
<br>The username is <i>Admin</i> and the password is <i>admin</i>
<br>
<br><li> This step is <b>HIGHLY</b> recommended: Open the file <i>WEB-INF/config/SystemGlobals.properties</i> and search for a key named <i>user.hash.sequence</i>. There is already a default value to the key, but is <b>VERY RECOMMENDED</b> that you change the value. It may be anything, and you won't need to remember the value. You can just change one or other char, insert some more... just type there some numbers and random characters, and then save the file. This value will be used to enhance the security of your JForum installation, and you will just need to do this step once.

		</td>
	</tr>
</table>

<jsp:include page="bottom.htm"/>

</body>
</html>
