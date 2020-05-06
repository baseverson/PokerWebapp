function admin_newGame() {
    // Send request to the server to start a new game
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("GET", "http://192.168.86.16:8080/PokerServer/rest/PokerTest/newRound", true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}