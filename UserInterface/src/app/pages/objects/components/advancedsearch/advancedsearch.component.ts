import { Component, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { OmsService } from '../shared/oms.service';
import { SearchService } from '../search/search.service';
import { LatLng } from '../map/map.latlng';
import { Locations } from '../map/map.locations';
import { Util } from '../shared/Util';
import { GlobalState } from '../../../../global.state';

@Component({
  templateUrl: './advancedsearch.component.html',
  styleUrls: ['./advancedsearch.component.css'],
  encapsulation: ViewEncapsulation.None,
})

export class AdvancedSearchComponent {
  listFilter: string;
  response: any;
  errorMessage: string;
  successMessage: string;
  mapUrl: string = '/pages/objects/map';
  fields: string[];
  model: any;
  clazz: string;
  dateFields: string[];

  camelToReg = Util.camelToReg;

  private undefinedProtect: any = { location: {} }; // any nestable properties go here, with sensible default

  constructor(private searchService: SearchService,
              private omsService: OmsService,
              private route: ActivatedRoute,
              private router: Router,
              private locations: Locations,
              private state: GlobalState) {
  }

  ngOnInit() {
    this.model = jQuery.extend({}, this.undefinedProtect);
    this.omsService.getAllProperties().subscribe(o => this.getFields(o));
    this.dateFields = ['startDate', 'endDate', 'dateOfBirth', 'dateBuilt'];
    this.state.notifyDataChanged('title.change', 'Advanced Search');
  }

  getFields(props: string[]) {
    const theFields = [];
    props.forEach(prop => {
      if (prop !== 'id' && prop !== 'lastUpdated') {
        theFields.push(prop);
      }
    });
    this.fields = theFields;
  }

  private assignReqDefaultValues() {
    Object.keys(this.undefinedProtect).forEach(field => {
        if (this.model[field] === undefined || this.model[field] === null) {
          this.model[field] = this.undefinedProtect[field];
        }
    });
  }

  private removeClazzField() {
    const clazzIndex = this.fields.indexOf('clazz');
    if (clazzIndex > -1) {
      this.fields.splice(clazzIndex, 1);
    }
  }

  private getSearchCriteria() {
    const searchCriteria = {};
    let value = '';

    for ( const key in this.model ) {
      if ( this.model.hasOwnProperty( key )) {
        value = this.model[key];

        if ( value && value.length > 0 ) {
          searchCriteria[key] = value;
        }
      }
    }

    return searchCriteria;
  }

  search(): void {
    const searchCriteria = this.getSearchCriteria();
    const searchCriteriaString = JSON.stringify(searchCriteria);

    // Clear messages
    this.state.notifyDataChanged('message.info', '');
    this.state.notifyDataChanged('message.error', '');

    if (searchCriteriaString === '{}') {
      this.state.notifyDataChanged('message.info', 'Please enter a search criteria.');
      // alert('Please enter a search criteria');
    } else {
      this.searchService.searchAdvanced(searchCriteriaString)
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
    const latLongs = [];
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
}
