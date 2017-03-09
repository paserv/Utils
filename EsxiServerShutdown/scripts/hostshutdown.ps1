$hostAddresses = @("10.24.5.54", "10.24.5.55", "10.24.5.56", "10.24.5.57", "10.24.5.58", "10.24.4.52", "10.24.4.54", "10.24.4.73", "10.24.4.74", "10.24.4.75", "10.24.4.76", "10.24.4.77", "10.24.4.7")
$masterHost = "10.24.5.53"
$userName = "root"
$password = "crsnapoli"
$waitTime = 120

$currTime = Get-date -format "yyyy-MM-dd_HH-mm-ss"
$logFile = "C:\MultiLink\log\ShutdownLog_$currTime.log"

$currTime = Get-date -format "yyyy-MM-dd HH:mm:ss"
$currTime + " - Begin shutdown of all hosts" >> $logFile

foreach ($currAddress in $hostAddresses) {
	$currLogFile = "C:\MultiLink\log\VMware_Host_$currAddress.log"
	$currTime = Get-date -format "yyyy-MM-dd HH:mm:ss"
	$currTime + " - Begin shutdown of hosts: $currAddress" >> $logFile
	Start-Job -filepath C:\MultiLink\bin\ShutdownEsxi.ps1 -ArgumentList ($currAddress, $userName, $password, $waitTime, $currLogFile)
}

$currTime = Get-date -format "yyyy-MM-dd HH:mm:ss"
$currTime + " - Waiting for hosts shutdown" >> $logFile
get-job | wait-job
$currTime = Get-date -format "yyyy-MM-dd HH:mm:ss"
$currTime + " - Hosts shutdown completed" >> $logFile

$currTime = Get-date -format "yyyy-MM-dd HH:mm:ss"
$currTime + " - Begin shutdown of master host: $masterHost" >> $logFile
$currLogFile = "C:\MultiLink\log\VMware_MasterHost_$masterHost.log"
C:\MultiLink\bin\ShutdownEsxi.ps1 $masterHost $userName $password $waitTime $currLogFile
$currTime = Get-date -format "yyyy-MM-dd HH:mm:ss"
$currTime + " - Master Host shutdown completed" >> $logFile

