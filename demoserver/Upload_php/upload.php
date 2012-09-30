<?php
 $file=$_FILES["uploadedfile"];
 $file_name=$_FILES["uploadedfile"]['name'];
 move_uploaded_file($_FILES["uploadedfile"]["tmp_name"],"uploads/" . $_FILES["uploadedfile"]["name"]);
?>
