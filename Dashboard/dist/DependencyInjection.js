"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const log4js = require("log4js");
const DashboardEndpoint_1 = require("./DashboardEndpoint");
class DependencyInjection {
    createDashboardEndpoint(socketIO) {
        if (this.dashboardEndpoint) {
            return this.dashboardEndpoint;
        }
        this.dashboardEndpoint = new DashboardEndpoint_1.DashboardEndpoint(this.createLogger(), socketIO);
        return this.dashboardEndpoint;
    }
    createLogger() {
        if (this.logger) {
            return this.logger;
        }
        log4js.configure('config/log4js.json');
        this.logger = log4js.getLogger('default');
        return this.logger;
    }
}
exports.DependencyInjection = DependencyInjection;
//# sourceMappingURL=DependencyInjection.js.map