class PlayerLogin extends React.Component {
    constructor(props) {
        super(props);
        this.state = {loginPlayerName: ''};
        this.state = {loginPassword: ''};
    }

    /**
     * Handles changes to the player name text input field for logging a player in.
     */
    handleLoginPlayerChange = (event) => {
        this.setState({loginPlayerName: event.target.value});
    }

    /**
     * Handles the form submittal for the player login
     */
    handleLoginPlayerSubmit = (event) => {
        event.preventDefault();
        // TODO: login player logic

        var newPlayerName = "";

        // If the cookie is already populated with a player name, use that name. Otherwise, read the new name from the state.
        if (getPlayerName() != "") {
            newPlayerName = getPlayerName();
        } else {
            newPlayerName = this.state.loginPlayerName;
        }

        // Check to see if a player name was entered
        if (newPlayerName == "") {
            window.alert("Error: Please enter a player name to log in.");
            return;
        }

        // Send login request to the server
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                // Set the player name cookie
                setPlayerName(newPlayerName);

                // Tell the parent that we have a successful login
                this.props.handleSuccessfulLogin();
/*
                // Clear the current playerInfo variable
                setPlayerInfo(null);
                // Register the web socket connection
                ws.send("RegisterSession:" + getPlayerName());
                console.log("Web Socket connection registered to " + getPlayerName());

                // A successful call will result in the player info returned as a JSON.
                // Store the info in the global playerInfo variable and update the UI.
                setPlayerInfo(JSON.parse(this.responseText));
                updateUserFunctions();
                getTableInfo();
 */
            } else if (xhttp.readyState == 4 && xhttp.status == 500) {
                window.alert(xhttp.responseText);
            }
        };

        xhttp.open("POST", "http://" + serverAddress + "rest/PlayerManagement/login?playerName=" + newPlayerName, true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send();
    }

        /**
     * Creates the content for displaying the form for logging in a player.  Called by render().
     */
    render() {
        return (
            <form onSubmit={this.handleLoginPlayerSubmit}>
                <h4>Login Existing Player</h4>
                <table><tbody>
                    <tr>
                        <td>
                            User Name:
                        </td>
                        <td>
                            <input type="text" onChange={this.handleLoginPlayerChange}/>
                            <input type='submit' value='Login'/>
                        </td>
                    </tr>
                </tbody></table> 
            </form>
        );
    }
}
