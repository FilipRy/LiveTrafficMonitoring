import {App} from "./App";
import {Server} from "http";
import * as SocketIO from "socket.io";

export const PORT = 9000;

let main: App = new App();

main.express.use('/api/v1', main.router);

let server: Server = main.express.listen(PORT, () => {
    console.log('listening on port ' + PORT);
    let io: SocketIO.Server = SocketIO(server);
    main.run(io);
});