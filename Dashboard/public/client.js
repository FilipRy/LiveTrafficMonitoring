function init() {

    let roadPolylines = [];
    let currentTime = null;

    let timeDateEl = document.getElementById("time-date");
    let occupiedRoadsTableEl = document.getElementById("roads-used-table-body");

    let socket = io();

    let map = new google.maps.Map(document.getElementById('map'), {
        zoom: 12,
        center: {lat: 56.163147, lng: 10.2013813},
        mapTypeId: 'roadmap'
    });

    socket.on('newSensorData', function (msg) {

        let roadSegmentCoordinates = [
            {lat: msg.startPointLatitude, lng: msg.startPointLongitude},
            {lat: msg.endPointLatitude, lng: msg.endPointLongitude}
        ];

        if (roadPolylines[msg.reportId]) {
            roadPolylines[msg.reportId].setMap(null);
        }

        let color = '';
        if (msg.vehicleCount < 10) {
            color = '#008000';
        }
        else if (msg.vehicleCount < 30) {
            color = '#FFFF00';
        } else {
            color = '#FF0000';
        }

        let roadSegment = new google.maps.Polyline({
            path: roadSegmentCoordinates,
            geodesic: true,
            strokeColor: color,
            strokeOpacity: 1.0,
            strokeWeight: 1.5
        });

        roadSegment.setMap(map);

        roadPolylines[msg.reportId] = roadSegment;

        if (msg.timestamp != currentTime) {
            let date = new Date(msg.timestamp[0], msg.timestamp[1], msg.timestamp[2], msg.timestamp[3], msg.timestamp[4], 0, 0);
            timeDateEl.innerHTML = 'Time: ' + date.toLocaleString();
            currentTime = msg.timestamp;
        }

    });

    socket.on('newRoadOccupancyData', function (roadsOccupancyData) {

        while (occupiedRoadsTableEl.firstChild) {
            occupiedRoadsTableEl.removeChild(occupiedRoadsTableEl.firstChild);
        }

        let i = 1;
        for (let j = roadsOccupancyData.length - 1; j >= 0; j--) {

            let roadOccupancyEntry = roadsOccupancyData[j];

            let tableEntry = document.createElement('tr');

            let headEntry = document.createElement('th');
            headEntry.scope = 'row';
            headEntry.innerHTML = i++;
            let streetNameEntry = document.createElement('td');
            streetNameEntry.innerHTML = roadOccupancyEntry.roadName;
            let occupiedDataEntry = document.createElement('td');
            occupiedDataEntry.innerHTML = roadOccupancyEntry.numberOfVehiclesPerDay;

            tableEntry.appendChild(headEntry);
            tableEntry.appendChild(streetNameEntry);
            tableEntry.appendChild(occupiedDataEntry);

            occupiedRoadsTableEl.appendChild(tableEntry);
        }

    });
}