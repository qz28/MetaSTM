@echo off
setlocal
if ""=="%METASTM%" set METASTM=%~dp0%..
set METASTM_LIB=%METASTM%\lib
java -Xms64m -Xmx256m -Djava.library.path="%METASTM_LIB%" -jar "%METASTM_LIB%/MetaSTM.jar" %*
