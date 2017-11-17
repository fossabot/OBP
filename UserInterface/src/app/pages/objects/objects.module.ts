import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SearchComponent } from './components/search/search.component';
import { UserRoleRequestAdminComponent } from './components/userrolechange/userrolechange.component';
import { AdvancedSearchComponent } from './components/advancedsearch/advancedsearch.component';
import { SearchResultsComponent } from './components/searchresults/searchresults.component';
import { AutoCompleteComponent } from './components/autocomplete/autocomplete.component';
import { CrudComponent } from './components/crud/crud.component';
import { ReportsComponent } from './components/reports/reports.component';
import { LocationsComponent } from './components/locations/locations.component';
import { ObjectviewComponent } from './components/objectview/objectview.component';
import { SearchService } from './components/search/search.service';
import { HistoryService } from './components/reports/history.service';
import { UserRoleAdminService } from './components/userrolechange/userrolechange.service';
import { PropertiesService } from './components/properties/properties.service';
import { OmsService } from './components/shared/oms.service';
import { PropertiesComponent } from './components/properties/properties.component';
import { PropertyComponent } from './components/properties/property.component';
import { VisualizeComponent } from './components/visualize/visualize.component';
import { Tabs } from './components/tabs/tabs';
import { Tab } from './components/tabs/tab';
import { RelationshipComponent } from './components/relationship/relationship.component';

import 'leaflet';
import { MapComponent } from './components/map/map.component';
import { Locations } from './components/map/map.locations';
import { LatLng } from './components/map/map.latlng';
import { MapService } from './components/map/map.service';

// Map uses the ba-card
import { NgaModule } from '../../theme/nga.module';

// Properties uses the bootstrap dropdown
import { NgbDropdownModule, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';

import { AutoCompleteModule } from 'primeng/primeng';

export const routes = [
    { path: '', component: SearchComponent, pathMatch: 'full' },
    { path: 'search/:filter', component: SearchComponent },
    { path: 'search', component: SearchComponent },
    { path: 'relationship', component: RelationshipComponent },
    { path: 'useradmin', component: UserRoleRequestAdminComponent },
    { path: 'advancedsearch/:filter', component: AdvancedSearchComponent },
    { path: 'advancedsearch', component: AdvancedSearchComponent },
    { path: 'searchresults', component: SearchResultsComponent },
    { path: 'map', component: MapComponent, pathMatch: 'full' },
    { path: 'properties', component: PropertiesComponent },
    { path: 'property', component: PropertyComponent },
    { path: 'visualize', component: VisualizeComponent },
    { path: 'visualize/:clazz/:id', component: VisualizeComponent },
    { path: ':clazz', component: ObjectviewComponent },
    { path: ':clazz/:id', component: ObjectviewComponent },
    { path: '**', redirectTo: 'search' },
];

@NgModule({
    declarations: [
        SearchComponent,
        UserRoleRequestAdminComponent,
        AdvancedSearchComponent,
        SearchResultsComponent,
        AutoCompleteComponent,
        CrudComponent,
        ReportsComponent,
        LocationsComponent,
        MapComponent,
        PropertiesComponent,
        PropertyComponent,
        VisualizeComponent,
        ObjectviewComponent,
        Tabs,
        Tab,
        RelationshipComponent,
    ],
    imports: [
        CommonModule,
        FormsModule,
        HttpModule,
        RouterModule.forChild(routes),
        NgaModule,
        ReactiveFormsModule,

        // Properties page uses the drop downs.
        NgbDropdownModule,
        NgbModalModule,
        AutoCompleteModule,
    ],
    exports: [
      ReactiveFormsModule,
    ],
    providers: [
        OmsService,
        SearchService,
        HistoryService,
        UserRoleAdminService,
        PropertiesService,
        PropertyComponent,
        LatLng,
        Locations,
        MapService,
    ],
})
export class ObjectsModule {}
