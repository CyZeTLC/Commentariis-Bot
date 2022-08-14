<?php
require '../../server.php';
initialize("login");

if (isset($_POST['submit'])) {
    $password = $_POST['password'];
    $username = $_POST['username'];

    $stmt = $pdo->prepare("SELECT * FROM accounts WHERE username = ?");
    $stmt->execute(array($username));
    
    if ($stmt->rowCount() == 1) {
        $row = $stmt->fetch();
        if (password_verify($password, $row['password'])) {
            if (isset($_POST['keep-logged-in'])) {
                $loginToken = md5(uniqid(rand(), true));

                $stmt = $pdo->prepare("SELECT * FROM loginTokens WHERE uid = ?");
                $stmt->execute(array($row['uid']));

                if ($stmt->rowCount() == 0) {
                    $stmt = $pdo->prepare("INSERT INTO loginTokens (uid,token) VALUES (?,?)");
                    $stmt->execute(array($row['uid'], $loginToken));
                } else {
                    $stmt = $pdo->prepare("UPDATE loginTokens SET token = ? WHERE uid = ?");
                    $stmt->execute(array($loginToken, $row['uid']));
                }

                setcookie("login-token", $loginToken, time() + 60 * 60 * 24 * 30, "/");
            }
        }
    }
}

require '../../default/header.php';
?>
<div class="area" style="z-index: 0">
    <ul class="circles">
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
    </ul>
    <div class="min-h-screen bg-gray-100 flex flex-col justify-center py-12 px-6 lg:px-8">
        <div class="sm:mx-auto sm:w-full sm:max-w-md z-10">
            <img class="mx-auto h-12 w-auto" src="https://img.cyzetlc.eu/PTB9GOgooh" alt="Logo" />
            <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">Melde dich an</h2>
            <p class="mt-2 text-center text-sm text-gray-600 max-w">
                Um fortzufahren, musst du dich anmelden!
            </p>
        </div>
        <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md z-10">
            <div class="bg-white py-8 px-6 shadow rounded-lg sm:px-10">
                <?php
                if (isset($_POST['submit'])) {
                    $password = $_POST['password'];
                    $username = $_POST['username'];

                    $stmt = $pdo->prepare("SELECT * FROM accounts WHERE username = ?");
                    $stmt->execute(array($username));

                    if ($stmt->rowCount() == 1) {
                        $row = $stmt->fetch();

                        if (password_verify($password, $row['password'])) {
                            $_SESSION['uid'] = $row['uid'];

                            $stmt = $pdo->prepare("UPDATE accounts SET last_login = ? WHERE uid = ?");
                            $stmt->execute(array(time(), $row['uid']));

                            echo "<div class='info-box-success mb-2'>Du wurdest erfolreich angemeldet!<br>Bitte warte einen Augenblick..</div>";
                            echo "<script>setTimeout(function(){window.location.href='" . "https://intern.cyzetlc.eu" . "'}, 3500);</script>";
                        } else {
                            echo "<div class='info-box-fail mb-2'>Benutzername und Passwort stimmen nicht überein!</div>";
                        }
                    } else {
                        echo "<div class='info-box-fail mb-2'>Benutzername und Passwort stimmen nicht überein!</div>";
                    }
                }
                ?>
                <form class="mb-0 space-y-6" method="POST">
                    <div>
                        <label for="username" class="block text-sm font-medium text-gray-700">Benutzername</label>
                        <div class="mt-1">
                            <input id="username" name="username" type="text" autocomplete="current-username" required class="input" />
                        </div>
                    </div>

                    <div>
                        <label for="password" class="block text-sm font-medium text-gray-700">Passwort</label>
                        <div class="mt-1">
                            <input id="password" name="password" type="password" autocomplete="current-password" required class="input" />
                        </div>
                    </div>

                    <div class="flex items-center">
                        <input id="keep-logged-in" name="keep-logged-in" type="checkbox" class="rounded-sm border-2 border-gray-300 text-indigo-600 focus:ring-indigo-500" />
                        <label for="keep-logged-in" class="ml-2 block text-sm text-gray-900">
                            Angemeldet bleiben.
                        </label>
                    </div>

                    <div>
                        <button type="submit" name="submit" class="button w-full">Anmelden</button>
                    </div>
                </form>
            </div>
        </div>
        <div class="sm:mx-auto sm:w-full sm:max-w-md z-10">
            <p class="mt-2 text-center text-sm text-gray-600 max-w">
                <a href="#" class="px-4">Sprache</a>
                <a href="" class="px-4">Hilfe</a>
                <a href="" class="px-4">Impressum</a>
                <a href="" class="px-4">Datenschutz</a>
            </p>
        </div>
    </div>
</div>
<?php
require '../../default/end.php';
