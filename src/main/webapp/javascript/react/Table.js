class Table extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            images: []
        }
    }

    componentDidMount() {
        let sources = [
            "./graphics/background/red_texture_shadow_side.jpg",
            "./graphics/background/table-black.png"
            //"./graphics/background/table-black.png"
        ];
        this.preloadImages(sources, this.state.images, this.drawTable);
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

    drawTable() {
        var canvas = document.getElementById("tableCanvas");
        var ctx = canvas.getContext("2d")

        ctx.clearRect(0,0,canvas.width,canvas.height);

        this.drawBackground(ctx);
        this.drawTableBackground(ctx);
        this.drawBoard(ctx);
        this.drawPot(ctx);
        //this.drawSeats(ctx);
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
        let img = new Image();
        img.src = "./graphics/back.png";
        img.onload = () => {
            const x_offset = 495;
            const y_offset = 250;
            const x_spacing = 85;
            const cardWidth = 80;
            const cardHeight = 120;

            // TODO - temp card back display below

            ctx.drawImage(img, x_offset, y_offset, cardWidth, cardHeight);
            ctx.drawImage(img, x_offset+(x_spacing), y_offset, cardWidth, cardHeight);
            ctx.drawImage(img, x_offset+(2*x_spacing), y_offset, cardWidth, cardHeight);
            ctx.drawImage(img, x_offset+(3*x_spacing), y_offset, cardWidth, cardHeight);
            ctx.drawImage(img, x_offset+(4*x_spacing), y_offset, cardWidth, cardHeight);
        }
    }

    drawPot(ctx) {
            const x_offset = 500;
            const y_offset = 400;

            // TODO

            ctx.font = "15px Arial";
            ctx.fillStyle = "#ffffff";
            ctx.textAlign = "start";
            ctx.fillText("Pot #1: 23456", x_offset, y_offset);
            ctx.fillText("PlayerA", x_offset, y_offset + 20);
            ctx.fillText("PlayerB", x_offset, y_offset + 40);
    }

    drawSeats(ctx) {
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
