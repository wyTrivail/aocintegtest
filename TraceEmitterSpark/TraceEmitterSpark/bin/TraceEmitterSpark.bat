@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  TraceEmitterSpark startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and TRACE_EMITTER_SPARK_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\TraceEmitterSpark.jar;%APP_HOME%\lib\spark-core-2.9.2.jar;%APP_HOME%\lib\httpclient-4.5.12.jar;%APP_HOME%\lib\opentelemetry-sdk-0.6.0.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\jetty-webapp-9.4.30.v20200611.jar;%APP_HOME%\lib\websocket-server-9.4.30.v20200611.jar;%APP_HOME%\lib\jetty-servlet-9.4.30.v20200611.jar;%APP_HOME%\lib\jetty-security-9.4.30.v20200611.jar;%APP_HOME%\lib\jetty-server-9.4.30.v20200611.jar;%APP_HOME%\lib\websocket-servlet-9.4.30.v20200611.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\opentelemetry-api-0.6.0.jar;%APP_HOME%\lib\guava-28.2-android.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\websocket-client-9.4.30.v20200611.jar;%APP_HOME%\lib\jetty-client-9.4.30.v20200611.jar;%APP_HOME%\lib\jetty-http-9.4.30.v20200611.jar;%APP_HOME%\lib\websocket-common-9.4.30.v20200611.jar;%APP_HOME%\lib\jetty-io-9.4.30.v20200611.jar;%APP_HOME%\lib\jetty-xml-9.4.30.v20200611.jar;%APP_HOME%\lib\websocket-api-9.4.30.v20200611.jar;%APP_HOME%\lib\opentelemetry-context-prop-0.6.0.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-compat-qual-2.5.5.jar;%APP_HOME%\lib\error_prone_annotations-2.3.4.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\jetty-util-9.4.30.v20200611.jar;%APP_HOME%\lib\grpc-context-1.28.0.jar


@rem Execute TraceEmitterSpark
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %TRACE_EMITTER_SPARK_OPTS%  -classpath "%CLASSPATH%" com.amazon.aocagent.App %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable TRACE_EMITTER_SPARK_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%TRACE_EMITTER_SPARK_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
