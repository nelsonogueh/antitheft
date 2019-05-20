<?php
/**
 * User: Nelson Ogueh
 * Date: 14/05/2019
 * Time: 10:30 AM
 * Company: Dulceprime Inc.
 * Company address: http://dulceprime.com
 */
 
//Receive JSON
$JSON_Received = $_REQUEST["json_brought"];

$numbersPassed = array();
$emailsPassed = array();
$gpsCoordinate_X = '';
$gpsCoordinate_X = $_REQUEST["gpsCoordinateX"];
$gpsCoordinate_Y = '';
$gpsCoordinate_Y = $_REQUEST["gpsCoordinateY"];
//Decode Json
$obj = json_decode($JSON_Received, true);
foreach ($obj['phoneNumbers'] as $key => $value) {
    $numbersPassed[] = $value;
}
foreach ($obj['emailAddress'] as $key => $value) {
    $emailsPassed[] = $value;
}

$arraySize = count($numbersPassed);

$phone = "";

for ($i = 0; $i < $arraySize; $i++) {
    $phone .= replace_first_zero_in_contact($numbersPassed[$i]) . ",";
}
$phone = rtrim($phone, ',');


//echo 'X coord: ';
//echo $gpsCoordinate_X;
//echo 'Y coord: ';
//echo $gpsCoordinate_Y;
$get_data = callRESTAPI('GET', 'https://account.kudisms.net/api/?username=azemobordaniel@gmail.com&password=gbogo251coma153&message=It seems the phone of your friend has been compromised! The current GPS location of the device is: ' . $gpsCoordinate_X . ', ' . $gpsCoordinate_Y . '&sender=ANTI THEFT&mobiles=' . $phone, false);

$response = json_decode($get_data, true);
$success = $response['status'];
$errors = $response['errors'];

//echo $success;

//print_r($response);



// SEND MAIL
$email = "";

for ($i = 0; $i < $arraySize; $i++) {
    $email .= $emailsPassed[$i] . ",";
}
$email = rtrim($email, ',');


$to      = $email;
$subject = 'Anti theft report';

require_once "Mail.php"; // PEAR Mail package
require_once ('Mail/mime.php'); // PEAR Mail_Mime packge

$from = "support@dulceprime.com"; //enter your email address
$headers = array ('From' => $from,'To' => $to, 'Subject' => $subject);
$text = "It seems the phone of your friend has been compromised! The current GPS location of the device is: ' . $gpsCoordinate_X . ', ' . $gpsCoordinate_Y";
//$text = ''; // text versions of email.
$html = "<html><body>It seems the phone of your friend has been compromised! The current GPS location of the device is: ' . $gpsCoordinate_X . ', ' . $gpsCoordinate_Y</body></html>"; // html versions of email.

$crlf = "\n";

$mime = new Mail_mime($crlf);
$mime->setTXTBody($text);
$mime->setHTMLBody($html);

//do not ever try to call these lines in reverse order
$body = $mime->get();
$headers = $mime->headers($headers);

$host = "localhost"; // all scripts must use localhost
$username = "edostat3"; //  your email address (same as webmail username)
$password = "princessdaniella"; // your password (same as webmail password)

$smtp = Mail::factory('smtp', array ('host' => $host, 'auth' => true,
    'username' => $username,'password' => $password));

$mail = $smtp->send($to, $headers, $body);


echo $to;
echo "<br>";
echo "<br>";




/*
$smtp = Mail::factory('smtp', array(
            'host' => 'ssl://smtp.gmail.com',
            'port' => '465',
            'auth' => true,
            'username' => 'johndoe@gmail.com',
            'password' => 'passwordxxx'
        ));

    $mail = $smtp->send($to, $headers, $body);

    if (PEAR::isError($mail)) {
        echo('<p>' . $mail->getMessage() . '</p>');
    } else {
        echo('<p>Message successfully sent!</p>');
    }  */



if (PEAR::isError($mail)) {
    echo("<p>" . $mail->getMessage() . "</p>");
}
else {
    echo("<p>Email Message successfully sent!</p>");
}

//
//echo $success;
////echo "failure: "+$errors;


// Replace the first zero of a phone number with nothing
function replace_first_zero_in_contact($phone_number)
{
    $number = $phone_number;
// Remove the spaces.
    $number = str_replace(' ', '', $number);
// Grab the first number.
    $first_number = substr($number, 0, 1);
    if ($first_number == 0) {
        // Check if the first number is 0.
        // Get rid of the first number.
        $number = substr($number, 1, 999);
    }

// Remove the + sign.
    $number = str_replace('+', '', $number);
    $number = "234" . $number;
    return $number;
}


function callRESTAPI($method, $url, $data)
{
    $curl = curl_init();

    switch ($method) {
        case "POST":
            curl_setopt($curl, CURLOPT_POST, 1);
            if ($data)
                curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
            break;
        case "PUT":
            curl_setopt($curl, CURLOPT_CUSTOMREQUEST, "PUT");
            if ($data)
                curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
            break;
        default:
            if ($data)
                $url = sprintf("%s?%s", $url, http_build_query($data));
    }

    // OPTIONS:
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_HTTPHEADER, array(
        'APIKEY: 111111111111111111111',
        'Content-Type: application/json',
    ));
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($curl, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);

    // EXECUTE:
    $result = curl_exec($curl);
    if (!$result) {
        die("Connection Failure");
    }
    curl_close($curl);
    return $result;
}

