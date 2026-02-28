<?php
$host = "localhost";
$username = "root";
$password = ""; // Default for XAMPP is empty
$dbname = "MedicCenterDB";

// Create connection
$conn = new mysqli($host, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>