var tableInfo = null;
var ws = null;
//var serverAddress = "192.168.86.16:8080/PokerServer";
//var serverAddress = "76.95.180.166:8076/PokerServer";
var serverAddress = "sevdev.ddns.net:8076/Poker_alpha";
var wsAddress = "ws://" + serverAddress + "/PokerWebSocket";

/**********************************************************************************************************
 * General functions
 **********************************************************************************************************/
function log(message) {
    if (typeof console == "object") {
        console.log(message);
    }
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
        log("WebSocket already connected.");
        return;
    }

    //
    // onopen function
    //
    ws.onopen = function() {
        log("WebSocket opened to " + wsAddress);
        if (getPlayerName() == "") {
            // No user name set.  Don't register the connection.
            log("No user logged in.  Connection not registered.")
        }
        else {
            ws.send("RegisterSession:" + getPlayerName());
            log("Web Socket connection registered to " + getPlayerName());
        }
    }

    //
    // onmessage function
    //
    ws.onmessage = function(evt) {
        var receivedMsg = evt.data;
        log("Received message: " + receivedMsg);

        // If this was an update message, get new table state and refresh the page
        if (receivedMsg = "TableUpdated") {
            getTableInfo();
        }
    }

    //
    // onclose message
    //
    ws.onclose = function() {
        log("Websocket connection to " + wsAddress + " closed.");
        ws = null;
    };
}

/**********************************************************************************************************
 * User management functions
 **********************************************************************************************************/

/*
 * Display player creation/login/logout functions.
 */
function updateUserFunctions() {
    var outputHTML = "";
    var playerName = getPlayerName();

    // Check to see if the player name is null.  If not, the user is logged in.
    if (playerName != "") {
        // Player logged in. Display the player name.
        outputHTML += "Player Name: <div style='color:blue'><b>" + playerName + "</b></div><br>";
        outputHTML += "<button type='button' onclick='logout()'>Logout</button>";
    }
    else {
        // No player logged in. Display the create user and login options.
        outputHTML += "Create New Player<br>";
        outputHTML += "<input id='createPlayerName'>   "
        outputHTML += "<button type='button' onclick='createPlayer()'>Create New Player</button>";

        outputHTML += "<br><br>";

        outputHTML += "Player Login<br>";
        outputHTML += "<input id='loginPlayerName'>   "
        outputHTML += "<button type='button' onclick='login()'>Login</button>";

    }

    document.getElementById("userFunctions").innerHTML = outputHTML;
}

/*
 * Create a new player
 */
function createPlayer() {
    var playerName = document.getElementById("createPlayerName").value;

    // Check to see if a player name was entered
    if (playerName == "") {
        window.alert("Error: Please enter a player name to create.");
        return;
    }

    // Check to see if a player is already logged in
    if (getPlayerName() != "") {
        window.alert("Error: Player already logged in.");
        return;
    }

    // Send create user request to the server
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {

            // Upon success response from the server, automatically log the user in.
            // Set the cookie, establish the web socket, and update the UI.
            log(this.responseText);

            // Set the player name cookie
            document.cookie = "PlayerName=" + playerName;

            // Register the web socket connection
            ws.send("RegisterSession:" + getPlayerName());
            console.log("Web Socket connection registered to " + getPlayerName());

            // update the UI
            updateUserFunctions();
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/PlayerManagement/createPlayer?playerName=" + playerName, true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
}

/*
 * Logs in a player
 */
function login() {
    // TODO: Implement real user login and authentication
    var playerName = document.getElementById("loginPlayerName").value;

    // Check to see if a player name was entered
    if (playerName == "") {
        window.alert("Error: Please enter a player name to log in.");
        return;
    }

    // Check to see if a player is already logged in
    if (getPlayerName() != "") {
        window.alert("Error: Player already logged in.");
        return;
    }

    // Send login request to the server
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {

            // Upon success response from the server, set the cookie, establish the web socket, and update the UI.
            log(this.responseText);

            // Set the player name cookie
            document.cookie = "PlayerName=" + playerName;

            // Register the web socket connection
            ws.send("RegisterSession:" + getPlayerName());
            console.log("Web Socket connection registered to " + getPlayerName());

            // update the UI
            updateUserFunctions();
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/PlayerManagement/login?playerName=" + playerName, true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
/*
    // For now, just store the player name in a cookie
    var playerName = document.getElementById("loginPlayerName").value;
    if (window.confirm("Login player \"" + playerName + "\"?")) {

        // Unregister the session with this username
        if(getPlayerName() != "") {
            ws.send("UnregisterSession:" + getPlayerName());
            log("Web Socket connection registration removed for " + getPlayerName());
        }

        document.cookie = "PlayerName=" + playerName;
        ws.send("RegisterSession:" + getPlayerName());
        log("Web Socket connection registered to " + getPlayerName());
    }
    document.getElementById("newPlayerName").innerHTML="";
*/
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
        log("Web Socket connection registration removed for " + getPlayerName());

        document.cookie = "PlayerName=";
        //updateDisplayedPlayerName();
        updateUserFunctions();
    }
}

/*
 * Reads the player name from the cookie.
 */
function getPlayerName() {
    var playerName = getCookie("PlayerName");
    log("Reading current player name: " + playerName);
    return playerName;
}

/**********************************************************************************************************
 * User action functions
 **********************************************************************************************************/

/*
 * Notify the server that the player has requested to take a seat.
 */
function sitDown(seatNum) {
    // Only allow a player to sit down if they have a playerName defined
    if (getPlayerName() == "") {
        log("Cannot sit at table. Player name not defined.")
        window.alert("You must set your player name before you can sit at the table.");
        return;
    }

    // Send request to the server for a player to sit at a seat
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/Table/sitDown?playerName=" + getPlayerName() + "&seatNum=" + seatNum, true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/*
 * Notify the server that the player has requested to leave the table.
 */
function leaveTable(seatNum) {
    // Send request to the server for a player to leave the table
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/Table/leaveTable?seatNum=" + seatNum, true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/*
 * Notify the server that the player has requested to fold.
 */
function fold() {
    // Send request to the server for a player to leave the table
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/Table/fold?playerName=" + getPlayerName(), true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/**
  * Place a bet (send the bet to the server).
  */
function placeBet(betAmount) {
    // Send request to the server for a player to leave the table
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/Table/bet?playerName=" + getPlayerName() + "&betAmount=" + betAmount, true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/*
 * Notify the server that the player has placed a bet.
 */
function bet() {
    // Get the bet amount entered by the user
    var betAmount = document.getElementById("betAmount").value;
    var seatNum = tableInfo.currentAction;

    // If the current bet is 0, bet needs to be at least the big blind.
    if (tableInfo.currentBet == 0 && betAmount < tableInfo.bigBlind) {
        window.alert("The best must be at least the size of the big blind.");
        return;
    }

    // If the current bet is greater than zero, the bet needs to be at least double the current bet.
    if (tableInfo.currentBet > 0 && betAmount < (tableInfo.currentBet * 2)) {
        window.alert("The best must be at least double the current bet.");
        return;
    }

    // Check to make sure the bet isn't bigger than the player's stack
    if (tableInfo.seats[seatNum-1].player.stackSize < betAmount) {
        if (window.confirm("The bet is larger than your current stack. Go All In?")) {
            // Player says yes, go All In. Set the betAmount to the player's stack size.
            betAmount = tableInfo.seats[seatNum-1].player.stackSize;
        }
        else {
            // Player does not want to go all in. Do nothing.
            return;
        }
    }

    // Place the bet
    placeBet(betAmount);
}

/**
  * Player is checking. {
  */
function check() {
    // If the current bet is not 0, player cannot check.
    if (tableInfo.currentBet != 0) {
        window.alert("There is a current bet. Checking is not allowed.");
        return;
    }

    // Send request to the server for a player to leave the table
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/Table/check?playerName=" + getPlayerName(), true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/**
  * Player is calling.
  */
function call() {
    // Send request to the server for a player to leave the table
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/Table/call?playerName=" + getPlayerName(), true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/**
  * Player is going all in.
  */
function allin() {
    // Send request to the server for a player to leave the table
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            log(this.responseText);
        }
    };

    xhttp.open("POST", "http://" + serverAddress + "/rest/Table/allIn?playerName=" + getPlayerName(), true);
    xhttp.setRequestHeader("Content-type", "test/plain");
    xhttp.send();
}

/**********************************************************************************************************
 * UI display update functions
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
 * Retrieves the current table info from the server and converts it into a javascript object that is
 * stored as a global variable. Upon receipt of the table status response, call updateTableDisplay() to
 * update the UI.
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
    xhttp.open("GET", "http://" + serverAddress + "/rest/Table/getTableState?playerName=" + playerName, true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
}

/*
 * Retrieves the current table info from the server and updates all table info in the page.
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
 */
function updateSeatDisplay() {
    var tableDisplay = "";
    var numSeats = 8;

    for (seatCount=0; seatCount<numSeats; seatCount++) {
        tableDisplay += getSingleSeatDisplay(seatCount, tableInfo);
    }

    document.getElementById("TableBody").innerHTML = tableDisplay;
}

/**
  * Produces HTML for a single seat as designated by the seatNum param passed in.
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

    outputHTML += getSeatPlayerInfo(seatNum);
    outputHTML += getSeatCards(seatNum);
    outputHTML += getSeatSpecialInfo(seatNum);
    outputHTML += getSeatActionInputs(seatNum);

    //
    //end of seat row
    //
    outputHTML += "</tr>";
    return outputHTML;
}

/**
  * Produces HTML for the first column (player info) of a single seat as designated by the seatNum param passed in.
  */
function getSeatPlayerInfo(seatNum) {
    var outputHTML = "<td>";
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

    // Close the cell
    outputHTML += "</td>";

    return outputHTML;
}

/**
  * Produces HTML for the second column (cards) of a single seat as designated by the seatNum param passed in.
  */
function getSeatCards(seatNum) {
    var outputHTML = "";
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

    return outputHTML;
}

/**
  * Produces HTML for the third column (special info) of a single seat as designated by the seatNum param passed in.
  */
function getSeatSpecialInfo(seatNum) {
    var outputHTML = "<td>"

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
    if (tableInfo.seats[seatNum].player!=null) {

        // If there is a current bet, display it.
        if (tableInfo.seats[seatNum].playerBet !=0) {
            outputHTML += "<br>";
            outputHTML += "Current Bet: " + tableInfo.seats[seatNum].playerBet;
        }
    }

    // Close the cell
    outputHTML += "</td>"

    return outputHTML;
}

/**
  * Produces HTML for the fourth column (action inputs) of a single seat as designated by the seatNum param passed in.
  */
function getSeatActionInputs(seatNum) {
    // If the round state is showdown, do nothing.  Players can take no more action.
    if (tableInfo.roundState == "SHOWDOWN") {
        return;
    }

    var outputHTML = "<td>";

    // Check to see if this seat is in the hand and All In. If so, display the All In button and return.
    // Don't show any other action buttons.
    if (tableInfo.seats[seatNum].inHand == true && tableInfo.seats[seatNum].isAllIn == true) {
        // Display the All In button.
        outputHTML += "<img src='graphics/AllIn.png' alt='Dealer' width='50' height='50'>";
    }
    else {
        // Check to first make sure there is a player in this seat
        if (tableInfo.seats[seatNum].player != null) {

            // Only display the action buttons if:
            // 1) this is for the logged in player
            // 2) the current action is on this seat
            // 3) the seat/player is not already All-In (the All In check was done previously in this function)
            if (tableInfo.seats[seatNum].player.playerName == getPlayerName() &&
                tableInfo.currentAction == seatNum+1) {

                //***********************************************************
                // Always display fold button
                //***********************************************************
                outputHTML += "<button type='button' onClick='fold()'>Fold</button>";
                outputHTML += "<br><br>";

                //***********************************************************
                // Current Bet is 0 - Display the Check and Bet buttons
                //***********************************************************

                // If the current bet is zero, display the check button
                if (tableInfo.currentBet == 0) {
                    outputHTML += "<button type='button' onClick='check()'>Check</button>";
                    outputHTML += "<br><br>";

                    // The min bet is at least the big blind.  If the player's stack size is equal to or less than the
                    // big blind, display All In button.
                    if (tableInfo.bigBlind < tableInfo.seats[seatNum].player.stackSize) {
                        // Display the minimum bet amount (big blind)
                        outputHTML += "Min Bet: " + tableInfo.bigBlind + "<br>";
                        // Display the bet input field
                        outputHTML += "<input id='betAmount' size='5'><br>"
                        outputHTML += "<button type='button' onClick='bet()'>Bet</button>";
                        outputHTML += "<br><br>";
                    }
                }

                //***********************************************************
                // Current Bet is > 0 - Display the Call and Raise buttons
                //***********************************************************

                // If the current bet is greater than zero, display the Call/All-In button
                else if (tableInfo.currentBet > 0) {
                // If the current bet is less than the player's current stack, display the Call button
                    if (tableInfo.currentBet < tableInfo.seats[seatNum].player.stackSize) {
                        var callAmount = tableInfo.currentBet - tableInfo.seats[seatNum].playerBet;
                        outputHTML += "<button type='button' onClick='call()'>Call (" + callAmount + ")</button>";
                        outputHTML += "<br><br>";
                    }

                    // Check to make sure the current stack size is more than double the current bet. If not, all they can do is go All In.
                    if ((tableInfo.currentBet * 2) < tableInfo.seats[seatNum].player.stackSize) {
                        // Display the minimum raise amount (twice the current bet). This will be checked in the bet function.
                        outputHTML += "Min Raise: " + (tableInfo.currentBet * 2) + "<br>";
                        // Display the bet input field
                        outputHTML += "<input id='betAmount' size='5'><br>"
                        outputHTML += "<button type='button' onClick='bet()'>Raise</button>";
                        outputHTML += "<br><br>";
                    }
                }

                //*************************************************
                // Always display the All-In Button
                //*************************************************

                outputHTML += "<button type='button' onClick='allin()'>All-In (" + tableInfo.seats[seatNum].player.stackSize + ")</button>";
            }
        }
    }

    // Close the cell
    outputHTML += "</td>";

    return outputHTML;
}


