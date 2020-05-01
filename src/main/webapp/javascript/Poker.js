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

    // TODO: get table info from server and parse into javascript object
}

/*
 * Retrieves the current table info from the server and updates all table info in the page.
 *
 * Returns: none
 */
function updateTableDisplay() {
    // TODO: Retrieve the table info from the server and parse it into a javascript object
    //var tableInfo = JSON.parse(getTableInfo());
    var tableInfo = null;

    updatePotDisplay(tableInfo);
    updateSeatDisplay(tableInfo);
}

/*
 * Updates the pot in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function updatePotDisplay(tableInfo) {
    // TODO
}

/*
 * Updates the table of seats in the page based on the tableInfo passed in.
 *
 * Returns: none
 */
function updateSeatDisplay(tableInfo) {
    var tableDisplay = "";
    var numSeats = 8;

    for (i=1; i<=numSeats; i++) {
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
    // TODO

    var outputHTML = "";

    // Start of seat row. Each seat row contains 3 cells (columns).
    outputHTML += "<tr>";

    //
    // First cell - Player info
    //
    outputHTML += "<td>";
    outputHTML += "Seat #" + seatNum + "<br><br>";

    // If the seat is open, set the player name to 'OPEN' and display a button to allow a user to sit down.
    // TODO if (table info says seat is open)
    if (true) {
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

    // TODO get cards from the table info.
    // If the seat is open, cards should be blank, otherwise set according to table info.
    // TODO if (seat is open)
    if (true) {
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

    // TODO - If there's special info (Dealer/SB/BB) display
    // TODO - If there is a current bet, display it
    // TODO - If the player is All-In, display it here
    // TODO - If action is on this seat, display input field and button to bet

    outputHTML += "Current Bet: 100<br><br>";
    outputHTML += "<img src='graphics/Dealer.png' alt='Dealer' width='50' height='50'>";
    outputHTML += "<img src='graphics/AllIn.png' alt='AllIn' width='50' height='50'>";

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
 * Notify the server that the player has taken a seat.
 *
 * Returns: none
 */
function sitDown(seatNum) {
    // TODO
}
