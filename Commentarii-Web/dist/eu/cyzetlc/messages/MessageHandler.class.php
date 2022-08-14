<?php
namespace eu\cyzetlc\messages;
require_once '/var/www/zwoote/intern/eu/cyzetlc/messages/Message.class.php';

class MessageHandler {
    public static function get_all_messages() {
        global $pdo;
        $arr = array();
        $stmt = $pdo->prepare("SELECT `key`,`message` FROM `messages` ORDER BY numeric_id DESC");
        $stmt->execute();

        while ($rs = $stmt->fetch()) {
            array_push($arr, new Message($rs['key'], $rs['message']));
        }
        return $arr;
    }

    public static function get_message($key, $args) {
        global $currentDevice;
        $language_key = strtolower(explode(", ", $currentDevice->get_user_location())[2]);
        return self::get_static_message($language_key . "." . $key, $args);
    }

    public static function get_static_message($key, $args) {
        global $pdo;

        $stmt = $pdo->prepare("SELECT message FROM messages WHERE `key` = ?");
        $stmt->execute(array($key));
        $message = "";

        if ($stmt->rowCount() == 0) {
            $message = "<span class='text-red-600'>Not found: $key</span>";
        } else {
            $rs = $stmt->fetch();
            $db_message = $rs['message'];

            for ($i = 0; $i < count($args); $i++) {
                $db_message = str_replace("{" . $i . "}", $args[$i], $db_message);
            }

            $message = $db_message;
        }
        return $message;
    }
}