<?php
namespace eu\cyzetlc\utils;

class UserDevice
{
    public $os;
    public $browser;
    public $sessionId;
    public $device;
    public $userLocation;

    function __construct($sessionId, $os, $browser, $device, $userLocation)
    {
        $this->sessionId = $sessionId;
        $this->browser = $browser;
        $this->os = $os;
        $this->device = $device;
        $this->userLocation = $userLocation;
    }

    function display_banner()
    {
?>
        <div class="flex">
    <?php
        if ($this->get_device() == "Computer")
        {
?>
        <div style="width: 64px;" class="p-2 bg-white rounded-lg grid place-items-center"><i class="fa-solid fa-desktop fa-2x"></i></div>
    <?php
        }
        else if ($this->get_device() == "Mobile")
        {
?>
        <svg class="p-2 bg-white rounded-lg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" id="Capa_1" x="0px" y="0px" width="64" height="64" viewBox="0 0 27.442 27.442" style="enable-background:new 0 0 27.442 27.442" xml:space="preserve"><g><path d="M19.494,0H7.948C6.843,0,5.951,0.896,5.951,1.999v23.446c0,1.102,0.892,1.997,1.997,1.997h11.546   c1.103,0,1.997-0.895,1.997-1.997V1.999C21.491,0.896,20.597,0,19.494,0z M10.872,1.214h5.7c0.144,0,0.261,0.215,0.261,0.481   s-0.117,0.482-0.261,0.482h-5.7c-0.145,0-0.26-0.216-0.26-0.482C10.612,1.429,10.727,1.214,10.872,1.214z M13.722,25.469   c-0.703,0-1.275-0.572-1.275-1.276s0.572-1.274,1.275-1.274c0.701,0,1.273,0.57,1.273,1.274S14.423,25.469,13.722,25.469z    M19.995,21.1H7.448V3.373h12.547V21.1z"/><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g></svg>
    <?php
        }
        else if ($this->get_device() == "Tablet")
        {
?>
        <svg class="p-2 bg-white rounded-lg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" id="Layer_1" x="0px" y="0px" width="64" height="64" viewBox="0 0 91.31 122.88" style="enable-background:new 0 0 91.31 122.88" xml:space="preserve"><style type="text/css">.st0{fill-rule:evenodd;clip-rule:evenodd;}</style><g><path class="st0" d="M8.99,0h73.34c4.94,0,8.98,4.05,8.98,8.99V113.9c0,4.94-4.04,8.98-8.98,8.98l-73.34,0 c-4.94,0-8.99-4.04-8.99-8.98V8.99C0,4.04,4.04,0,8.99,0L8.99,0L8.99,0z M46.23,101.28c4.05,0,7.32,3.28,7.32,7.33 c0,4.05-3.28,7.33-7.32,7.33c-4.05,0-7.33-3.28-7.33-7.33C38.91,104.56,42.19,101.28,46.23,101.28L46.23,101.28z M12.53,10.34 h66.25c0.74,0,1.34,0.6,1.34,1.34v84.41c0,0.73-0.6,1.34-1.34,1.34H12.53c-0.73,0-1.34-0.6-1.34-1.34V11.68 C11.2,10.94,11.8,10.34,12.53,10.34L12.53,10.34z" width="64" height="64"/></g></svg>
        <?php
        }
        else
        {
?>
        <svg class="p-2 bg-gray-400 rounded-lg" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" width="93.936px" height="93.936px" viewBox="0 0 93.936 93.936" style="enable-background:new 0 0 93.936 93.936" xml:space="preserve"><g><path d="M80.179,13.758c-18.342-18.342-48.08-18.342-66.422,0c-18.342,18.341-18.342,48.08,0,66.421
c18.342,18.342,48.08,18.342,66.422,0C98.521,61.837,98.521,32.099,80.179,13.758z M44.144,83.117
c-4.057,0-7.001-3.071-7.001-7.305c0-4.291,2.987-7.404,7.102-7.404c4.123,0,7.001,3.044,7.001,7.404
C51.246,80.113,48.326,83.117,44.144,83.117z M54.73,44.921c-4.15,4.905-5.796,9.117-5.503,14.088l0.097,2.495
c0.011,0.062,0.017,0.125,0.017,0.188c0,0.58-0.47,1.051-1.05,1.051c-0.004-0.001-0.008-0.001-0.012,0h-7.867
c-0.549,0-1.005-0.423-1.047-0.97l-0.202-2.623c-0.676-6.082,1.508-12.218,6.494-18.202c4.319-5.087,6.816-8.865,6.816-13.145
c0-4.829-3.036-7.536-8.548-7.624c-3.403,0-7.242,1.171-9.534,2.913c-0.264,0.201-0.607,0.264-0.925,0.173
s-0.575-0.327-0.693-0.636l-2.42-6.354c-0.169-0.442-0.02-0.943,0.364-1.224c3.538-2.573,9.441-4.235,15.041-4.235
c12.36,0,17.894,7.975,17.894,15.877C63.652,33.765,59.785,38.919,54.73,44.921z" width="64" height="64"/></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g><g></g></svg>
        <?php
        }
?>
    <div class="ml-2 justify-start">
        <strong><?php echo $this->get_os(); ?></strong>
        <?php 
        echo "· " . $this->get_browser(); 
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }

        if (session_id() == $this->get_session_id()) {
            ?>
            <span class="p-1 bg-red-400 uppercase text-white font-semibold rounded-lg">Dieses Gerät</span>
            <?php
        }
        ?>
        <p class="text-sm">
            <?php echo $this->get_device(); ?>
        </p>
        <p class="text-sm">
            <?php echo $this->get_user_location(); ?>
        </p>
    </div>
</div>
<?php
    }

    function get_user_location()
    {
        return $this->userLocation;
    }

    function get_device()
    {
        return $this->device;
    }

    function get_session_id()
    {
        return $this->sessionId;
    }

    function get_browser()
    {
        return $this->browser;
    }

    function get_os()
    {
        return $this->os;
    }
}

