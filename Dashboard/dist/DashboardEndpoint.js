"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class DashboardEndpoint {
    constructor(logger, socketIO) {
        this.logger = logger;
        this.socketIO = socketIO;
    }
    routes(app) {
        app.route('/traffic-report')
            .post((req, res) => {
            let newSensorData = req.body;
            this.logger.debug(`Received new traffic sensor data: ${JSON.stringify(newSensorData, null, 4)}`);
            this.socketIO.emit('newSensorData', newSensorData);
            res.status(200).send(newSensorData);
        });
        app.route('/most-occupied-roads')
            .post((req, res) => {
            let newRoadOccupancyData = req.body;
            this.logger.debug(`Received new road occupancy data: ${JSON.stringify(newRoadOccupancyData, null, 4)}`);
            this.socketIO.emit('newRoadOccupancyData', newRoadOccupancyData);
            res.status(200).send(newRoadOccupancyData);
        });
    }
}
exports.DashboardEndpoint = DashboardEndpoint;
//# sourceMappingURL=DashboardEndpoint.js.map