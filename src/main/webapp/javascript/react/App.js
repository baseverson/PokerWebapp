const { Component } = React;

class App extends React.Component {


    constructor(props) {
        super(props);
        this.state = {
            playerName: getPlayerName(),
        }

        this.ws = new WebSocket(wsAddress);
    }


    /**
     * Handle the response on the login request.
     */
    handleSuccessfulLogin = () => {
        // Set the playerName and playerInfo state variables
        this.setState({playerName: getPlayerName()});
        this.ws.send("RegisterSession:" + this.state.playerName);
    };

    /**
     * Handle the response on the logout request.
     */
    handleSuccessfulLogout = () => {
        // Clear the playerName and playerInfo state variables
        this.ws.send("UnregisterSession:" + this.state.playerName);
        this.setState({playerName: ""});
    };

    componentDidMount() {
        //
        // onopen function
        //
        this.ws.onopen = () => {
            console.log("WebSocket opened to " + wsAddress);
            if (getPlayerName() == "") {
                // No user name set.  Don't register the connection.
                console.log("No user logged in.  Connection not registered.")
            }
            else {
                this.ws.send("RegisterSession:" + getPlayerName());
                console.log("Web Socket connection registered to " + getPlayerName());
            }
        }

        //
        // onmessage function
        //
        this.ws.onmessage = (evt) => {
            var receivedMsg = evt.data;

            console.log(receivedMsg);

            // If this was a table update message, get the new table state and refresh the page
            if (receivedMsg = "TableUpdated") {
                // Tell the Table component to pull the latest player info from the server.
                this.refs.tableComponent.retrieveTableInfo();
            }
            // If this was a player update message, get the new player state and refresh the page
            else if (receivedMsg = "PlayerUpdated") {
                // Tell the PlayerInfo component to pull the latest player info from the server.
                this.refs.playerInfoComponent.retrievePlayerInfo();
            }
        }

        //
        // onclose message
        //
        this.ws.onclose = () => {
            console.log("Websocket connection to " + wsAddress + " closed.");
        }
    }

    /**
     * Render the react object for display.
     */
    render() {

        if (this.state.playerName == "") {
            return (
                <div>
                    <Heading />
                    <PlayerCreate />
                    <PlayerLogin handleSuccessfulLogin = {this.handleSuccessfulLogin}/>
                </div>
            );
        } else {
            return (
                <div>
                    <Heading />
                    <PlayerInfo
                        ref="playerInfoComponent"
                        handleSuccessfulLogout = {this.handleSuccessfulLogout}
                    />
                    <Table
                        ref="tableComponent"
                    />
                </div>
            )
        }
    }
}

ReactDOM.render(<App />, document.getElementById("app"));
