class PlayerActions extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            tableInfo: null
        }
    }

    /**
     * Function for which the parent component may pass updated table information
     * @param newTableInfo - Updated table information
     */
    updateTableInfo = (newTableInfo) => {
        this.setState({tableInfo: newTableInfo});

    }

    /**
     * Determine the seat number for the specified player
     * @param playerName - Player for which to find the seat number
     * @returns {number} - Number of the seat the player is occupying; -1 if player is not occupying a seat
     */
    playerSeatNum(playerName) {
        var result = -1;
        for (var i=0; i<this.state.tableInfo.numSeats; i++) {
            if (this.state.tableInfo.seats[i].player != null) {
                if (this.state.tableInfo.seats[i].player.playerName == getPlayerName()) {
                    result = i;
                }
            }
        }
        return result;
    }

    /**
     * Function to handle when the user clicks the "Leave Table" button
     */
    handleLeaveTableSubmit = () => {
        // Send leave table request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log(xhttp.responseText);
            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                console.log(xhttp.responseText);
                window.alert(xhttp.responseText);
            }
        }

        xhttp.open("POST", "http://" + serverAddress + "rest/Table/leaveTable?playerName=" + getPlayerName(), true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send();
    }

    /**
     * Function to handle when the user clicks the "Fold" Button
     */
    handleFoldSubmit = () => {
        // Send leave table request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log(xhttp.responseText);
            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                console.log(xhttp.responseText);
                window.alert(xhttp.responseText);
            }
        }

        xhttp.open("POST", "http://" + serverAddress + "rest/Table/fold?playerName=" + getPlayerName(), true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send();
    }

    /**
     * Function to handle when the user clicks the "Check" button
     */
    handleCheckSubmit = () => {
        // If the current best is not 0, player cannot check.
        if (this.state.tableInfo.currentBet !=0) {
            window.alert("There is a current bet. Checking is not allowed.");
            return;
        }

        // Send check request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log(xhttp.responseText);
            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                console.log(xhttp.responseText);
                window.alert(xhttp.responseText);
            }
        }

        xhttp.open("POST", "http://" + serverAddress + "rest/Table/check?playerName=" + getPlayerName(), true);
        xhttp.setRequestHeader("Content-type", "text/plain");
        xhttp.send();
    }

    /**
     * Function to handle when the user clicks the "Call" button
     */
    handleCallSubmit = () => {
        // Send call request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log(xhttp.responseText);
            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                console.log(xhttp.responseText);
                window.alert(xhttp.responseText);
            }
        }

        xhttp.open("POST", "http://" + serverAddress + "rest/Table/call?playerName=" + getPlayerName(), true);
        xhttp.setRequestHeader("Content-type", "text/plain");
        xhttp.send();
    }

    /**
     * Function to handle when the user clicks the "Bet" / "Raise" buttons
     */
    handleBetRaiseSubmit = () => {
        // Get the bet amount entered by the user
        var betAmount = document.getElementById("betAmount").value;

        let seatNum = this.playerSeatNum(getPlayerName());

        // If the current bet is 0, bet needs to be at least the big blind.
        if (this.state.tableInfo.currentBet == 0 && betAmount < this.state.tableInfo.bigBlind) {
            window.alert("The best must be at least the size of the big blind.");
            return;
        }

        // If the current bet is greater than zero, the bet needs to be at least double the current bet.
        if (this.state.tableInfo.currentBet > 0 && betAmount < (this.state.tableInfo.currentBet * 2)) {
            window.alert("The best must be at least double the current bet.");
            return;
        }

        // Check to make sure the bet isn't bigger than the player's stack
        if (this.state.tableInfo.seats[seatNum].player.stack < betAmount) {
            if (window.confirm("The bet is larger than your current stack. Go All In?")) {
                // Player says yes, go All In. Set the betAmount to the player's stack size.
                betAmount = this.state.tableInfo.seats[seatNum].player.stack;
            }
            else {
                // Player does not want to go all in. Do nothing.
                return;
            }
        }

        // Send call request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log(xhttp.responseText);
            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                console.log(xhttp.responseText);
                window.alert(xhttp.responseText);
            }
        }

        xhttp.open("POST", "http://" + serverAddress +
            "rest/Table/bet?playerName=" + getPlayerName() +
            "&betAmount=" + betAmount,
            true);
        xhttp.setRequestHeader("Content-type", "text/plain");
        xhttp.send();
    }

    /**
     * Function to handle when the user clicks the "All In" button
     */
    handleAllInSubmit = () => {
        // Send all in request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log(xhttp.responseText);
            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                console.log(xhttp.responseText);
                window.alert(xhttp.responseText);
            }
        }

        xhttp.open("POST", "http://" + serverAddress + "rest/Table/allIn?playerName=" + getPlayerName(), true);
        xhttp.setRequestHeader("Content-type", "text/plain");
        xhttp.send();
    }

    /**
     * Main render function for the React component.
     * @returns {*} - returns the DOM elements to render on the page
     */
    render() {
        // Generate user action buttons (or don't) based on the latest table info.
        let leaveTableButton;
        let foldButton;
        let checkCallButton;
        let betRaiseLabel;
        let betRaiseInputField;
        let betRaiseButton;
        let allInButton;

        if (this.state.tableInfo != null) {
            // Get the player's seat number.
            let seatNum = this.playerSeatNum(getPlayerName());

            // If the player is currently sitting in a seat, display the "Leave Table" button.
            if (seatNum != -1) {
                leaveTableButton = <button onClick={this.handleLeaveTableSubmit}>Leave Table</button>
            }

            // If the action is on this player, display the various action buttons.
            if (this.state.tableInfo.currentAction >= 0 &&
                this.state.tableInfo.currentAction == seatNum) {

                // Always display the fold button to the action seat.
                foldButton = <button onClick={this.handleFoldSubmit}>Fold</button>

                //
                // If the current bet is zero, display the check and bet buttons
                //

                if (this.state.tableInfo.currentBet == 0) {
                    // Current bet is zero. Display the check button.
                    checkCallButton = <button onClick={this.handleCheckSubmit}>Check</button>

                    // The min bet is at least the big blind. If the player's stack size is equal to or less than
                    // the big blind, don't display the bet button. They can only go All In.
                    if (this.state.tableInfo.bigBlind < this.state.tableInfo.seats[seatNum].player.stack) {
                        betRaiseLabel = "Min Bet: " + this.state.tableInfo.bigBlind;
                        betRaiseInputField = <input id={"betAmount"} size={5}/>
                        betRaiseButton = <button onClick={this.handleBetRaiseSubmit}>Bet</button>
                    }
                }

                //
                // Current bet is > 0. Display the call and raise buttons
                //
                else if (this.state.tableInfo.currentBet > 0) {
                    // If the current bet is less than the player's current stack, display the call button
                    if (this.state.tableInfo.currentBet < this.state.tableInfo.seats[seatNum].player.stack) {
                        // Check for the special case that this is the big blind and a call is a check.
                        let callAmount = this.state.tableInfo.currentBet - this.state.tableInfo.seats[seatNum].playerBet;
                        if (callAmount == 0) {
                            // Display the call button, but label it as a check
                            checkCallButton = <button onClick={this.handleCallSubmit}>Check</button>
                        } else {
                            checkCallButton = <button onClick={this.handleCallSubmit}>Call ({callAmount})</button>
                        }
                    }

                    // Check to make sure the player stack is more than double the current bet.
                    // If not, they can't raise. They can only go All In.a
                    if ((this.state.tableInfo.currentBet * 2) < this.state.tableInfo.seats[seatNum].player.stack) {
                        betRaiseLabel = "Min Raise: " + (this.state.tableInfo.currentBet * 2);
                        betRaiseInputField = <input id={"betAmount"} size={5}/>
                        betRaiseButton = <button onClick={this.handleBetRaiseSubmit}>Raise</button>
                    }
                }

                //
                // Always display the All-In button
                //
                allInButton = <button onClick={this.handleAllInSubmit}>All-In ({this.state.tableInfo.seats[seatNum].player.stack})</button>
            }
        }

        return (
            <div>
                <table style={{padding: "10px"}}>
                    <tbody>
                    <tr>
                        <td style={{padding: "10px"}}>
                            {leaveTableButton}
                        </td>
                        <td style={{padding: "10px"}}>
                            {foldButton}
                        </td>
                        <td style={{padding: "10px"}}>
                            {checkCallButton}
                        </td>
                        <td style={{padding: "10px"}}>
                            {betRaiseLabel} {betRaiseInputField} {betRaiseButton}
                        </td>
                        <td style={{padding: "10px"}}>
                            {allInButton}
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        );
    }
}
