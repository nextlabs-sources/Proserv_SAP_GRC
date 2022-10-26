##########################################################################
############   SAPGRC-Plugin   ###############################
##########################################################################

SAP GRC Plugin (User Attribute Provider) for GRC Integration

1.	Objective
-----------------------------------------------------------------------------
The SAP GRC Plugin is a new product component which will enable communication 
between GRC and Java Policy controller/CE-PC.

2.	Scope & Design
-----------------------------------------------------------------------------
SCOPE:
This plugin is developed based on “user attribute provider” framework of Policy 
Controller platform and uses SAP’s JCo library.

ASSUMPTIONS:
•	SAPJCo-EntitlementManager plugin is already installed (Most important!!)

3.	Build From Source (only for developers to modify/enhance source code)
-----------------------------------------------------------------------------
•	Get the latest source from Perforce //depot/plugins/SAP_GRC/
•	Install Java version 1.5/above and ANT on your PC. Configure below environment variables;
	o	JAVA_HOME= {java jdk installation root folder}
	o	ANT_HOME= {ant installation root folder}
•	Append or add to PATH variable:  %JAVA_HOME% \bin; %ANT_HOME%\bin;
•	If any changes are made to the checked-out source code, build the SAP JCo binaries as below:
•	Open CMD, go to the SAP_JCo folder and run ant -f build.xml

4.	Distribution & Deployment          
-----------------------------------------------------------------------------
The SAP JCo 3.0 library requires a Java Runtime Environment (JRE) version 1.5 or above. 
Policy Controller is already bundled with a JRE and hence no need to separately install JRE.

All binaries related to this plugin are distributed as .zip file. Extract the contents of 
this .zip file to any temporary folder and to appropriate installation steps.

Pre-requisites
•	Install VC++ 2005 SP1 distribution and patch for SP1
	o	http://www.microsoft.com/downloads/details.aspx?familyid=EB4EBE2D-33C0-4A47-9DD4-B9A6D7BD44DA&displaylang=en (VC++ 2005 SP1)
	o	http://www.microsoft.com/downloads/details.aspx?displaylang=en&FamilyID=766a6af7-ec73-40ff-b072-9112bab119c2 (Patch for SP1)
•	Add SAP Gateway Host and port to 
	o	<Windows Home>\System32\drivers\etc\services file in Windows
	o	/etc/services file in Linux/Solaris
	o	Samples
			sapgw00	3300/tcp
			sapgw01	3301/tcp
	o	sapgw00 or sapgw01 etc. should match with the gateway service names that are configured 
		in the SAPJavaSDKService.properties (is part of SAPJCo-EntitlementManager distribution)

Installation (Manual steps for Windows CE-Policy Controller)
•	Stop Policy Controller, if it is already running. 
•	Create below folders, if these do not exist.
	o	[NextLabs install home]/Policy Controller/jservice/config
	o	[NextLabs install home]/Policy Controller/jservice/jar/sap
	o	[NextLabs install home]/Policy Controller/jre/lib/ext
•	From the extracted contents of the .zip file, copy below files;
	o	config/SAPGRCPlugin.propertie to /Policy Controller/jservice/config
	o	jars/SAPGRCPlugin.jar to Policy Controller/jservice/jar/ sap

Installation (Manual steps for Windows/Linux/Solaris Java PC)
Note: Refer Java Policy Controller related documentation to set up the Java PC. It is usually deployed 
in a Tomcat server environment. [tomcat-home] refers to the Tomcat root folder under which JPC is deployed.
•	Stop Policy Controller, if it is already running. 
•	Create below folders, if these do not exist.
	o	[tomcat-home]/nextlabs/dpc/jservice/config
	o	[tomcat-home]/nextlabs/dpc/jservice/jar/sap
	o	[tomcat-home]/nextlabs/shared_lib/
•	From the extracted contents of the .zip file, copy below files;
	o	config/SAPGRCPlugin.propertie to [tomcat-home]/nextlabs/dpc/jservice/config
	o	jars/SAPGRCPlugin.jar to [tomcat-home]/nextlabs/dpc/jservice/jar/ sap

################################	
## Installation (.bat script for Windows CE-Policy Controller)
##•	Stop Policy Controller, if it is already running. 
##•	Open CMD as Administrator and go to the folder where SAP JCo zip file is extracted.
##•	Run deployManager.bat and follow the on-screen instructions.
##•	Few points to note during the installation are;
##	o	Specify the Policy Controller root folder in the first screen, before choosing install or 
##		uninstall option.
##	o	In ‘install’ options, specify the password in plain text form. Password encryption (refer 
##		‘Password Encryption’ section below) is required only during manual installation process.
##	o	‘Uninstall’ option deletes all the jars, dlls, properties etc. Remember to take back up of 
##		the SAPJavaSDKService.properties, if many destinations are configured in it.
################################

Un-Installation (Manual steps for CE or Java PC on Win/Linux/Solaris)
•	Stop Policy Controller, if it is already running. 
•	Delete /jservice/config/SAPGRCPlugin.properties (take a backup if needed)
•	Delete /service/jar/sap/SAPGRCPlugin.jar
•	Start Policy Controller.

Settings in SAPGRCPlugin.properties file
•	All necessary SAP Destination settings should be part SAPJavaSDKService.properties, including GRC system specific destination.
•	The prefix of the GRC system destination alone must be specified in SAPGRCPlugin.properties.
•	“query_risk_handler” property must be set with appropriate value in SAPGRCPlugin.properties. 
•	“grc_obligation_name” property must be set with appropriate value in SAPJavaSDKService.properties.

5.	Contact
-----------------------------------------------------------------------------
For any further changes/enhancements or any queries, contact Srikanth.Karanam@nextlabs.com or TuanMinh.Duong@nextlabs.com 
