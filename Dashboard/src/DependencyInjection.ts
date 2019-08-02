import {Logger} from "log4js";
import * as log4js from "log4js";
import {DashboardEndpoint} from "./DashboardEndpoint";
import * as SocketIO from "socket.io";

export class DependencyInjection {


    private logger: Logger;
    private dashboardEndpoint: DashboardEndpoint;

    public createDashboardEndpoint(socketIO: SocketIO.Server): DashboardEndpoint {
        if (this.dashboardEndpoint) {
            return this.dashboardEndpoint;
        }
        this.dashboardEndpoint = new DashboardEndpoint(this.createLogger(), socketIO);
        return this.dashboardEndpoint;
    }

    public createLogger(): Logger {
        if (this.logger) {
            return this.logger;
        }
        log4js.configure('config/log4js.json');
        this.logger = log4js.getLogger('default');
        return this.logger;
    }

}