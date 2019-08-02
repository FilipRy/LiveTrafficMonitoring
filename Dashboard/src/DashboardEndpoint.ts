import * as express from "express";
import {RoadOccupancy, TrafficSensorData} from "./types";
import {Request} from "express";
import {Response} from "express";
import {Logger} from "log4js";
import * as SocketIO from "socket.io";

export class DashboardEndpoint {

    private logger: Logger;
    private socketIO: SocketIO.Server;

    constructor(logger: Logger, socketIO: SocketIO.Server) {
        this.logger = logger;
        this.socketIO = socketIO;
    }

    public routes(app: express.Router): void {

        app.route('/traffic-report')
            .post((req: Request, res: Response) => {
                let newSensorData: TrafficSensorData = req.body;
                this.logger.debug(`Received new traffic sensor data: ${JSON.stringify(newSensorData, null, 4)}`);
                this.socketIO.emit('newSensorData', newSensorData);
                res.status(200).send(newSensorData);
            });

        app.route('/most-occupied-roads')
            .post( (req: Request, res: Response) => {
                let newRoadOccupancyData: RoadOccupancy[] = req.body;
                this.logger.debug(`Received new road occupancy data: ${JSON.stringify(newRoadOccupancyData, null, 4)}`);
                this.socketIO.emit('newRoadOccupancyData', newRoadOccupancyData);
                res.status(200).send(newRoadOccupancyData);
            });
    }
}