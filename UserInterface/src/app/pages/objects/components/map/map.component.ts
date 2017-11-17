import { Component, OnInit, Input } from '@angular/core';
import { MapService } from './map.service';
import { LatLng } from './map.latlng';
import { Locations } from './map.locations';
import { MouseEvent } from 'leaflet';
import { Router } from '@angular/router';

@Component({
    selector: 'oms-map',
    templateUrl: './map.component.html',
    styleUrls: ['./map.scss'],
})

export class MapComponent implements OnInit {
    title: string = 'Map';
    errorMessage: string;
    fullScreen: boolean;
    locationsString: string;

    constructor(private mapService: MapService,
        private locations: Locations,
        private router: Router) {
            this.fullScreen = false;
    }

    ngOnInit() {
        let numMarkers: number = 0;

        // Assign the local storage variable to a local variable
        const locLocations = window.localStorage.getItem('locations');

        // If the local storage variable exists, assign it to the location object
        if (locLocations !== null) {
            // If there is a local storage variable, it means we should open the map in a new window
            this.fullScreen = true;

            // Hide the components around the map so it is the only component visible
            $('#mapWrapper').css('width', '100%');
            $('#mapWrapper').css('height', '100%');
            $('#map').css('height', '100vh');
            $('.al-content').css('padding', '0px');
            $('.card-body').css('padding', '0px');
            $('ba-content-top').hide();

            // Assign the location to the locations object
            this.locations = JSON.parse(locLocations);

            // Clear the local storage item
            window.localStorage.removeItem('locations');
        }

        // If the locations object is populated, load the map 
        if (this.locations.latlongs !== undefined && this.locations.latlongs[0].lat !== undefined) {
            this.locationsString = JSON.stringify(this.locations);
            numMarkers = this.locations.latlongs.length;

            const map = L.map('map', {
                zoomControl: false,
                center: L.latLng(this.locations.latlongs[0].lat, this.locations.latlongs[0].lng),
                zoom: 3,
                minZoom: 1,
                maxZoom: 20,
                layers: [this.mapService.baseMaps.OpenStreetMap],
            });

            L.Icon.Default.imagePath = 'assets/img/theme/vendor/leaflet';
            const shipIcon = L.icon({
                iconUrl: 'assets/img/ship.png',

                iconSize: [40, 40], // size of the icon
                // iconAnchor:   [-1, -1], // point of the icon which will correspond to marker's location
                // popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
            });

            const markerArray = [];
            for (let i = 0; i < numMarkers; i++) {
                if (this.locations.latlongs[i].type === 'Ship') {
                    markerArray.push(L.marker([this.locations.latlongs[i].lat, this.locations.latlongs[i].lng], 
                    { icon: shipIcon }));
                    L.marker([this.locations.latlongs[i].lat, this.locations.latlongs[i].lng], 
                    { icon: shipIcon }).addTo(map).bindPopup(this.locations.latlongs[i].name);
                } else {
                    markerArray.push(L.marker([this.locations.latlongs[i].lat, this.locations.latlongs[i].lng]));
                    L.marker([this.locations.latlongs[i].lat, 
                    this.locations.latlongs[i].lng]).addTo(map).bindPopup(this.locations.latlongs[i].name);
                }
            }

            const group = L.featureGroup(markerArray);
            map.fitBounds(group.getBounds());
            if (numMarkers === 1) {
                map.zoomOut(12);
            }

            L.control.zoom({ position: 'topright' }).addTo(map);
            L.control.layers(this.mapService.baseMaps).addTo(map);
            L.control.scale().addTo(map);

            this.mapService.map = map;

            //    map.invalidateSize(false);

            L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>, Tiles courtesy of <a href="http://hot.openstreetmap.org/" target="_blank">Humanitarian OpenStreetMap Team</a>',
            }).addTo(map);
        }
    }

    openMap(location: LatLng, mapUrl: string): void {
        if (location !== undefined && location !== null) {
            this.locations.latlongs = [];
            this.locations.latlongs.push(location);
            this.router.navigate([mapUrl]);
        } else {
            this.errorMessage = 'No location information exists to open the map';
        }
    }

    openFullScreen(locations: string){
        window.localStorage.setItem('locations', locations);
        window.open('#/viewerpage/objects/map');
    }
}
