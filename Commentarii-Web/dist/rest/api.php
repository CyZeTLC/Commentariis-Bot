<?php
require "../server.php";
ini_set('display_errors', 0);
$json = json_decode("{}", true);

if (isset($_POST['action'])) {
    $action = $_POST['action'];
    $given_csrf = $_POST['csrf'];
    $current_csrf = $_SESSION['csrf']['token'];

    /* CSRF Check */
    if ($current_csrf == $given_csrf) {
        if ($_SESSION['csrf']['time'] + (1000 * 60 * 60 * 24) > time()) {
            if ($action == "get_all_posts") {
                $filter = "";
                if (isset($_POST['filter'])) {
                    $filter = $_POST['filter'];
                }

                if (isset($_POST['limit'])) {
                    $posts = $postHandler->get_limited_posts($filter, intval($_POST['limit']));
                } else {
                    $posts = $postHandler->get_all_posts($filter);
                }

                foreach ($posts as $post) {
                    $publish_date = date('d.m.Y', $post->get_timestamp() / 1000);
?>
                    <div class="shadow rounded-sm border border-neutral-300 border-t-2 border-t-blue-400">
                        <div class="bg-gray-600 w-full h-64" style="background: url(<?php echo $post->get_img_url(); ?>); background-repeat: no-repeat; background-position: center center; background-size: cover"></div>
                        <div class="p-2">
                            <p class="text-neutral-400 text-sm"><?php echo $publish_date; ?></p>
                            <a href="https://intern.cyzetlc.eu/posts/?edit=<?php echo $post->get_uri_code(); ?>" class="text-neutral-700 font-bold text-2xl"><?php echo $post->get_title(); ?></a>
                            <p class="text-neutral-900 pt-3">
                                <?php echo htmlToPlainText(substr($post->get_content(), 0, 295)); ?>
                            </p>
                        </div>
                    </div>
                <?php
                }
            } else if ($action == "get_stats") {
                ?>
                <div>
                    <h1 class="countup text-neutral-700 text-4xl"><?php echo count($devices); ?></h1>
                    <p class="text-neutral-500 text-2xl">Besucher</p>
                </div>
                <div class="xs:block lg:hidden w-full grid place-items-center">
                    <hr class="w-1/4">
                </div>
                <div>
                    <h1 class="countup text-neutral-700 text-4xl">
                        <?php
                        $now = time();
                        $your_date = strtotime("2022-05-29");
                        $datediff = $now - $your_date;
                        echo round(count($devices) / round($datediff / (60 * 60 * 24)));
                        ?>
                    </h1>
                    <p class="text-neutral-500 text-2xl">Besucher / Tag</p>
                </div>
                <div class="xs:block lg:hidden w-full grid place-items-center">
                    <hr class="w-1/4">
                </div>
                <div>
                    <h1 class="countup text-neutral-700 text-4xl">
                        <?php
                        echo round($datediff / (60 * 60 * 24));
                        ?>
                    </h1>
                    <p class="text-neutral-500 text-2xl">Tage online</p>
                </div>
                <div class="xs:block lg:hidden w-full grid place-items-center">
                    <hr class="w-1/4">
                </div>
                <div>
                    <h1 class="countup text-neutral-700 text-4xl"><?php echo $postHandler->get_all_posts_count(); ?></h1>
                    <p class="text-neutral-500 text-2xl">Beiträge</p>
                </div>
                <div class="xs:block lg:hidden w-full grid place-items-center">
                    <hr class="w-1/4">
                </div>
                <div>
                    <h1 class="countup text-neutral-700 text-4xl">11</h1>
                    <p class="text-neutral-500 text-2xl">Königskompanie</p>
                </div>
                <?php
            } else if ($action == "get_warnings") {
                $stmt = $pdo->prepare("SELECT os FROM user_devices WHERE os = 'Unknown OS Platform' OR browser = 'Unknown'");
                $stmt->execute();

                if ($stmt->rowCount() > 0) {
                ?>
                    <div class="bg-red-400 text-red-700 font-semibold text-sm p-4 mb-1 rounded-lg">
                        Es wurden <?php echo $stmt->rowCount(); ?> Botanfragen gefunden!
                    </div>
                <?php
                }

                $stmt = $pdo->prepare("SELECT location FROM user_devices WHERE location NOT LIKE '%, DE%'");
                $stmt->execute();

                if ($stmt->rowCount() / count($devices) > 0.6) {
                ?>
                    <div class="bg-red-400 text-red-700 font-semibold text-sm p-4 mb-1 rounded-lg">
                        Ungewöhnlich viele ausländische Anfrangen gefunden! Kanpp <?php echo round($stmt->rowCount() / count($devices) * 100); ?>%
                    </div>
<?php
                }
            } else if ($action == "get_most_used_browser") {
                $stmt = $pdo->prepare("SELECT COUNT(*) AS amount,browser FROM user_devices GROUP BY browser ORDER BY amount DESC LIMIT 4");
                $stmt->execute();
                $output = json_decode("{}", true);
                $i = 0;

                while ($rs = $stmt->fetch()) {
                    $output['browsers'][$i]['name'] = $rs['browser'];
                    $output['browsers'][$i]['amount'] = $rs['amount'];
                    $i++;
                }
                echo json_encode($output);
            } else if ($action == "get_most_used_cities") {
                $stmt = $pdo->prepare("SELECT COUNT(*) AS amount,location FROM user_devices GROUP BY location ORDER BY amount DESC LIMIT 4");
                $stmt->execute();
                $output = json_decode("{}", true);
                $fullAmount = 0;
                $i = 0;

                while ($rs = $stmt->fetch()) {
                    $output['cities'][$i]['name'] = explode(", ", $rs['location'])[0];
                    $output['cities'][$i]['amount'] = $rs['amount'];

                    $fullAmount += $rs['amount'];
                    $i++;
                }

                $output['cities'][$i]['name'] = "Andere";
                $output['cities'][$i]['amount'] = count($devices) - $fullAmount;
                echo json_encode($output);
            } else if ($action == "get_all_logins") {
                $output = json_decode("{}", true);

                $i = 0;
                foreach ($devices as $device) {
                    $output["devices"][$i]['numeric_id'] = "";
                    echo "'numeric_id': '" . $i . "',";
                    echo "'device': '" . $device->get_device() . "',";
                    echo "'os': '" . $device->get_os() . "',";
                    echo "'location': '" . $device->get_user_location() . "'";
                    $i++;
                }

                echo json_encode($output);
            } else if ($action == "change_message") {
                if ($_POST['type'] == "key") {
                    $stmt = $pdo->prepare("UPDATE `messages` SET `key` = ? WHERE `key` = ?");
                    $stmt->execute(array($_POST['content'], $_POST['id']));

                    if ($stmt->rowCount() > 0) {
                        echo "success";
                    }
                } else {
                    $stmt = $pdo->prepare("UPDATE `messages` SET `message` = ? WHERE `key` = ?");
                    $stmt->execute(array($_POST['content'], $_POST['id']));

                    if ($stmt->rowCount() > 0) {
                        echo "success";
                    }
                }
            } else if ($action == "delete_message") {
                $stmt = $pdo->prepare("DELETE FROM `messages` WHERE `key` = ?");
                $stmt->execute(array($_POST['id']));

                if ($stmt->rowCount() > 0) {
                    echo "success";
                }
            } else if ($action == "add_message") {
                $key = $_POST['key'];
                $content = $_POST['content'];
                $stmt = $pdo->prepare("SELECT `key` FROM `messages` WHERE `key` = ?");
                $stmt->execute(array($key));

                if ($stmt->rowCount() > 0) {
                    $stmt = $pdo->prepare("UPDATE `messages` SET `message` = ? WHERE `key` = ?");
                    $stmt->execute(array($content, $key));

                    if ($stmt->rowCount() > 0) {
                        echo "success";
                    }
                } else {
                    $stmt = $pdo->prepare("INSERT INTO `messages` (`key`, `message`) VALUES (?,?)");
                    $stmt->execute(array($key, $content));

                    if ($stmt->rowCount() > 0) {
                        echo "success_new";
                    }
                }
            } else if ($action == "edit_post") {
                $stmt = $pdo->prepare("UPDATE posts SET content = ? WHERE uri_code = ?");
                $stmt->execute(array($_POST['content'], $_POST['key']));

                if ($stmt->rowCount() > 0) {
                    echo "success";
                }
            }
        } else {
            $json['error'] = 403;
            $json['message'] = "CSRF-Token expired!";
        }
    } else {
        $json['error'] = 403;
        $json['message'] = "Invaild CSRF-Token!";
    }
} else {
    $json['error'] = 501;
    $json['message'] = "Invaild request!";
}

if (count($json) > 0) {
    echo json_encode($json);
}
