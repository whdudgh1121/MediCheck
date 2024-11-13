<?php
$servername = "localhost";
$username = "your_db_username";
$password = "your_db_password";
$dbname = "your_db_name";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$id = $_POST['id'];
$password = $_POST['password'];

// Check if user exists
$sql = "SELECT * FROM users WHERE id = ? AND password = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $id, $password);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo "success";
} else {
    echo "fail";
}

$stmt->close();
$conn->close();
?>