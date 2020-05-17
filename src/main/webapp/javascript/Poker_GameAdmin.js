var serverAddress = "sevdev.ddns.net:8076/Poker_alpha";
var serverPath = "rest/GameAdmin"
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

    //xhttp.open("GET", "http://" + serverAddress + "/" + serverPath + "/newRound", true);
    xhttp.open("GET", "http://" + serverAddress + "/rest/GameAdmin/newRound", true);
    xhttp.setRequestHeader("Content-type", "text/plain");
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

    xhttp.open("GET", "http://" + serverAddress + "/rest/GameAdmin/advanceRound", true);
    xhttp.setRequestHeader("Content-type", "text/plain");
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

    xhttp.open("GET", "http://" + serverAddress + "/rest/GameAdmin/advanceAction", true);
    xhttp.setRequestHeader("Content-type", "text/plain");
    xhttp.send();
}

/**
  * Admin function - set the game winner
  */
function admin_setWinner() {
    // Get the seat number to set the winner to from the input field.
    var winningSeatNum = document.getElementById("winningSeatNum").value;

    // Send request to the server to move the action to the next player
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/GameAdmin/setWinningSeatNum?seatNum=" + winningSeatNum, true);
    xhttp.setRequestHeader("Content-type", "text/plain");
    xhttp.send();
}
