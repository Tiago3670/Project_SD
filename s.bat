@echo OFF
SETLOCAL ENABLEDELAYEDEXPANSION
SET "destdir=C:\Users\tiago\OneDrive\Área de Trabalho\EI\3 Ano\SD\Project_SD\teste\"
SET "filename1=%1"
SET "outfile=%destdir%\out.txt"
SET /a count=0
FOR /f "delims=" %%a IN (%1) DO (
SET /a count+=1
SET "line[!count!]=%%a"
)
(
FOR /L %%a IN (%count%,-1,1) DO ECHO(!line[%%a]!
)>"%outfile%"

GOTO :EOF