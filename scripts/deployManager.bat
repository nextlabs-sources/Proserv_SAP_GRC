@echo off
REM ############################################################
REM ## SAP GRC Install Script
REM ## OS: Windows; PC: CE-Policy Controller
REM ############################################################
SETLOCAL ENABLEDELAYEDEXPANSION

REM #### Identify 64/32bit ###
IF "%~1"=="" (SET Computer=%ComputerName%) ELSE (SET Computer=%~1)
IF /I NOT "%Computer%"=="%ComputerName%" (
	PING %Computer% -n 2 2>NUL | FIND "TTL=" >NUL
)
FOR /F "tokens=2 delims==" %%A IN ('WMIC /Node:"%Computer%" Path Win32_Processor Get AddressWidth /Format:list') DO SET OSB=%%A

REM #### Default values
SET NXL_PC_HOME=C:\Program Files\NextLabs\Policy Controller\
SET SERVER_PREFIX=SERVGRC_
SET QUERY_RISK_HANDLER=GRAC_NL_AD

:ShowMainOptions
cls
echo ######### SAP GRC Deployment Manager ###########
echo.
echo NOTE: Please STOP Policy Controller before proceeding further.
echo.
echo    [1] Set root directory of Policy Controller [!NXL_PC_HOME!]. 
echo    [8] Install
echo    [9] Uninstall
echo    [0] Exit
echo.
GOTO ChooseMainOption

:ChooseMainOption
set CO=0
set /P CO=Choose an option [default is !CO!] :
IF  %CO% NEQ 1 IF %CO% NEQ 8 IF %CO% NEQ 9 IF %CO% NEQ 0 GOTO ShowMainOptions
set MO=MainOption!CO!
GOTO %MO%

:MainOption1
set /P NXL_PC_HOME=Enter root directory of PC[default is !NXL_PC_HOME!] :
set NXL_PC_HOME=!NXL_PC_HOME:"=!
GOTO ShowMainOptions

:MainOption8
cls
echo ######### SAP GRC Deployment Manager ###########
echo.
echo Installation Menu
echo.
echo    [1] Set Client Host [!CLIENT_HOST!]
echo    [2] Set Server Prefix [!SERVER_PREFIX!]
echo    [3] Set Query Risk Handler [!QUERY_RISK_HANDLER!]
echo    [9] Proceed with INSTALLATION. Make sure all above values are correctly set.
echo    [0] Back
echo.
GOTO ChooseInsOption

:MainOption9
cls
echo ######### SAP GRC Deployment Manager ###########
echo.
echo Uninstallation Menu
echo.
echo    [9] Proceed with UNINSTALLATION. All SAP GRC binaries and SAPGRCUserAttributePlugin.properties will be deleted.
echo    [0] Back
echo.
GOTO ChooseUninsOption

:MainOption0
GOTO FinalExit

:FinalExit
set /p DUMMY=Hit ENTER to complete.
EXIT /B

:ChooseInsOption
set CO=0
set /P CO=Choose an option [default is !CO!] :
IF  %CO% NEQ 0 IF %CO% NEQ 1 IF %CO% NEQ 2 IF %CO% NEQ 3 IF %CO% NEQ 4 IF %CO% NEQ 5 IF %CO% NEQ 6 IF %CO% NEQ 7 IF %CO% NEQ 8 IF %CO% NEQ 9 GOTO MainOption8
set IO=InsOption!CO!
GOTO %IO%

:InsOption0
GOTO ShowMainOptions

:InsOption1
set /P CLIENT_HOST=Enter Client Host[default is !CLIENT_HOST!] :
GOTO MainOption8

:InsOption2
set /P SERVER_PREFIX=Enter Server Prefix[default is !SERVER_PREFIX!] :
GOTO MainOption8

:InsOption3
set /P QUERY_RISK_HANDLER=Enter Query Risk Handler[default is !QUERY_RISK_HANDLER!] :
GOTO MainOption8

:InsOption9
echo Installing SAP GRC ...
echo #Auto Generated>.\config\temp.properties
FOR /F "tokens=* delims=" %%x in (.\config\SAPGRCUserAttributePlugin.properties) DO (
	set FILE_CONTENT=%%x
	set FILE_CONTENT=!FILE_CONTENT:$CLIENT_HOST$=%CLIENT_HOST%!
	set FILE_CONTENT=!FILE_CONTENT:$SERVER_PREFIX$=%SERVER_PREFIX%!
	set FILE_CONTENT=!FILE_CONTENT:$QUERY_RISK_HANDLER$=%QUERY_RISK_HANDLER%!
	echo !FILE_CONTENT!>>.\config\temp.properties
)
REM #### Create necessary folders ###
IF NOT EXIST "%NXL_PC_HOME%\jservice\jar\sap\" MKDIR "%NXL_PC_HOME%\jservice\jar\sap\"
IF NOT EXIST "%NXL_PC_HOME%\jservice\config\" MKDIR "%NXL_PC_HOME%\jservice\config\"
IF NOT EXIST "%NXL_PC_HOME%\jre\lib\ext\" MKDIR "%NXL_PC_HOME%\jre\lib\ext\"
REM #### Copy files ###
COPY .\config\temp.properties "%NXL_PC_HOME%\jservice\config\SAPGRCUserAttributePlugin.properties" >NUL
DEL  .\config\temp.properties >NUL
COPY .\jars\SAPGRCUserAttributePlugin*.jar "%NXL_PC_HOME%\jservice\jar\sap\" >NUL
echo DONE.
GOTO FinalExit

:ChooseUninsOption
set CO=0
set /P CO=Choose an option [default is !CO!] :
IF  %CO% NEQ 0 IF %CO% NEQ 9 GOTO MainOption9
set UO=UninsOption!CO!
GOTO %UO%

:UninsOption0
GOTO ShowMainOptions

:UninsOption9
echo Uninstalling SAP JCo ...
REM #### Delete files ###
DEL /Q "%NXL_PC_HOME%\jservice\config\SAPGRCUserAttributePlugin.properties" 2>NUL
DEL /Q "%NXL_PC_HOME%\jservice\jar\sap\SAPGRCUserAttributePlugin.jar" 2>NUL
echo DONE.
GOTO FinalExit