<?php
/* Imports */
require_once '/var/www/zwoote/intern/eu/cyzetlc/account/Account.class.php';
require_once '/var/www/zwoote/intern/eu/cyzetlc/utils/UserInfo.class.php';
require_once '/var/www/zwoote/intern/eu/cyzetlc/utils/UserDevice.class.php';
require_once '/var/www/zwoote/intern/eu/cyzetlc/post/PostHandler.class.php';
require_once '/var/www/zwoote/intern/eu/cyzetlc/messages/MessageHandler.class.php';

require 'config.inc.php';

use eu\cyzetlc\account\Account;
use eu\cyzetlc\utils\UserInfo;
use eu\cyzetlc\utils\UserDevice;
use eu\cyzetlc\post\PostHandler;
use eu\cyzetlc\messages\MessageHandler;

use PDO;
use DateTime;

/* Variables */
$debugMode = false;
$account = new Account();
$userInfo = new UserInfo();
$dateTime = new DateTime();
$postHandler = new PostHandler();
$messageHandler = new MessageHandler();

/* Errors */
ini_set('display_errors', 1);
ini_set("log_errors", 1);
ini_set('display_startup_errors', 0);
error_reporting(E_ALL);

/* Security Headers */
header('X-XSS-Protection: 1; mode=block');
header('X-Content-Type-Options: nosniff');
header('X-Frame-Options: SAMEORIGIN');
header('Strict-Transport-Security: max-age=17280000');
header('Referrer-Policy: no-referrer');
header('Feature-Policy: camera "none"; microphone "none"; geolocation "none"; payment "none";');
header('permissions-policy: camera=(), microphone=(), geolocation=()');

//MySQL
$dsn = 'mysql:host=' . $mysqlCredentials['host'] . ';dbname=' . $mysqlCredentials['schema'];
$pdo = new PDO($dsn, $mysqlCredentials['user'], $mysqlCredentials['passwd']);
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); /* Enable exceptions on errors */

/* Session */
session_start();
if (!isset($_SESSION['created'])) {
    $_SESSION['created'] = time();
}

$_SESSION['last_action'] = time();

/* Debug Messages */
if ($debugMode) {
    var_dump($account);
    var_dump($dateTime);
}

/* Pagination */
function initialize($page) {
    $_SESSION['last_page_load'] = time();
    $_SESSION['current_page'] = $page;

    generate_csrf();
}

/* CSRF Token */
function generate_csrf()
{
    $token = md5(uniqid(rand(), true));
    $_SESSION['csrf']['token'] = $token;
    $_SESSION['csrf']['time'] = time();
}

/* User Device */
$devices = array();
$userId = "000-000-000-000";

$stmt = $pdo->prepare("SELECT session_id,user_id FROM user_devices WHERE user_id = ? AND session_id = ?");
$stmt->execute(array($userId, session_id()));

$stmt = $pdo->prepare("SELECT * FROM user_devices WHERE user_id = ?");
$stmt->execute(array($userId));

while ($rs = $stmt->fetch(PDO::FETCH_ASSOC)) {
    $device = new UserDevice($rs['session_id'], $rs['os'], $rs['browser'], $rs['device'], $rs['location']);
    array_push($devices, $device);
}

/* Login */
if (isset($_GET['logout'])) {
    setcookie("login-token", "", time() - 1, "/");
    session_destroy();
}

if (isset($_COOKIE['login-token']) && !isset($_SESSION['uid'])) {
    $stmt = $pdo->prepare("SELECT * FROM loginTokens WHERE token = ?");
    $stmt->execute(array($_COOKIE['login-token']));

    if ($stmt->rowCount() == 1) {
        $row = $stmt->fetch();
        $_SESSION['uid'] = $row['uid'];
    } else {
        if (!$contains) {
            header("location: " . $loginUrl);
        }
    }
} else if (!isset($_SESSION['uid'])) {
    if (strpos($_SERVER['REQUEST_URI'], "login") == FALSE) {
        header("location: https://commentarii.cyzetlc.eu/login/");
    }
}

/**
 * Functions
 */
function htmlToPlainText($str)
{
    $str = str_replace('&nbsp;', ' ', $str);
    $str = html_entity_decode($str, ENT_QUOTES | ENT_COMPAT, 'UTF-8');
    $str = html_entity_decode($str, ENT_HTML5, 'UTF-8');
    $str = html_entity_decode($str);
    $str = htmlspecialchars_decode($str);
    $str = strip_tags($str);

    return $str;
}
