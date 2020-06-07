class ActionLog extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            logInfo: null,
            logString: ""
        }
    }

    updateLogInfo = (newLogInfo) => {
        this.setState({logInfo: newLogInfo});

        var newLogString = "";
        if (this.state.logInfo != null) {
            for (var i=0; i<this.state.logInfo.length; i++) {
                newLogString += this.state.logInfo[i] + "\n";
            }
        }
        this.setState({logString: newLogString});
    }

    componentDidMount() {
        var logArea = document.getElementById("log");
        logArea.scrollTop = logArea.scrollHeight;
    }

    render() {
        return (
            <div>
                <textarea id="log" name="log" rows="10" cols="80" value={this.state.logString}>{this.state.logString}</textarea>
            </div>
        );
    }
}
