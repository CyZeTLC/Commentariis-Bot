<?php
namespace eu\cyzetlc\post;

class Post
{
    private $numeric_id;
    private $uri_code;
    private $title;
    private $author_id;
    private $timestamp;
    private $img_url;
    private $content;

    function __construct($numeric_id, $uri_code, $title, $author_id, $timestamp, $img_url, $content)
    {
        $this->numeric_id = $numeric_id;
        $this->uri_code = $uri_code;
        $this->title = $title;
        $this->author_id = $author_id;
        $this->timestamp = $timestamp;
        $this->img_url = $img_url;
        $this->content = $content;
    }

    function get_numeric_id()
    {
        return $this->numeric_id;
    }

    function get_uri_code()
    {
        return $this->uri_code;
    }

    function get_title()
    {
        return $this->title;
    }

    function get_author_id()
    {
        return $this->author_id;
    }

    function get_timestamp()
    {
        return $this->timestamp;
    }

    function get_img_url()
    {
        return $this->img_url;
    }

    function get_content()
    {
        return $this->content;
    }
}

