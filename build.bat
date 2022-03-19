@echo off
set path=%cd%
cd C:\Program Files\MSBuild\14.0\Bin
MSBuild.exe %path%\vstudio\jdialogs.vcxproj -p:Configuration=Release -p:Platform=Win32
MSBuild.exe %path%\vstudio\jdialogs.vcxproj -p:Configuration=Release -p:Platform=x64
pause=nul