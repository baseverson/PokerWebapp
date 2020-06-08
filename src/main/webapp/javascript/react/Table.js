class Table extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            images: [],
            tableInfo: null
        };

        // Number of seats at the table
        this.numSeats = 10;

        // Offsets for the board cards. Controls the placement of the board card images on the table.
        this.boardOffsets = {
            x: 495,
            y: 250,
            spacing: 5,
            cardWidth: 80,
            cardHeight: 120
        }

        // Offsets for the pots. Controls the placement of the pots on the table.
        this.potOffsets = {
            x: 450,
            y: 400,
            potSpacing: 100,
            nameSpacing: 20
        };

        // Define the dimensions for each seat's space on the canvas
        this.seatAreaWidth = 200;
        this.seatAreaHeight = 200;

        // Define the location of each seat's space on the canvas
        this.seatOffsets = [
            // top row
            {x: 355, y: 15},
            {x: 605, y: 15},
            {x: 855, y: 15},
            // right side
            {x: 1145, y: 170},
            {x: 1145, y: 420},
            // bottom row
            {x: 855, y: 575},
            {x: 605, y: 575},
            {x: 355, y: 575},
            // left side
            {x: 60, y: 420},
            {x: 60, y: 170},
        ];
    }

    getSeatElementOffsets(seatNum) {
        // Define the relative location for the elements of each seat canvas.
        // Each seat will have a different arrangement of its elements depending on its position around the table
        switch (seatNum) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                return (
                    {
                        name: {x: 15, y: 25},
                        stack: {x: 110, y: 25},
                        cards: {x: 15, y: 30},
                        buttons: {x: 15, y: 155},
                        allin: {x: 55, y: 155},
                        bet: {x: 110, y: 170},
                        winner: {x: 110, y: 155}
                    }
                );
                break;
            case 5:
            case 6:
            case 7:
                return (
                    {
                        buttons: {x: 10, y: 5},
                        allin: {x: 55, y: 5},
                        bet: {x: 110, y: 40},
                        winner: {x: 110, y: 10},
                        cards: {x: 15, y: 50},
                        name: {x: 15, y: 190},
                        stack: {x: 110, y: 190}
                    }
                );
                break;
            case 8:
            case 9:
                return (
                    {
                        name: {x: 15, y: 25},
                        stack: {x: 110, y: 25},
                        cards: {x: 15, y: 30},
                        buttons: {x: 110, y: 155},
                        allin: {x: 155, y: 155},
                        bet: {x: 15, y: 180},
                        winner: {x: 15, y: 155}
                    }
                );
                break;
        }
    }

    /**
     * Lifecycle function for the component - will run after the component is mounted
     */
    componentDidMount() {
        // graphics to pre-load
        let sources = [
            "./graphics/background/red_texture_shadow_side.jpg",
            "./graphics/background/table-black.png",
            "./graphics/openSeat.png",
            "./graphics/Dealer.png",
            "./graphics/BigBlind.png",
            "./graphics/SmallBlind.png",
            "./graphics/AllIn.png",
            "./graphics/winner.png",
            "./graphics/back.png",
            "./graphics/blank.png",
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
            "./graphics/A_spades.png"
        ];

        // Pre-load the graphics specified above. Once the pre-load is complete, pull the latest table info
        // This will force a table display update.
        this.preloadImages(sources, this.state.images, this.drawTable);

        // One the graphics are pre-loaded, pull the latest table info (this will force a table display update
        this.retrieveTableInfo();
    }

    /**
     * Preload images specified and then call the callback function specified.
     * @param sources - list of images to preload
     * @param images - array to store the images objects in once loaded
     * @param callback - this function will be called when all images are loaded
     */
    preloadImages(sources, images, callback) {
        var image;
        var remaining = sources.length;
        for (var i=0; i<sources.length; i++) {
            image = new Image();
            image.onload = () => {
                --remaining;
                if (remaining <=0) {
                    callback();
                }
            };
            image.src = sources[i];
            images.push(image);
        }
    }

    /**
     * Function to handle when the user clicks in the canvas
     * @param event - info about the click event
     */
    handleCanvasClickEvent = (event) => {
        // Loop through each seat and see if the click fell within the seat's area
        for (var i=0; i<this.numSeats; i++) {
            //console.log(
            //    "Seat #" + i + " dimensions: " +
            //    "x: " + this.seatOffsets[i].x + ":" + (this.seatOffsets[i].x + this.seatAreaWidth) + " " +
            //    "y: " + this.seatOffsets[i].y + ":" + (this.seatOffsets[i].y + this.seatAreaHeight)
            //);

            if (
                event.offsetX >= this.seatOffsets[i].x &&
                event.offsetX <= (this.seatOffsets[i].x + this.seatAreaWidth) &&
                event.offsetY >= this.seatOffsets[i].y &&
                event.offsetY <= (this.seatOffsets[i].y + this.seatAreaHeight)
            )
            {
                console.log("This click is in the seat #" + i + " area!");
                // Found which seat area this click is in.  Check to see if the seat is current open.
                if (this.state.tableInfo.seats[i].player == null &&
                     this.playerSeatNum(getPlayerName()) < 0) {
                    // Seat is open.  Call sitDown() to place the player in this seat.
                    // Don't forget to increment the counter (which starts at 0) to get the seat number (which starts at 1).
                    this.sitDown(i);
                }
            }
        }
        //console.log("You clicked on the canvas at " + event.offsetX + ":" + event.offsetY);
    }

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
     * Player wishes to sit down in a seat.
     * @param seatNum - seat number the player wants to sit in
     */
    sitDown(seatNum) {
        // Send request to the server to seat the player
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log(xhttp.responseText);
            } else if (xhttp.readyState == 4 && xhttp.status == 500) {
                console.log(xhttp.responseText);
                window.alert(xhttp.responseText);
            }
        }

        xhttp.open("POST", "http://" + serverAddress + "rest/Table/sitDown?playerName=" + getPlayerName() + "&seatNum=" + seatNum, true);
        xhttp.setRequestHeader("Content-type", "text/plain");
        xhttp.send();
    }

    /**
     * Custom function to draw images synchronously after load.
     * @param ctx - context upon which to draw the image
     * @param imgSrc - URL of the image to draw
     * @param x - x location to draw the image in the context/canvas
     * @param y - y location to draw the image in the context/canvas
     * @param width - width the image should be drawn (optional)
     * @param height - height the image should be drawn (optional)
     * @returns {Promise<unknown>}
     */
    async drawImg(ctx, imgSrc, x, y, width, height) {
        return new Promise(resolve => {
            var img = new Image();
            img.onload = () => {
                if (typeof width === 'undefined') { ctx.drawImage(img, x, y); }
                else { ctx.drawImage(img, x, y, width, height); }
                resolve('resolved');
            }
            img.src = imgSrc;
        });
    }

    /**
     * Makes an AJAX call to the server to pull the latest table info.
     * Once the info is retrieved, calls drawTable() automatically.
     */
    retrieveTableInfo() {
        // Send request to the server for the latest table info
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {

                // Clear the playerInfo state
                this.setState({tableInfo: JSON.parse(xhttp.responseText)});

                // Pass the log info to the ActionLog component for display
                this.refs.actionLogComponent.updateLogInfo(this.state.tableInfo.log);
                this.refs.playerActionsComponent.updateTableInfo(this.state.tableInfo);

                // Draw the table based on the info just received.
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

    /**
     * Retrieve the canvas from the DOM and update it based on the current table info retrieved.
     */
    drawTable = () => {
        // Get the canvas and context from the DOM
        var canvas = document.getElementById("tableCanvas");
        var ctx = canvas.getContext("2d")

        // Add an event handler to handle click events on the canvas
        canvas.addEventListener('click', this.handleCanvasClickEvent);

        // Clear the canvas to start
        ctx.clearRect(0,0,canvas.width,canvas.height);

        // Draw the canvas from the bottom up
        this.drawBackground(ctx);
        this.drawTableImage(ctx);
        this.drawBoard(ctx);
        this.drawPot(ctx);
        this.drawAllSeats(ctx);
    }

    /**
     * Draw the backdrop for the table
     * @param ctx - context upon which to draw the backdrop
     */
    async drawBackground(ctx) {
        //await this.drawImg(ctx, "./graphics/background/red_texture_shadow_side.jpg", 0, 0, 1400, 800);

        let img = new Image();
        img.src = "./graphics/background/red_texture_shadow_side.jpg";
        //img.onload = () => {
            ctx.drawImage(img, 0, 0, 1400, 800);
        //}
    }

    /**
     * Draw the table image
     * @param ctx - context upon which to draw the table image
     */
    async drawTableImage(ctx) {
        //await this.drawImg(ctx, "./graphics/background/table-black.png", 100, 100, 1200, 600);

        let img = new Image();
        img.src = "./graphics/background/table-black.png";
        //img.onload = () => {
            const x_offset = 100;
            const y_offset = 100;
            ctx.drawImage(img, x_offset, y_offset, 1200, 600);
        //}
    }

    /**
     * Draw the board cards
     * @param ctx - context upon which to draw the board cards
     */
    drawBoard(ctx) {
        if (this.state.tableInfo == null) {
            // No table state retrieved. Just return.
            return;
        }

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
                img.onload = () => {
                    // Once the image is loaded, draw it on the canvas
                    ctx.drawImage(
                        img,
                        this.boardOffsets.x + (boardCount * (this.boardOffsets.cardWidth + this.boardOffsets.spacing)),
                        this.boardOffsets.y,
                        this.boardOffsets.cardWidth,
                        this.boardOffsets.cardHeight
                    );
                }
                img.src = "./graphics/" + cardFileName;
            }
        }
    }

    /**
     * Draw the pot information
     * @param ctx - context upon which to draw the pot information
     */
    drawPot(ctx) {
        if (this.state.tableInfo == null) {
            // No table state retrieved. Just return.
            return;
        }

        ctx.font = "15px Arial";
        ctx.fillStyle = "#ffffff";
        ctx.textAlign = "start";

        // Loop through each pot
        for (var i=0; i<this.state.tableInfo.potList.length; i++) {
            var pot = this.state.tableInfo.potList[i];
            // Draw the pot label and size
            ctx.fillText(
                "Pot #" + i + ": " + pot.potSize,
                 this.potOffsets.x + (this.potOffsets.potSpacing * i),
                 this.potOffsets.y
            );

            // Draw the names eligible for this pot
            for (var j=0; j<pot.seatNumberList.length; j++) {
                ctx.fillText(
                    this.state.tableInfo.seats[pot.seatNumberList[j]].player.playerName,
                    this.potOffsets.x + (this.potOffsets.potSpacing * i),
                    this.potOffsets.y + (this.potOffsets.nameSpacing * (j+1))
                );
            }
        }
    }

    /**
     * Draw all seats and their respective elements
     * @param ctx - context upon which to draw the seats
     * @returns {Promise<void>}
     */
    async drawAllSeats(ctx) {
        if (this.state.tableInfo == null) {
            // No table state retrieved. Just return.
            return;
        }

        // Loop to draw each seat
        for (let i = 0; i < 10; i++) {
            // drawSeat() returns a canvas, so draw it on the main canvas.
            // Pass the specific seat info.
            let seatCanvas = await this.drawSeat(this.state.tableInfo.seats[i]);

            // Create a new image, load the seat canvas into that, and draw it on the main table canvas.
            await this.drawImg(ctx, seatCanvas.toDataURL('image/png'), this.seatOffsets[i].x, this.seatOffsets[i].y);
        }
    }

    /**
     * Draw a single seat depending on the current table info. Returns a canvas that should be drown upon the main canvas.
     * @param seat - seat info to draw
     * @returns {Promise<HTMLCanvasElement>} - canvas to draw upon the main table canvas
     */
    async drawSeat(seat) {
        // Create a new canvas upon which to draw the seat elements
        var canvas = document.createElement('canvas');
        canvas.width = this.seatAreaWidth;
        canvas.height = this.seatAreaHeight;
        var ctx = canvas.getContext("2d");

        // TODO: temp border for seat canvas placement - REMOVE
        //ctx.strokeStyle = "white";
        //ctx.strokeRect(0, 0, canvas.width, canvas.height);

        // Check to see if the seat is open
        if (seat.player == null) {
            // Seat is open. If the player is not already sitting in a seat,
            // draw the open seat / sit down icon and return the canvas.
            if (this.playerSeatNum(getPlayerName()) < 0) {
                await this.drawImg(ctx, "./graphics/openSeat.png", 45, 50, 110, 100);
            }
            return canvas;
        }

        // There is a player in the seat.

        // Font style for the seat display
        ctx.font = "15px Arial";
        ctx.fillStyle = "#ffffff";
        ctx.textAlign = "start";

        // Offsets for this specific seat
        var seatElementOffsets = this.getSeatElementOffsets(seat.seatNum);

        //
        // Draw player's name
        //
        ctx.fillText(
            seat.player.playerName,
            seatElementOffsets.name.x,
            seatElementOffsets.name.y
        );

        //
        // Draw player stack
        //
        ctx.fillText(
            "Stack: " + seat.player.stack,
            seatElementOffsets.stack.x,
            seatElementOffsets.stack.y
        );

        //
        // Draw cards
        //
        const cardSpacing = 5;
        const cardWidth = 80;
        const cardHeight = 120;

        let imgURL = "";

        // Loop through the list of cards for the seat
        for (let i=0; i<seat.cards.length; i++) {
            let card = seat.cards[i];

            // Make sure the card isn't null (as it could be before a hand is dealt).
            if (card != null) {

                if (card.hidden){
                    // If the card is hidden, just show the card back image
                    imgURL = "./graphics/" + "back.png";
                } else {
                    imgURL = "./graphics/" + card.rank + "_" + card.suit + ".png";
                }

                // Use custom drawImg() function for synchronous draw
                await this.drawImg(
                    ctx,
                    imgURL,
                    seatElementOffsets.cards.x + (i * (cardWidth + cardSpacing)),
                    seatElementOffsets.cards.y,
                    cardWidth,
                    cardHeight);
            }
        }

        //
        // Draw special buttons (Dealer, SB, BB)
        //
        const buttonSize = 40;
        var buttonURL = "";

        if (this.state.tableInfo.dealerPosition == seat.seatNum) {
            buttonURL = "./graphics/Dealer.png";
        }
        else if (this.state.tableInfo.smallBlindPosition == seat.seatNum) {
            buttonURL = "./graphics/SmallBlind.png";
        }
        else if (this.state.tableInfo.bigBlindPosition == seat.seatNum) {
            buttonURL = "./graphics/BigBlind.png";
        }

        if (buttonURL != "") {
            await this.drawImg(
                ctx,
                buttonURL,
                seatElementOffsets.buttons.x,
                seatElementOffsets.buttons.y,
                buttonSize,
                buttonSize
            );
        }

        //
        // Draw All In button
        //
        const allinSize = 40;
        if (seat.isAllIn == true) {
            await this.drawImg(
                ctx,
                "./graphics/AllIn.png",
                seatElementOffsets.allin.x,
                seatElementOffsets.allin.y,
                allinSize,
                allinSize
            );
        }

        //
        // Draw current bet
        //
        if (seat.playerBet > 0) {
            ctx.fillText(
            "Bet: " + seat.playerBet,
                seatElementOffsets.bet.x,
                seatElementOffsets.bet.y
            );
        }

        //
        // Draw action indicator
        //
        if (this.state.tableInfo.currentAction == seat.seatNum) {
            ctx.strokeStyle = "yellow";
            ctx.lineWidth = 10;
            ctx.strokeRect(0, 0, canvas.width, canvas.height);
        }

        //
        // Draw winner indicator
        //
        const winnerBadgeSize = 40;
        for (let i=0; i<this.state.tableInfo.winningSeats.length; i++) {
            if (this.state.tableInfo.winningSeats[i] == seat.seatNum) {
                ctx.strokeStyle = "#00FF00";
                ctx.lineWidth = 10;

                ctx.strokeRect(0, 0, canvas.width, canvas.height);
                await this.drawImg(
                    ctx,
                    "./graphics/winner.png",
                    seatElementOffsets.winner.x,
                    seatElementOffsets.winner.y,
                    winnerBadgeSize,
                    winnerBadgeSize
                );
            }
        }

        return canvas;
    }

    /**
     * Main render function for the React component.
     * @returns {*} - returns the DOM elements to render on the page
     */
    render() {
            return (
            <div>
                <canvas id="tableCanvas" width={1400} height={800} style={{border: "2px solid #000000"}}>
                    Your browser does not support the HTML5 canvas tag.
                </canvas>
                <PlayerActions ref="playerActionsComponent"/>
                <ActionLog ref="actionLogComponent"/>
            </div>
        );
    }
}
