% The following is a list of available LiDAR data on the server.
% format:
% file_name number_of_frames
% There will be file_name_0000.bin file_name_0001.bin etc..
<?
//ini_set('display_errors', 'On');
//error_reporting(E_ALL);

$sequences = Array();
$files = scandir(".", 1);

foreach($files as $file) {
    if (substr($file, -4) === ".bin"){
        $seq = substr($file, 0, -9);
        //print "> $seq\n";
        if (array_key_exists($seq, $sequences)){
            $sequences[$seq] ++;
        }else{
            $sequences[$seq] = 1;
        }
    }
}
foreach ($sequences as $seq => $count) {
    print "$seq $count\n";
}
?>