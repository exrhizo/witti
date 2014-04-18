 SERVER Files

 dir.php   - Script to view files in directory from http
 index.php - Autogenerates a list of available sequences
 live.php  - Returns latest file prefixed live_frame*
           |- if there is a file newer then GET var ctime
           `- used: live.php?ctime=10000000
