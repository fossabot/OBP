import { Component, Input, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';

import { SearchService } from '../search/search.service';
import { LatLng } from '../map/map.latlng';
import { Locations } from '../map/map.locations';
import { MapComponent } from '../map/map.component';
import { OmsService } from '../shared/oms.service';
import { AuthService } from '../../../auth/auth.service';

import { GlobalState } from '../../../../global.state';

@Component({
  selector: 'search-results',
  templateUrl: './searchresults.component.html',
  styleUrls: ['./searchresults.component.css'],
  providers: [MapComponent],
  encapsulation: ViewEncapsulation.None,
})

export class SearchResultsComponent {
  listFilter: string;
  @Input() response: any;
  errorMessage: string;
  successMessage: string;
  mapUrl: string = '/pages/objects/map';
  canDelete: boolean;
  canEdit: boolean;
  constructor(private searchService: SearchService,
    private router: Router,
    private locations: Locations,
    private omsService: OmsService,
    private map: MapComponent,
    private authService: AuthService,
    private state: GlobalState,
  ) {
  }

  search(searchString: string): void {
    this.errorMessage = '';
    if (searchString === undefined || searchString === '') {
      this.errorMessage = 'Please enter a search criteria.';
    } else {
      this.searchService.searchBasic(searchString)
        .subscribe(data => {
          this.response = this.mergeResults(data);
        },
        error => this.errorMessage = <any>error);
    }
  }

  isValidLocation(loc: any): boolean {
    return loc !== undefined 
      && loc !== null
      && loc.lat !== undefined
      && loc.lat !== null 
      && loc.lon !== undefined
      && loc.lon !== null;
  }
  mergeResults(data) {
    let agg = [];
    Object.keys(data).forEach(function (fieldName) {
      data[fieldName].forEach(function (elem) {
        elem.clazz = fieldName;
      });
      agg = agg.concat(data[fieldName]);
    });
    return agg;
  }

  openMap(location: any, name: string, type: string): void {
    const loc = new LatLng();
    loc.lat = location.lat;
    loc.lng = location.lon;
    loc.name = name;
    loc.type = type;
    this.map.openMap(loc, this.mapUrl);
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

  private onDelete(data: any) {
    // this.router.navigate(['pages/objects']);
  }

  private onDeleteError(error: any) {
    this.state.notifyDataChanged('message.error', 'Could not delete user.');
    // alert('Could not delete user.');
    console.log(error);
  }

  private onDeleteComplete() {
    this.state.notifyDataChanged('message.info', 'Object deleted.');
    // alert('Object deleted');
  }

  deleteObject(obj): void {
    this.state.notifyDataChanged('message.info', '');
    this.state.notifyDataChanged('message.error', '');
    
    const abc = this;
    if (obj.id) {
      if (confirm(`Are you sure you want to delete ${obj.name}?`)) {
        this.omsService.deleteObject(obj).subscribe({
          next: (value) => this.onDelete(value),
          error: (error) => this.onDeleteError(error),
          complete: () => this.onDeleteComplete(),
        });
        
        const index: number = this.response.indexOf(obj);
        if (index !== -1) {
            this.response.splice(index, 1);
        }
      }
    }
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
  }

}
