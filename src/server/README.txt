Application Name: Wirelessly Integrating Teleoperation Instrument  (WITTI)
Team: Witty
Authors: Brianna Heersink, Brian Smith, Alex Warren
Date: 06 May 2014

 SERVER Files

 dir.php   - Script to view files in directory from http
 index.php - Autogenerates a list of available sequences
 live.php  - Returns latest file prefixed live_frame*
           |- if there is a file newer than GET var ctime
           `- used: live.php?ctime=10000000
