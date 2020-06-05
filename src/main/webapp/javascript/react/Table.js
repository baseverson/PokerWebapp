class Table extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            images: [],
            tableInfo: null
        }

        this.seatOffset = [
            { // Seat 1 (index 0)
                x: 350,
                y: 70
            },
            { // Seat 2 (index 1)
                x: 620,
                y: 70
            },
            {
                x: 850,
                y: 70
            }
        ];
    }

    componentDidMount() {
        let sources = [
            "./graphics/background/red_texture_shadow_side.jpg",
            "./graphics/background/table-black.png",
            "./graphics/Dealer.png",
            "./graphics/BigBlind.png",
            "./graphics/SmallBlind.png",
            "./graphics/AllIn.png",
            "./graphics/winner.jpg",
            "./graphics/blank.png",
            "./graphics/back.png",
            "./graphics/2_clubs.png",
            "./graphics/3_clubs.png",
            "./graphics/4_clubs.png",
            "./graphics/5_clubs.png",
            "./graphics/6_clubs.png",
            "./graphics/7_clubs.png",
            "./graphics/8_clubs.png",
            "./graphics/9_clubs.png",
            "./graphics/10_clubs.png",
            "./graphics/J_clubs.png",
            "./graphics/Q_clubs.png",
            "./graphics/K_clubs.png",
            "./graphics/A_clubs.png",
            "./graphics/2_diamonds.png",
            "./graphics/3_diamonds.png",
            "./graphics/4_diamonds.png",
            "./graphics/5_diamonds.png",
            "./graphics/6_diamonds.png",
            "./graphics/7_diamonds.png",
            "./graphics/8_diamonds.png",
            "./graphics/9_diamonds.png",
            "./graphics/10_diamonds.png",
            "./graphics/J_diamonds.png",
            "./graphics/Q_diamonds.png",
            "./graphics/K_diamonds.png",
            "./graphics/A_diamonds.png",
            "./graphics/2_hearts.png",
            "./graphics/3_hearts.png",
            "./graphics/4_hearts.png",
            "./graphics/5_hearts.png",
            "./graphics/6_hearts.png",
            "./graphics/7_hearts.png",
            "./graphics/8_hearts.png",
            "./graphics/9_hearts.png",
            "./graphics/10_hearts.png",
            "./graphics/J_hearts.png",
            "./graphics/Q_hearts.png",
            "./graphics/K_hearts.png",
            "./graphics/A_hearts.png",
            "./graphics/2_spades.png",
            "./graphics/3_spades.png",
            "./graphics/4_spades.png",
            "./graphics/5_spades.png",
            "./graphics/6_spades.png",
            "./graphics/7_spades.png",
            "./graphics/8_spades.png",
            "./graphics/9_spades.png",
            "./graphics/10_spades.png",
            "./graphics/J_spades.png",
            "./graphics/Q_spades.png",
            "./graphics/K_spades.png",
            "./graphics/A_spades.png",
        ];
        this.preloadImages(sources, this.state.images, this.drawTable);
        this.retrieveTableInfo();
    }

    preloadImages(sources, images, callback) {
        var image;
        var remaining = sources.length;
        for (var i=0; i<sources.length; i++) {
            image = new Image();
            image.onload = () => {
                --remaining;
                if (remaining <=0) {
                    this.drawTable()
                    //callback();
                }
            };
            image.src = sources[i];
            images.push(image);
        }
    }

    handleCanvasClickEvent(event) {
        console.log("You clicked on the canvas at " + event.offsetX + ":" + event.offsetY);
    }

    retrieveTableInfo() {
        // Send request to the server for the latest table info
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {

                // Clear the playerInfo state
                this.setState({tableInfo: JSON.parse(xhttp.responseText)});
                this.drawTable();

            }
            else if (xhttp.readyState == 4 && xhttp.status == 500) {
                console.log("Unable to update the state of the table. Error: " + xhttp.responseText);
                window.alert("Unable to update the state of the table.");
            }
        };

        xhttp.open("GET", "http://" + serverAddress + "rest/Table/getTableState?playerName=" + getPlayerName(), true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send();
    }

    drawTable() {
        // Get the canvas and context from the DOM
        var canvas = document.getElementById("tableCanvas");
        var ctx = canvas.getContext("2d")

        // Add an event handler to handle click events on the canvas
        canvas.addEventListener('click', this.handleCanvasClickEvent);

        // Clear the canvas to start
        ctx.clearRect(0,0,canvas.width,canvas.height);

        // Draw the canvas from the bottom up
        this.drawBackground(ctx);
        this.drawTableBackground(ctx);
        this.drawBoard(ctx);
        this.drawPot(ctx);
        this.drawSeats(ctx);
    }

    drawBackground(ctx) {
        let img = new Image();
        img.src = "./graphics/background/red_texture_shadow_side.jpg";
        //img.onload = () => {
            ctx.drawImage(img, 0, 0, 1400, 800);
        //}
    }

    drawTableBackground(ctx) {
        let img = new Image();
        img.src = "./graphics/background/table-black.png";
        //img.onload = () => {
            const x_offset = 100;
            const y_offset = 100;
            ctx.drawImage(img, x_offset, y_offset, 1200, 600);
        //}
    }

    drawBoard(ctx) {
        if (this.state.tableInfo == null) {
            // No table state retrieved. Just return.
            return;
        }

        const x_offset = 495;
        const y_offset = 250;
        const x_spacing = 85;
        const cardWidth = 80;
        const cardHeight = 120;

        let cardFileName = "";

        // Make sure we have cards
        if (this.state.tableInfo.board.length == 5) {
            // Loop through the board cards
            for (let boardCount=0; boardCount<5; boardCount++) {
                // If the board card info is null, display a blank card
                if (this.state.tableInfo.board[boardCount] == null) {
                    cardFileName = "blank.png";
                }
                // If the card is hidden, display the card back
                else if (this.state.tableInfo.board[boardCount].hidden) {
                    cardFileName = "back.png";
                } else {
                    // Otherwise, display the card
                    cardFileName = this.state.tableInfo.board[boardCount].rank + "_" + this.state.tableInfo.board[boardCount].suit + ".png";
                }

                let img = new Image();
                // Setting the image source will load the image
                img.src = "./graphics/" + cardFileName;
                img.onload = () => {
                    // Once the image is loaded, draw it on the canvas
                    ctx.drawImage(img, x_offset + (boardCount * x_spacing), y_offset, cardWidth, cardHeight);
                }
            }
        }
    }

    drawPot(ctx) {
        if (this.state.tableInfo == null) {
            // No table state retrieved. Just return.
            return;
        }

        const x_offset = 500;
            const y_offset = 400;

            // TODO

            ctx.font = "15px Arial";
            ctx.fillStyle = "#ffffff";
            ctx.textAlign = "start";
            ctx.fillText("Pot #1: " + this.state.tableInfo.bigBlind, x_offset, y_offset);
            ctx.fillText("PlayerA", x_offset, y_offset + 20);
            ctx.fillText("PlayerB", x_offset, y_offset + 40);
    }

    drawSeats(ctx) {
        if (this.state.tableInfo == null) {
            // No table state retrieved. Just return.
            return;
        }

        const x_spacing = 55;
        const cardWidth = 50;
        const cardHeight = 75;

        let img = new Image();
        // Setting the image source will load the image
        img.src = "./graphics/" + "back.png";
        img.onload = () => {
            // Once the image is loaded, draw it on the canvas
            ctx.drawImage(img, this.seatOffset[0].x, this.seatOffset[0].y, cardWidth, cardHeight);
            ctx.drawImage(img, this.seatOffset[0].x + x_spacing, this.seatOffset[0].y, cardWidth, cardHeight);
        }


        // TODO
    }

    render() {
        return (
            <div>
                <canvas id="tableCanvas" width={1400} height={800} style={{border: "2px solid #000000"}}>
                    Your browser does not support the HTML5 canvas tag.
                </canvas>
                <PlayerActions />
            </div>
        );
    }
}
