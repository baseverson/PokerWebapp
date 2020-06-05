
class PlayerCreate extends React.Component {
    /**
     * Constructor
     * @param {*} props 
     */
    constructor(props) {
        super(props);
        this.state = {createPlayerName: ''};
    }

    /**
     * Handles changes to the text input field for creating new player.
     */
    handleCreatePlayerChange = (event) => {
        this.setState({createPlayerName: event.target.value});
    }

    /**
     * Handles the form submittal for the new player creation
     */
    handleCreatePlayerSubmit = (event) => {
        event.preventDefault();
        if (window.confirm("Create player '" + this.state.createPlayerName + "'?")) {

            // Send create user request to the server
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = () => {
                if (xhttp.readyState == 4 && (xhttp.status == 200 || xhttp.status == 500)) {
                    console.log(xhttp.responseText);
                    window.alert(xhttp.responseText);
                }
            }

            xhttp.open("POST", "http://" + "localhost:8080/Poker_alpha/" + "rest/PlayerManagement/createPlayer?playerName=" + this.state.createPlayerName, true);
            xhttp.setRequestHeader("Content-type", "text/plain");
            xhttp.send();
        }
    }

    /**
     * Creates the content for displaying the form for creating a new player.
     */
    render() {
        return (
            <form onSubmit={this.handleCreatePlayerSubmit}>
                <h4>Create New Player</h4>
                <table><tbody><tr>
                    <td>
                        New Player Name:
                    </td>
                    <td>
                        <input type="text" onChange={this.handleCreatePlayerChange}/>
                        <input type='submit' value='Create Player'/>
                    </td>
                </tr></tbody></table>
            </form>
        );
    }
}
