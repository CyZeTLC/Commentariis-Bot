<?php
namespace eu\cyzetlc\post;
require_once '/var/www/ms.cyzetlc.eu/eu/cyzetlc/post/Post.class.php';

class PostHandler {
	public static function get_post($uri_code) {
		global $pdo;
		$stmt = $pdo->prepare("SELECT * FROM posts WHERE uri_code = ?");
		$stmt->execute(array($uri_code));
		$rs = $stmt->fetch();

		if ($stmt->rowCount() == 0) {
			return null;
		} else {
			return new Post($rs['numeric_id'], $uri_code, $rs['title'], $rs['author_id'], $rs['timestamp'], $rs['image_link'], $rs['content']);
		}
	}

	public static function get_all_posts($name) {
		global $pdo;

		$qry = "SELECT uri_code FROM posts";
		if ($name != "" && $name != null) {
			$qry .= " WHERE title LIKE \"%$name%\"";
		}

		$stmt = $pdo->prepare($qry . " ORDER BY timestamp DESC");
		$stmt->execute();

		$posts = array();
		while ($rs = $stmt->fetch()) {
			array_push($posts, self::get_post($rs['uri_code']));
		}
		return $posts;
	}

	public static function get_last_posts($amount) {
		global $pdo;

		$qry = "SELECT uri_code FROM posts ORDER BY timestamp DESC LIMIT " . $amount;
		$stmt = $pdo->prepare($qry);
		$stmt->execute();

		$posts = array();
		while ($rs = $stmt->fetch()) {
			array_push($posts, self::get_post($rs['uri_code']));
		}
		return $posts;
	}

	public static function get_all_posts_count() {
		global $pdo;
		$qry = "SELECT uri_code FROM posts";
		$stmt = $pdo->prepare($qry);
		$stmt->execute();
		return $stmt->rowCount();
	}
}