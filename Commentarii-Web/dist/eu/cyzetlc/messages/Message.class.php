<?php
namespace eu\cyzetlc\messages;

class Message {
    private $key;
    private $message;

    function __construct($key, $message) {
        $this->key = $key;
        $this->message = $message;
    }

    function get_key() {
        return $this->key;
    }

    function get_message() {
        return $this->message;
    }
}