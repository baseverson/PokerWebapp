//var serverAddress = "sevdev.ddns.net:8076/Poker_alpha";
var fullpath = window.location.pathname;
var path = fullpath.split("/");
var serverAddress = window.location.host + "/" + path[1] + "/";
var serverPath = "rest/PlayerAdmin"
