@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
SET "destdir=C:\Users\dseabra\IdeaProjects\2022\Project_SD\Processor"
SET "filename1=%1"
SET "outfile=%1"
SET /a count=0
FOR /f "delims=" %%a IN (%filename1%) DO (
SET /a count+=1
SET "line[!count!]=%%a"
)
(
FOR /L %%a IN (%count%,-1,1) DO ECHO(!line[%%a]!
)>"%outfile%"

GOTO :EOF