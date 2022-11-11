echo off
cls
echo “SCRIPT .BAT para realizar backup”
pause
cd\docume~1\%username%\documentos
copy| * d:\Arquivos_Backup
pause
echo “Abrir calculadora”
start calc.exe
pause