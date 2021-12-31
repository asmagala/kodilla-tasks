call runcrud
if %ERRORLEVEL% == 0 goto startbrowser
goto fail

:startbrowser
echo runcrud ERRORLEVEL = %ERRORLEVEL%
timeout 30
start "chrome" http://localhost:8080/crud/v1/task/getTasks
if %ERRORLEVEL% == "0" goto fine

:fail
echo.
echo Program finished with errors
exit

:fine
echo.
echo Program successfully complited :)