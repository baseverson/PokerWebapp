var tableInfo = null;
var ws = null;
var serverAddress = "192.168.86.16:8080/PokerServer";
var wsAddress = "ws://" + serverAddress + "/PokerWebSocket";

/**********************************************************************************************************
 * User management functions
 **********************************************************************************************************/

/*
 * Logs in a new user
 */
function login() {
    // TODO: Implement real user login and authentication

    // For now, just store the player name in a cookie
    var playerName = document.getElementById("newPlayerName").value;
    if (window.confirm("Change user to \"" + playerName + "\"?")) {

        // Unregister the session with this username
        if(getPlayerName() != "") {
            ws.send("UnregisterSession:" + getPlayerName());
            console.log("Web Socket connection registration removed for " + getPlayerName());
        }

        document.cookie = "PlayerName=" + playerName;
        updateDisplayedPlayerName(playerName);
        ws.send("RegisterSession:" + getPlayerName());
        console.log("Web Socket connection registered to " + getPlayerName());
    }
    document.getElementById("newPlayerName").innerHTML="";
}

/*
 * Log out the current user
 */
function logout() {
    // TODO: Implement real user logout

    // For now, just set the playerName cookie to empty string
    if (window.confirm("Log out \"" + getPlayerName() + "\"?")) {
        // Unregister the session with this username
        ws.send("UnregisterSession:" + getPlayerName());
        console.log("Web Socket connection registration removed for " + getPlayerName());

        document.cookie = "PlayerName=";
        updateDisplayedPlayerName();
    }
}

/*
 * Reads the player name from the cookie.
 */
function getPlayerName() {
    var playerName = getCookie("PlayerName");
    console.log("Reading current player name: " + playerName);
    return playerName;
}

/*
 * Updates the display to reflect the player name passed in.
 */
function updateDisplayedPlayerName() {
    var playerName = getPlayerName();
    document.getElementById("PlayerName").innerHTML = "<b>"+playerName+"</b>";
    document.getElementById("PlayerName").style.color = "blue";
}

/*
 * Notify the server that the player has requested to take a seat.
 *
 * Returns: none
 */
function sitDown(seatNum) {
    // Only allow a player to sit down if they have a playerName defined
    if (getPlayerName() == "") {
        console.log("Cannot sit at table. Player name not defined.")
        window.alert("You must set your player name before you can sit at the table.");
        return;
    }

    // Send request to the server for a player to sit at a seat
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/PokerTable/sitDown?playerName=" + getPlayerName() + "&seatNum=" + seatNum, true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();

    // Update the page for the new table state.
    getTableInfo();
}

/*
 * Notify the server that the player has requested to leave the table.
 *
 * Returns: none
 */
function leaveTable(seatNum) {
    // Send request to the server for a player to leave the table
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/PokerTable/leaveTable?seatNum=" + seatNum, true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();

    // Update the page for the new table state.
    getTableInfo();
}

/**********************************************************************************************************
 * Web Socket functions
 **********************************************************************************************************/
function establishWebSocketConnection() {

    //
    // Establish socket connection (if not already connected).
    //

    if (("WebSocket" in window) == false) {
        alert("WebSocket is NOT supported by your Browser!");
        return;
    }

    if (ws == null) {
        ws = new WebSocket(wsAddress);
    }
    else {
        console.log("WebSocket already connected.");
        return;
    }

    //
    // onopen function
    //
    ws.onopen = function() {
        console.log("WebSocket opened to " + wsAddress);
        if (getPlayerName() == "") {
            // No user name set.  Don't register the connection.
            console.log("No user logged in.  Connection not registered.")
        }
        else {
            ws.send("RegisterSession:" + getPlayerName());
            console.log("Web Socket connection registered to " + getPlayerName());
        }
    }

    //
    // onmessage function
    //
    ws.onmessage = function(evt) {
        var receivedMsg = evt.data;
        console.log("Received message: " + receivedMsg);

        // If this was an update message, get new table state and refresh the page
        if (receivedMsg = "TableUpdated") {
            getTableInfo();
        }
    }

    //
    // onclose message
    //
    ws.onclose = function() {
        console.log("Websocket connection to " + wsAddress + " closed.");
        ws = null;
    };
}

/**********************************************************************************************************
 * Table Status update functions
 **********************************************************************************************************/

/**
  * Finds the seat number occupied by playerName. Returns 0 if playerName is not found in a seat.
  */
function seatNumForPlayer(playerName){
    playerAtSeat = 0;
    if (tableInfo != null) {
        for (i=0; i<tableInfo.seats.length; i++) {
            if (tableInfo.seats[i].player != null && tableInfo.seats[i].player.playerName == playerName) {
                playerAtSeat = tableInfo.seats[i].seatNum;
                break;
            }
        }
    }
    return playerAtSeat;
}

/*
 * Retrieves the current table info from the server and converts it into a javascript object.
 *
 * Returns: Table information (javascript object)
 */
function getTableInfo() {
    var playerName = getCookie("PlayerName");
    var tableId = 0;

    // Send request to the server to get the table info
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            tableInfo = JSON.parse(this.responseText);
            updateTableDisplay();
        }
    };
    xhttp.open("GET", "http://" + serverAddress + "/rest/PokerTable/getTableState?playerName=" + playerName, true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
}

/*
 * Retrieves the current table info from the server and updates all table info in the page.
 *
 * Returns: none
 */
function updateTableDisplay() {
    // Update the page with the current table Id
    document.getElementById("tableId").innerHTML = "Table Id: " + tableInfo.tableId;

    updateRoundStateDisplay();
    updatePotDisplay();
    updateBoardDisplay();
    updateSeatDisplay();
}

/*
 * Updates the state of the table.
 */
function updateRoundStateDisplay() {
    document.getElementById("roundState").innerHTML = tableInfo.roundState;
}

/*
 * Updates the pot in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function updatePotDisplay() {
    document.getElementById("pot").innerHTML = tableInfo.pot;
}

/*
 * Updates the board in the page based on the tableInfo passed in.
 */
function updateBoardDisplay() {
    var cardFileName;

    for (boardCount=0; boardCount<5; boardCount++) {
        if (tableInfo.board[boardCount] == null) {
            cardFileName = "blank.png";
        }
        else if (tableInfo.board[boardCount].hidden) {
            cardFileName = "back.png";
        }
        else {
            cardFileName = tableInfo.board[boardCount].rank + "_" + tableInfo.board[boardCount].suit + ".png";
        }
        document.getElementById("board"+(boardCount+1)).src = "graphics/" + cardFileName;
    }
}

/*
 * Updates the table of seats in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function updateSeatDisplay() {
    var tableDisplay = "";
    var numSeats = 8;

    for (seatCount=0; seatCount<numSeats; seatCount++) {
        tableDisplay += getSingleSeatDisplay(seatCount, tableInfo);
    }

    document.getElementById("TableBody").innerHTML = tableDisplay;
}

/*
 * Updates the table of seats in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function getSingleSeatDisplay(seatNum) {
    var outputHTML = "";

    // Start of seat row. Each seat row contains 3 cells (columns).
    if (tableInfo.currentAction == seatNum+1) {
        outputHTML += "<tr style='background-color:#FFC2B3'>";
    }
    else {
        outputHTML += "<tr style='background-color:#FFFFFF'>";
    }

    //
    // First cell - Player info
    //
    outputHTML += "<td>";
    outputHTML += "Seat #" + (seatNum+1) + "<br><br>";

    // If the seat is open, set the player name to 'OPEN' and display a button to allow a user to sit down.
    if (tableInfo.seats[seatNum].player == null ) {
        // Add OPEN for player name
        outputHTML += "OPEN<br><br>";

        // If the current player is not already sitting at a seat, display the "Sit Here" button.
        if (seatNumForPlayer(getPlayerName()) == 0) {
            outputHTML += "<button type='button' onClick='sitDown(" + (seatNum+1) + ")'>Sit Here</button>";
        }
    }
    // Else, display the player name and stack size.
    else {
        // Fill in the player info
        outputHTML += "<div style='color:blue'><b>" + tableInfo.seats[seatNum].player.playerName + "</b></div><br>";
        outputHTML += "Stack: " + tableInfo.seats[seatNum].player.stackSize + "<br><br>";

        // If the current player is sitting at this seat, display the "Leave Table" button.
        if (seatNumForPlayer(getPlayerName()) == (seatNum + 1)) {
            outputHTML += "<button type='button' onClick='leaveTable(" + (seatNum+1) + ")'>Leave Table</button>";
        }
    }

    outputHTML += "</td>";

    //
    // Second cell - Cards
    //

    var fileName;
    for (cardCount=0; cardCount<tableInfo.seats[seatNum].cards.length; cardCount++) {
        if (tableInfo.seats[seatNum].player==null) {
            fileName = "blank.png"
        }
        if (tableInfo.seats[seatNum].cards[cardCount]==null) {
            //outputHTML += "<img src='graphics/blank.png' alt='Blank Card' width='100' height='150'>";
            fileName = "blank.png"
        }
        else if (tableInfo.seats[seatNum].cards[cardCount].hidden==true) {
            //outputHTML += "<img src='graphics/back.png' alt='Card Back' width='100' height='150'>";
            fileName = "back.png"
        }
        else {
            fileName = tableInfo.seats[seatNum].cards[cardCount].rank + "_" + tableInfo.seats[seatNum].cards[cardCount].suit + ".png";
            //outputHTML += "<img src='graphics/" + fileName + "' alt='Card' width='100' height='150'>";
        }
        outputHTML += "<td><img src='graphics/" + fileName + "' alt='Card' width='100' height='150'></td>";
    }

    //
    // Third cell - Special Player info (Bet, Dealer/SB/BB, All-In
    //

    outputHTML += "<td>"

    // If this seat is the dealer, display the dealer button.
    if (tableInfo.dealerPosition == seatNum+1) {
        outputHTML += "<img src='graphics/Dealer.png' alt='Dealer' width='50' height='50'>";
    }

    // If this seat is the small blind, display the small blind button.
    if (tableInfo.smallBlindPosition == seatNum+1) {
        outputHTML += "<img src='graphics/SmallBlind.png' alt='Dealer' width='50' height='50'>";
    }

    // If this seat is the big blind, display the big blind button.
    if (tableInfo.bigBlindPosition == seatNum+1) {
        outputHTML += "<img src='graphics/BigBlind.png' alt='Dealer' width='50' height='50'>";
    }

    // Check to see if there is player in this seat.
    if (tableInfo.seats[seatNum]!=null) {
        // TODO - If there is a current bet, display it
        // TODO - If the player is All-In, display it here
    }

    outputHTML += "</td>"

    //
    //end of seat row
    //
    outputHTML += "</tr>";
    return outputHTML;
}


