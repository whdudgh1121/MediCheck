<?php
$servername = "localhost";
$username = "root";
$password = "0000";
$dbname = "medicheckdb";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$id = $_POST['id'];
$password = $_POST['password'];

// Check if user exists
$sql = "SELECT * FROM userdata WHERE id = ? AND password = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $id, $password);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "fail"]);
}

$stmt->close();
$conn->close();
?>