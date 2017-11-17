import {Injectable} from '@angular/core';
import {LatLng} from './map.latlng';

@Injectable()
export class Locations {
    public latlongs: LatLng[];
    public constructor() {}
}
