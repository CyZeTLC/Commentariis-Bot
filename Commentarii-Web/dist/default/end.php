<?php
if (isset($_SESSION['current_page']) && $_SESSION['current_page'] != "login") {
    echo "</div>";
}
?>
</body>
<script src="https://unpkg.com/@themesberg/flowbite@latest/dist/flowbite.bundle.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js" async></script>
<?php
if (isset($_SESSION['current_page']) && $_SESSION['current_page'] != "login") {
?>
    <script type="text/javascript">
        var isToggled = true;

        $(document).ready(function() {
            var boxWidth = $("#navbar").width();

            $("#toggle_navbar").click(function() {
                if (isToggled) {
                    $("#navbar").animate({
                        width: 0
                    });
                    isToggled = false;
                } else {
                    $("#navbar").animate({
                        width: boxWidth
                    });
                    isToggled = true;
                }
            });
        })

        $("#<?php echo $_SESSION['current_page']; ?>_nav").addClass("active");
    </script>
<?php
}
?>
<script>
    var loader = document.getElementById("preloader");
    window.addEventListener("load", function() {
        loader.style.display = "none";
    })
</script>
</body>

</html>