<?php
$url = "https://discord.com/api/v6/oauth2/token";

$data = array(
    'client_id' => "925416749925552179",
    'client_secret' => "G0B3r9-13Q2fBHt-gIbUgBKSUQNF_Bk8",
    'grant_type' => 'authorization_code',
    'redirect_uri' => "https://cyzetlc.eu/rest/discord/index.php?value=success",
    'code' => $_REQUEST['code'],
    'scopr' => 'identoty guilds.join'
);
$header = array(
    'Content-Type' => "application/x-www-form-urlencoded"
);
$ch = curl_init($url);
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
$respone = curl_exec($ch);
curl_close($ch);

$rsp = json_decode($respone, 1);
$access_token = $rsp['access_token'];

$stmt = $pdo->prepare("SELECT * FROM discordAccessTokens WHERE uid = ?");
$stmt->execute(array($_SESSION['uid']));

if ($stmt->rowCount() != 0) {
    $stmt = $pdo->prepare("UPDATE discordAccessTokens SET access_token = ?");
    $stmt->execute(array($access_token));
} else {
    $stmt = $pdo->prepare("INSERT INTO discordAccessTokens (uid,access_token) VALUES (?,?)");
    $stmt->execute(array($_SESSION['uid'], $access_token));
}

$_SESSION['discord_access_token'] = $access_token;
$url = "https://cyzetlc.eu/rest/discord/?ac=getuser&uid=" . $_SESSION['uid'];
$rsp = json_decode(file_get_contents($url), 1);
$_SESSION['img_url'] = 'https://cdn.discordapp.com/avatars/' . $rsp['id'] . '/' . $rsp['avatar'] . '.png';
$_SESSION['discord'] = $rsp;
header("location: https://cyzetlc.eu/account/?type=success");
