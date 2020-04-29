/*
 * Stores the new player name in a cookie and updates the display in the page.
 */
function setPlayerName() {
  var playerName = document.getElementById("newPlayerName").value;
  if (window.confirm("Change user name to \"" + playerName + "\"?")) {
    document.cookie = "PlayerName=" + playerName;
    updateDisplayedPlayerName(playerName);
    //document.getElementById("PlayerName").innerHTML = "<b>"+playerName+"</b>";
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
function updateDisplayedPlayerName(playerName) {
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
  // Retrieve the table info from the server and parse it into a javascript object
  var tableInfo = JSON.parse(getTableInfo(playerName, tableId));

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
  var tableDisplay;
  var numSeats = 8;

  for (i=1; i<=numSeats; i++) {
    tableDisplay += getSeatDisplay(i, tableInfo);
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
}









function alertMe(message) {
    alertMessage = "Alert function: " + message;
    window.alert(alertMessage);
}

function getHello1() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById("msg1").innerHTML = this.responseText;
        }
    };
    //xhttp.open("GET", "dummy.txt", true);
    xhttp.open("GET", "http://192.168.86.16/dummy.txt", true);
    xhttp.setRequestHeader("Content-type", "text/plain");
    xhttp.send();
}

function getHello2() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById("msg2").innerHTML = this.responseText;
        }
    };
    xhttp.open("GET", "http://192.168.86.16:8090/Jersey2-HelloWorld/rest/hello/JavaScriptie", true);
    xhttp.setRequestHeader("Content-type", "text/plain");
    xhttp.send();
}
