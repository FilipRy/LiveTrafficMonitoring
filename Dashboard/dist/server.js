"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const App_1 = require("./App");
const SocketIO = require("socket.io");
exports.PORT = 9000;
let main = new App_1.App();
main.express.use('/api/v1', main.router);
let server = main.express.listen(exports.PORT, () => {
    console.log('listening on port ' + exports.PORT);
    let io = SocketIO(server);
    main.run(io);
});
//# sourceMappingURL=server.js.map