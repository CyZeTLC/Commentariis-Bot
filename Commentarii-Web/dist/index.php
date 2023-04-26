<?php
require './server.php';
initialize("home");

require './default/header.php';
?>
<h1 class="flex text-gray-600 font-semibold text-4xl">Willkommen, <?php echo $_SESSION['discord_data']['username']; ?> <img style="padding-left: 5px; height: 40px" src="https://emojipedia-us.s3.amazonaws.com/source/microsoft-teams/337/partying-face_1f973.png" loading="lazy" /></h1>
<p class="text-neutral-400">Hier findest du die aktuellen Statistiken.</p>
<section class="bg-white rounded-lg shadow p-3 text-black mt-4">
    <?php
    if (!isset($_SESSION['guild_id'])) {
        echo '<h1 class="text-gray-600 font-semibold text-3xl p-2">Wähle deinen Server</h1>';
        echo '<div class="grid lg:grid-cols-4 space-x-reverse space-y-reverse">';
        foreach ($_SESSION['discord_guilds'] as $server) {
            if ($server['owner'] == true || $server['owner'] == 1) {
                $image_url = "https://toppng.com/uploads/preview/discord-logo-png-discord-ico-11562937135cilktsftux.png";

                if ($server['icon'] !== null && $server['icon'] !== "") {
                    $image_url = 'https://cdn.discordapp.com/icons/' . $server['id'] . '/' . $server['icon'] . '.webp?size=240';   
                }

                $stmt = $pdo->prepare('SELECT * FROM settings WHERE  guild_id = ?');
                $stmt->execute(array($server['id']));
                $rs = $stmt->fetch();

                if ($stmt->rowCount() > 0) {
                    echo "<div class='rounded-md bg-neutral-800 mx-2 my-2'>";
                    echo '<a href="https://commentarii.cyzetlc.eu/mange/' . $server['id'] . '/home" class="text-sm hover:bg-blue-600 px-4 py-2 flex place-items-center rounded-md">';
                    echo '<img src="' . $image_url . '" loading="lazy" class="w-12 rounded-full mr-2" />';
                    echo '<p class="text-gray-200">' . $server['name'] . '</p>';
                    echo '</a>';
                    echo "</div>";
                }
            } 
        }
        echo '</div>';
        echo "<hr>";
        echo '<div class="grid lg:grid-cols-4">';

        foreach ($_SESSION['discord_guilds'] as $server) {
            if ($server['owner'] == true || $server['owner'] == 1) {
                $stmt = $pdo->prepare('SELECT * FROM settings WHERE  guild_id = ?');
                $stmt->execute(array($server['id']));
                $rs = $stmt->fetch();

                if ($stmt->rowCount() == 0) {
                    echo "<div class='rounded-md bg-neutral-800 mx-2 my-2'>";
                    echo '<a href="https://discord.com/oauth2/authorize?client_id=1007778519717269516&scope=bot&permissions=8&guild_id=' . $server['id'] . '&redirect_uri=https%3A%2F%2Fcommentarii.cyzetlc.eu%2Frest%2F%3Fjoined%3Dtrue" class="text-sm hover:bg-blue-600 px-4 py-2 flex place-items-center rounded-md">';
                    echo '<h1 class="font-bold text-4xl text-white mb-2 mr-2">+</h1>';
                    echo '<p class="text-gray-200">' . $server['name'] . '</p>';
                    echo '</a>';
                    echo "</div>";
                }
            } 
        }
    
        echo '</div>';
    }
    ?>
</section>
<section class="bg-white rounded-lg shadow p-5 text-black mt-4">
    <h1 class="text-gray-600 font-semibold text-3xl">Aktuelle Session</h1>
    <br>
    <pre><?php print_r($_SESSION); ?></pre>
</section>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script type="text/javascript">
    function createSkeleton(section) {
        var htmlCode = "";

        if (section == "stats") {
            for (let i = 0; i < 5; i++) {
                htmlCode += `<div class="animate-pulse">`;
                htmlCode += `<div class="h-12 mb-2 bg-gray-300 rounded-lg"></div>`;
                htmlCode += `<div class="h-4 bg-gray-300 rounded-lg"></div>`;
                htmlCode += `</div>`;
                if (i < 4) {
                    htmlCode += `<div class="xs:block lg:hidden w-full grid place-items-center"><hr class="w-1/4"></div>`;
                }
            }
        } else if (section == "chart") {
            htmlCode += `<div class="animate-pulse">`;
            htmlCode += `<div class="bg-gray-300 w-full rounded-lg" style="height: 340px;">`;
            htmlCode += `</div>`;
            htmlCode += `</div>`;
        }

        return htmlCode;
    }

    function loadBrowser() {
        let args = ["csrf:<?php echo $_SESSION['csrf']['token']; ?>"];
        generateAjaxRequest("get_most_used_browser", args, function(op) {
            setTimeout(function() {
                $("#browser_tracking_div").html(`<canvas id="browser_tracking"></canvas>`);
                const obj = JSON.parse(op);

                const labelsBarChart = [
                    obj.browsers[0].name,
                    obj.browsers[1].name,
                    obj.browsers[2].name,
                    obj.browsers[3].name,
                ];

                const dataBarChart = {
                    labels: labelsBarChart,
                    datasets: [{
                        label: "Anzahl genutzt",
                        backgroundColor: "hsl(252, 82.9%, 67.8%)",
                        borderColor: "hsl(252, 82.9%, 67.8%)",
                        data: [obj.browsers[0].amount, obj.browsers[1].amount, obj.browsers[2].amount, obj.browsers[3].amount],
                    }, ],
                };

                const configBarChart = {
                    type: "bar",
                    data: dataBarChart,
                    options: {},
                };

                var chartBar = new Chart(
                    document.getElementById("browser_tracking"),
                    configBarChart
                );
            }, 500);
        });
    }

    function loadUser() {
        let args = ["csrf:<?php echo $_SESSION['csrf']['token']; ?>"];
        generateAjaxRequest("get_most_used_cities", args, function(op) {
            setTimeout(function() {
                $("#user_tracking_div").html(`<canvas id="user_tracking"></canvas>`);
                const obj = JSON.parse(op);

                const dataPie = {
                    labels: [obj.cities[0].name, obj.cities[1].name, obj.cities[2].name, obj.cities[3].name, obj.cities[4].name],
                    datasets: [{
                        label: "Stadt",
                        data: [obj.cities[0].amount, obj.cities[1].amount, obj.cities[2].amount, obj.cities[3].amount, obj.cities[4].amount],
                        backgroundColor: [
                            "rgb(133, 105, 241)",
                            "rgb(164, 101, 241)",
                            "rgb(101, 143, 241)",
                        ],
                        hoverOffset: 4,
                    }, ],
                };

                const configPie = {
                    type: "pie",
                    data: dataPie,
                    options: {},
                };

                var chartBar = new Chart(document.getElementById("user_tracking"), configPie);
            }, 500);
        });
    }

    function loadStats() {
        let args = ["csrf:<?php echo $_SESSION['csrf']['token']; ?>"];
        generateAjaxRequest("get_stats", args, function(op) {
            setTimeout(function() {
                $("#stats").html(op);
                runAnimations();
            }, 500);
        });
    }

    $(window).ready(function() {
        $("#stats").html(createSkeleton("stats"));
        $("#user_tracking_div").html(createSkeleton("chart"));
        $("#browser_tracking_div").html(createSkeleton("chart"));

        setTimeout(function() {
            loadStats();
            loadBrowser();
            loadUser();
        }, 750);
    })
</script>
<?php
require './default/end.php';
?>