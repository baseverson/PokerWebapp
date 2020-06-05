class PlayerInfo extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            playerInfo: ""
        }

        //this.retrievePlayerInfo();
    }

    retrievePlayerInfo() {
        // Send request to the server for the latest player info
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {

                // Clear the playerInfo state
                this.setState({playerInfo: JSON.parse(xhttp.responseText)});

            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                // If we get an error here, it means the player isn't really logged in.
                // Clear the player name cookie and call the logout handler
                setPlayerName("");
                this.props.handleSuccessfulLogout();
            }
        };

        xhttp.open("GET", "http://" + serverAddress + "rest/PlayerManagement/getPlayerInfo?playerName=" + getPlayerName(), true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send();
    }

    handleLogoutPlayerSubmit = () => {
        // TODO: Implement real user logout
        // For now, just set the playerName cookie to an empty string

        if (window.confirm("Log out '" + getPlayerName() + "'?")) {
            // Send login request to the server
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = () => {
                if (xhttp.readyState == 4 && xhttp.status == 200) {
                    // Clear the playerName cookie and playerInfo global variable
                    setPlayerName("");

                    // Clear the playerInfo state
                    this.setState({playerInfo: ""});

                    // Tell the parent that we have a successful logout
                    this.props.handleSuccessfulLogout();
                }
                else if (xhttp.readyState == 4 && xhttp.status == 500) {
                    window.alert(xhttp.responseText);
                }
            };

            xhttp.open("POST", "http://" + serverAddress + "rest/PlayerManagement/logout?playerName=" + getPlayerName(), true);
            xhttp.setRequestHeader("Content-type", "application/json");
            xhttp.send();
        }
    }

    handleBuyInSubmit = () => {
        // Send buy-in request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                this.retrievePlayerInfo();
            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                window.alert(xhttp.responseText);
            }
        }

        // TODO: Hard code buy in amount at 80 chips for now
        xhttp.open("POST", "http://" + serverAddress + "rest/PlayerManagement/buyIn?playerName=" + getPlayerName() + "&buyInAmount=80", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send();
    }

    handleCashOutSubmit = () => {
        // Send cash out request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                this.retrievePlayerInfo();
            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                window.alert(xhttp.responseText);
            }
        }

        xhttp.open("POST", "http://" + serverAddress + "rest/PlayerManagement/cashOut?playerName=" + getPlayerName(), true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send();

    }

    componentDidMount() {
        this.retrievePlayerInfo();
    }

    render() {
        return (
            <div>
                <table style={{padding: "10px"}}>
                    <tbody>
                        <tr>
                            <td style={{padding: "10px"}}>
                                Player Name:      <div style={{color: 'lightgreen'}}><b>{getPlayerName()}</b></div>
                            </td>
                            <td style={{padding: "10px"}}>
                                Total Buy-In amount:      <div style={{color: 'lightgreen'}}><b>{this.state.playerInfo.buyin}</b></div>
                            </td>
                            <td style={{padding: "10px"}}>
                                Stack:      <div style={{color: 'lightgreen'}}><b>{this.state.playerInfo.stack}</b></div>
                            </td>
                        </tr>
                        <tr>
                            <td style={{padding: "10px"}}>
                                <button onClick={this.handleLogoutPlayerSubmit}>Logout</button>
                            </td>
                            <td style={{padding: "10px"}}>
                                <button onClick={this.handleBuyInSubmit}>Buy In (80)</button>
                            </td>
                            <td style={{padding: "10px"}}>
                                <button onClick={this.handleCashOutSubmit}>Cash Out</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        );
    }
}
