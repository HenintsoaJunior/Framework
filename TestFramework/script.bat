@echo all

rem == commentaire

rem Creation des variables
set "webapps=E:\logiciel\tomcat\webapps"
set "nomAppli=Framework"
set "temp=.\temp"
set "src=.\src"
set "lib=.\lib"
set "web=.\web"
set "webxml=.\webxml"
rem

rem Suppression de l'interieur de [temp] en supprimant les dossiers de maniere recursive
rmdir /s /q "%temp%\%nomAppli%"
rem

rem Creation d'un nouveau repertoire dans [temp]
mkdir "%temp%\%nomAppli%"
rem

rem Creation des structures de l'application [nomAppli]
mkdir "%temp%\%nomAppli%\WEB-INF"
mkdir "%temp%\%nomAppli%\WEB-INF\lib"
rem

rem Copie de l'interieur de [web], [webxml] et [lib] dans [nomAppli]

xcopy "%web%\*" "%temp%\%nomAppli%\" /s /e /y
for /F "tokens=*" %%A in (config.conf) do set "%%A"
xcopy "%root%\bin"  "%temp%\%nomAppli%\WEB-INF\classes" /s /e /y
xcopy "%webxml%\*" "%temp%\%nomAppli%\WEB-INF\" /s /e /y
xcopy "%lib%\*" "%temp%\%nomAppli%\WEB-INF\lib" /s /e /y
rem

rem Ajout de l'application dans une archive war
jar cvf "%nomAppli%.war" -C "%temp%\%nomAppli%" .
rem

rem Deploiement de l'application vers Tomcat
xcopy ".\%nomAppli%.war" "%webapps%" /y
rem

pause