<html>
<title>LiDAR files</title>

<body>
<h1>LiDAR Folder</h1>
<p>
 <h2>Directores and Files</h2>
<?
$files = scandir(".", 1);

#print_r($files);
foreach($files as $file) {
    if ($file != "." && $file != ".."){
        print "<a href='$file'>$file</a><br/>";
    }
}
?>
</p>
</body>


