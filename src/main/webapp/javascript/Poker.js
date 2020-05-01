/*
 * Stores the new player name in a cookie and updates the display in the page.
 */
function setPlayerName() {
    var playerName = document.getElementById("newPlayerName").value;
    if (window.confirm("Change user name to \"" + playerName + "\"?")) {
        document.cookie = "PlayerName=" + playerName;
        updateDisplayedPlayerName(playerName);
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
            updateTableDisplay(this.responseText);
        }
    };
    xhttp.open("GET", "http://192.168.86.16:8080/PokerServer/rest/PokerTable/getTableState", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send();
}

/*
 * Retrieves the current table info from the server and updates all table info in the page.
 *
 * Returns: none
 */
function updateTableDisplay(tableStateAsJSON) {
    var tableInfo = JSON.parse(tableStateAsJSON);

    updatePotDisplay(tableInfo);
    updateSeatDisplay(tableInfo);
    updateBoardDisplay(tableInfo);

    // TODO: REMOVE - TESTING ONLY
    // TODO: Retrieve the table info from the server and parse it into a javascript object
    document.getElementById("TestArea").innerHTML = tableStateAsJSON;
}

/*
 * Updates the pot in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function updatePotDisplay(tableInfo) {
    document.getElementById("pot").innerHTML = tableInfo.pot;
}

/*
 * Updates the table of seats in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function updateSeatDisplay(tableInfo) {
    var tableDisplay = "";
    var numSeats = 8;

    for (i=0; i<numSeats; i++) {
        tableDisplay += getSingleSeatDisplay(i, tableInfo);
    }

    document.getElementById("TableBody").innerHTML = tableDisplay;
}

/*
 * Updates the table of seats in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function getSingleSeatDisplay(seatNum, tableInfo) {
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
    if (tableInfo.seats[seatNum]==null) {
        // Add OPEN for player name
        outputHTML += "OPEN<br>";
        outputHTML += "<button type='button' onClick='sitDown(" + seatNum + ")'>Sit Here</button>";
    }
    // Else, display the player name and stack size.
    else {
        // TODO
        // TODO - Add player name
        // TODO - Add play stack size
    }

    outputHTML += "</td>";

    //
    // Second cell - Player info
    //

    outputHTML += "<td>"

    // If the seat is open, cards should be blank, otherwise set according to table info.
    if (tableInfo.seats[seatNum]==null) {
        outputHTML += "<img src='graphics/blank.png' alt='Blank Card' width='100' height='150'>";
        outputHTML += "<img src='graphics/blank.png' alt='Blank Card' width='100' height='150'>";
    }
    else {
        // TODO update card graphics based on table info
    }

    outputHTML += "</td>"

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

    // TODO: If this seat is the small blind, display the small blind button.
    // TODO: If this seat is the big blind, display the big blind button.

    // Check to see if there is player in this seat.
    if (tableInfo.seats[seatNum]!=null) {
        // TODO - If there is a current bet, display it
        // TODO - If the player is All-In, display it here
    }

    outputHTML += "</td>"



    //end of seat row
    outputHTML += "</tr>";
    return outputHTML;

/*
        <tr>
          <!-- First Cell - Player info -->
          <td>
            <table>
              <tr>
                <td id="Seat1_Name">Seat #1<br><br>OPEN<br><button type="button" onlick="">Sit Here</button></td>

              </tr>
              <tr>
                <td id="Seat1_Stack"></td>
              </tr>
            </table>
          </td>
          <!-- Second Cell - Cards -->
          <td>
            <img id="Seat1_Card1" src="../graphics/blank.png" alt="Card Back" width="100" height="150">
            <img id="Seat1_Card2" src="../graphics/blank.png" alt="Card Back" width="100" height="150">
          </td>
          <!-- Third Cell - Current Bet, Special Info (e.g. Dealer, BB, SB, All-In) -->
          <td>
            <table>
              <tr>
                <td id="Seat1_Bet"></td>
              </tr>
              <tr>
                <td id="Seat1_Info"></td>
              </tr>
            </table>
          </td>
        </tr>
*/
}

/*
 * Updates the board in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function updateBoardDisplay(tableInfo) {
    var cardFileName;

    for (i=0; i<5; i++) {
        if (tableInfo.board[i] == null || tableInfo.board[i].hidden) {
            cardFileName = "back.png";
        }
        else {
            cardFileName = tableInfo.board[i].rank + "_" + tableInfo.board[i].suit + ".png";
        }
        document.getElementById("board"+(i+1)).src = "graphics/" + cardFileName;
    }
}

/*
 * Notify the server that the player has taken a seat.
 *
 * Returns: none
 */
function sitDown(seatNum) {
    // TODO
}

function newGame() {
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
