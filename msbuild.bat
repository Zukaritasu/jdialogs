@echo off
set path=%cd%
cd C:\Program Files\MSBuild\14.0\Bin
MSBuild.exe %path%\vstudio\jdialogs.vcxproj -p:Configuration=Debug
pause=nul