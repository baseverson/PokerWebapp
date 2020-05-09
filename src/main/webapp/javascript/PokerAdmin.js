var serverAddress = "sevdev.ddns.net:8076/PokerServer";

function admin_newGame() {
    // Send request to the server to start a new game
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("GET", "http://" + serverAddress + "/rest/PokerAdmin/newRound", true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

function admin_advanceRound() {
    // Send request to the server to start a new game
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("GET", "http://" + serverAddress + "/rest/PokerAdmin/advanceRound", true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}
