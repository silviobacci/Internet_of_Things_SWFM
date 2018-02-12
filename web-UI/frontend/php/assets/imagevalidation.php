<?php
// -------------------------------------
// IMAGEVALIDATION.PHP
// contains php script to validate
// image uploaded to the server
// -------------------------------------

// Check file size
function check_image_size($file) {
    if ($file["size"] > 1500000)
        return false;
    return true;
}

// Check if file already exists
function check_image_not_exists($target_file) {
    if (file_exists($target_file))
        return false;
    return true;
}

// Check if image file is a actual image or fake image
function check_real_image($file) {
    $check = getimagesize($file["tmp_name"]);
    if($check !== false)
        return true;
    return false;
}

function image_get_extension($file) {
    return strtolower(pathinfo($file["name"], PATHINFO_EXTENSION));
}

// Allow certain file formats
function check_image_extension($file) {
    $extension = image_get_extension($file);

    if(
        $extension != "jpg"  && 
        $extension != "png"  && 
        $extension != "jpeg" && 
        $extension != "gif")
        return false;

    return true;
}

// Try to upload image
function upload_image($file, $target_file) {
    if (move_uploaded_file($file["tmp_name"], $target_file))
        return true;
    else
        return false;
}


?>