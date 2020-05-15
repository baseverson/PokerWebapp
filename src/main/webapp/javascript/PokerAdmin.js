var serverAddress = "sevdev.ddns.net:8076/Poker_alpha";

/**
  * Admin function - reset and start a new game.
  */
function admin_newGame() {
    // Send request to the server to start a new game
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("GET", "http://" + serverAddress + "/rest/Admin/newRound", true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/**
  * Admin function - move the game to the next round.
  */
function admin_advanceRound() {
    // Send request to the server to start a new game
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("GET", "http://" + serverAddress + "/rest/Admin/advanceRound", true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/**
  * Admin function - move the action to the next player.
  */
function admin_advanceAction() {
    // Send request to the server to move the action to the next player
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("GET", "http://" + serverAddress + "/rest/Admin/advanceAction", true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}
