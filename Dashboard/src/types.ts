

export interface TrafficSensorData {
    id: number,
    reportId: number,
    status: string,
    vehicleCount: number,
    avgSpeed: number,
    timestamp: Date,
    startPointLatitude: number,
    startPointLongitude: number,
    endPointLatitude: number,
    endPointLongitude: number
}


export interface RoadOccupancy {
    roadName: string,
    reportId: number,
    timestamp: Date,
    numberOfVehiclesPerDay: number
}