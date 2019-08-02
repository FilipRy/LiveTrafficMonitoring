import {DependencyInjection} from "./DependencyInjection";
import {Logger} from "log4js";
import {DashboardEndpoint} from "./DashboardEndpoint";

import * as express from 'express';
import * as bodyParser from 'body-parser';
import * as SocketIO from "socket.io";


export class App {

    public router: express.Router;
    public express: express.Application;

    private dependencyInjection: DependencyInjection;
    private logger: Logger;

    private dashboardEndpoint: DashboardEndpoint;

    constructor() {
        this.express = express();
        this.router = express.Router();
        this.config();
    }

    async run(io: SocketIO.Server) {
        this.dependencyInjection = new DependencyInjection();
        this.logger = this.dependencyInjection.createLogger();

        this.dashboardEndpoint = this.dependencyInjection.createDashboardEndpoint(io);
        this.dashboardEndpoint.routes(this.router);
    }

    private config(): void {
        // support application/json type post data

        let bodyParserCustom = {
            json: {limit: '50mb', extended: true},
            urlencoded: {limit: '50mb', extended: true}
        };

        this.express.use(bodyParser.json({
            limit: '50mb'
        }));

        this.express.use(bodyParser.text({
            limit: '50mb'
        }));

        //support application/x-www-form-urlencoded post data
        this.express.use(bodyParser.urlencoded({
            limit: '50mb',
            extended: true,
            parameterLimit: 1000000
        }));

        this.express.use(express.static('public'));
    }
}