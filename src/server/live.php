<?
// http://127.0.0.1/lidar/live.php?ctime=1397535767
//ini_set('display_errors', 'On');
//error_reporting(E_ALL);
$latest_ctime = 0;
$latest_filename = '';    

$d = dir(".");
while (false !== ($entry = $d->read())) {
  $filepath = "./{$entry}";
  // could do also other checks than just checking whether the entry is a file
  if (is_file($filepath) 
      && filectime($filepath) > $latest_ctime
      && substr($entry, 0, 10) === "live_frame") {
    $latest_ctime = filectime($filepath);
    $latest_filename = $entry;
  }
}

if ($_GET['ctime'] < $latest_ctime){
    $file = "./$latest_filename";
    header('ctime: $latest_ctime');
    header('Content-Length: ' . filesize($file));
    header('Content-Type: application/octet-stream');
    ob_clean();
    flush();
    readfile($file);
    exit;
}else{
    header('Content-Length: 0');
}
?>