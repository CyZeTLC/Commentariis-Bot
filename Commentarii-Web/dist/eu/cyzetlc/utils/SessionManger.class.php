<?php
namespace eu\cyzetlc\utils;

class SessionManager {
    public static function get_session_id() {
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }
        return session_id();
    }
}