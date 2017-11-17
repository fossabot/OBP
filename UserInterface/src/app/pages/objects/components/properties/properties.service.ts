import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/auth.service';

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/observable/throw';

import { environment } from '../../../../../environments/environment';


@Injectable()
export class PropertiesService {

  // The raw services are on obp-05 and have no authentication enabled.
  // The API gateway is on obp-02 and does have authentication enabled.
  // Eventually, the raw services will be put behind firewall but for now we can use them for debugging.
  private apiRoot = environment.apiRoot || 'https://obp-02.esl.saic.com:8443';
  private serviceRoot = environment.propertiesServiceRoot || 'https://obp-05.esl.saic.com:8085';
  private authService: AuthService;

  constructor(
    private http: Http,
    private router: Router) {
      this.authService = new AuthService(this.http, this.router);
  }

  getDictionary(): Observable<any> {
    const url = `${this.apiRoot}/dictionary/?jwt=${this.authService.getToken()}`;

    return this.http.get(url, this.authService.enhanceConfig())
      .map((res: Response) => res.json())
      .do(data => console.log('All: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }

  getDictionaryItem(id: string): Observable<any> {
    const url = `${this.apiRoot}/dictionary/id?jwt=${this.authService.getToken()}`;

    return this.http.get(url, this.authService.enhanceConfig())
      .map((res: Response) => res.json())
      .do(data => console.log('All: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }

  setDictionaryItem(field: string, classes: string[], dataType: string,
                    core: boolean, required: boolean): Observable<any> {
    const headers = new Headers({ 'Content-Type': 'application/json' });
    const options = new RequestOptions({ headers });

    const dictionaryItem = { 'field' : field, 'classes' : classes, 'dataType' : dataType,'core' : core,'required' : required};
    console.log(dictionaryItem);
    console.log(JSON.stringify(dictionaryItem));

    return this.http.post(`${this.apiRoot}/dictionary/?jwt=${this.authService.getToken()}`,
      JSON.stringify(dictionaryItem), this.authService.enhanceConfig(options))
      .map(res => res.json());
  }

  setDictionary( newDictionary: any ): Observable<any> {
    const headers = new Headers({ 'Content-Type': 'application/json' });
    const options = new RequestOptions({ headers });

    return this.http.put(`${this.apiRoot}/dictionary/?jwt=${this.authService.getToken()}`,
      JSON.stringify(newDictionary), this.authService.enhanceConfig(options))
      .map(res => res.json());
  }

   handleError(error: Response) {
      console.error(error);
      return Observable.throw(error.json().error || 'Server error');
   }
}
