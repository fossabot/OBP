import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/auth.service';

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/observable/throw';

import { IAvailableRoles } from './availableroles';

@Injectable()

export class UserRoleAdminService {

  // The API Gateway is on obp-02.  The raw service is on obp-05.
  //private apiRoot = 'https://obp-02.esl.saic.com:8443';
  private serviceRoot = 'https://obp-05.esl.saic.com:8082';

  private authService: AuthService
  private _availahlerolesurl = 'app/pages/userroles/availableuserroles.json';

  constructor(private http: Http, private router: Router ) {
    this.authService = new AuthService(this.http, this.router);
  }

  getAvailableRoles(): Observable<IAvailableRoles[]> {
    return this.http.get(this._availahlerolesurl)
      .map((response: Response) => <IAvailableRoles[]>response.json())
      .do(data => console.log('All: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }

  updateUserRole(username: string, role: string): Observable<any> {
    const headers = new Headers({ 'Content-Type': 'application/json' });
    const options = new RequestOptions({ headers });
    const url = `${this.serviceRoot}/user/${username}?jwt=${this.authService.getToken()}`;
    return this.http.put(url, { role: role }, this.authService.enhanceConfig(options))
      .map((res: Response) => res.json())
      .catch(this.handleError);
  }

  // private extractData(res: Response) {
  //   let body =  res.json();
  //   return body || {};
  // }
  handleError(error: Response | any) {
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    return Observable.throw(errMsg);
  }

}
