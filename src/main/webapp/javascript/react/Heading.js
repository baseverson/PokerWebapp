class Heading extends React.Component {
    constructor() {
        super();
        this.state = {
            version: "",
            tableId: ""
        }
    }

    componentDidMount() {
        // Send get version request to the server
        var versionRequest = new XMLHttpRequest();
        versionRequest.onreadystatechange = () => {
            this.setState({version:  versionRequest.responseText});
        }
        versionRequest.open("GET", "http://" + serverAddress + "rest/Table/getVersion", true);
        versionRequest.setRequestHeader("Content-type", "text/plain");
        versionRequest.send();

        // Send get table Id request to the server
        let tableIdRequest = new XMLHttpRequest();
        tableIdRequest.onreadystatechange = () => {
            this.setState({tableId:  tableIdRequest.responseText});
        }
        tableIdRequest.open("GET", "http://" + serverAddress + "rest/Table/getTableId", true);
        tableIdRequest.setRequestHeader("Content-type", "text/plain");
        tableIdRequest.send();
    }

    render() {
        return (
            <h2>Brandt's Poker Room ({this.state.version}) - Table Id: {this.state.tableId}</h2>
        );
    }
}

//ReactDOM.render(<Heading />, document.getElementById("heading"));

