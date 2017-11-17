import { Component, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';

import { SearchService } from './search.service';
import { LatLng } from '../map/map.latlng';
import { Locations } from '../map/map.locations';
import { OmsService } from '../shared/oms.service';
import { AuthService } from '../../../auth/auth.service';
import { GlobalState } from '../../../../global.state';
import * as moment from 'moment';

@Component({
  selector: 'oms-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  encapsulation: ViewEncapsulation.None,
})

export class SearchComponent {
  listFilter: string;
  response: any;
  suggestions: any;
  errorMessage: string;
  successMessage: string;
  mapUrl: string = '/pages/objects/map';

  canDelete: boolean;
  canEdit: boolean;

  constructor(private searchService: SearchService,
              private router: Router,
              private locations: Locations,
              private omsService: OmsService,
              private authService: AuthService,
              private state: GlobalState) {
  }

  search(searchString: any): void {
    this.errorMessage = '';
    let query = '';

    // Clear messages
    this.state.notifyDataChanged('message.info', '');
    this.state.notifyDataChanged('message.error', '');

    if (this.listFilter === undefined || this.listFilter === undefined || this.listFilter.trim() === '') {
      this.state.notifyDataChanged('message.info', 'Please enter a search criteria.');
    } else {

      if (searchString.query !== undefined) {
        query = searchString.query;
      } else {
        query = searchString;
      }

      // Replace mm/dd/yyyy with yyyy-mm-dd
      query = query.replace(/(0[1-9]|1[012])\/(0[1-9]|[12][0-9]|3[01])\/((19|20)\d\d)/g, '$3-$1-$2');

      // Replace any remaining slashes with spaces
      query = query.replace(/\//g, ' ');


      this.searchService.searchBasic(query)
        .subscribe(data => {
            this.response = this.mergeResults(data);
          },
          error => {
            this.errorMessage = <any>error;
            this.state.notifyDataChanged('message.error', 'There was an issue searching.  Please try again.');
            // alert('There was an issue searching.  Please try again.');
          });
    }
  }

  mergeResults(data) {
    let agg = [];
    const dataset = data.data[0];
    Object.keys(dataset).forEach(function (fieldName) {
      dataset[fieldName].forEach(function (elem) {
        elem.clazz = fieldName;
      });
      agg = agg.concat(dataset[fieldName]);
    });
    return agg;
  }

  openMap(location: any, name: string, type: string): void {
    const latlng: LatLng = new LatLng();
    latlng.name = name;
    latlng.type = type;
    if (location !== undefined && location !== null) {
      this.locations.latlongs = [];
      latlng.lat = Number(location.lat);
      latlng.lng = Number(location.lon);
      this.locations.latlongs.push(latlng);
      this.router.navigate([this.mapUrl]);
    } else {
      this.errorMessage = 'No location information exits to open the map';
    }
  }

  openMapSelected(): void {
    this.locations.latlongs = this.getLatLongs();

    if (this.locations.latlongs.length > 0) {
      this.router.navigate([this.mapUrl]);
    } else {
      this.errorMessage = 'No data with coordinates has been selected.';
    }
  }

  getLatLongs() {
    let latLongs = [];
    this.response.forEach(function (e) {
      if (e.selected && e.location !== undefined && e.location !== null) {
        const location: LatLng = new LatLng();
        location.lat = Number(e.location.lat);
        location.lng = Number(e.location.lon);
        location.name = e.name;
        location.type = e.type;
        latLongs.push(location);
      }
    });
    return latLongs;
  }

  toggle(isSelected: boolean) {
    if (this.response !== undefined) {
      this.response.forEach(function (o) {
        o.selected = isSelected;
      });
    }
  }

  ngOnInit() {
    const hasRWCD: boolean = this.authService.hasRoleOrAdmin(this.authService.ROLE_RWCD);
    const hasRWO: boolean = this.authService.hasRoleOrAdmin(this.authService.ROLE_RWO);

    this.canEdit = hasRWCD || hasRWO;
    this.canDelete = hasRWCD;

    this.state.notifyDataChanged('title.change', 'Basic Search');
  }
}
