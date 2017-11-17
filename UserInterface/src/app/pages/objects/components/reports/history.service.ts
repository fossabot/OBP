import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/auth.service';

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';
import 'rxjs/add/observable/throw';

import { environment } from '../../../../../environments/environment';

@Injectable()
export class HistoryService {



  // The API Gateway is on obp-02.  The raw service is on obp-05.
  private apiRoot = environment.apiRoot || 'https://obp-02.esl.saic.com:8443';


  private authService: AuthService;

  private dateRanges = [{ name: 'Most Recent', value: 'none' },
                        { name: '1 hour', value: '1hr' },
                        { name: '1 day', value: '1day' },
                        { name: '7 days', value: '7days' },
                        { name: '30 days', value: '30days' },
                        { name: 'All', value: 'all' }];

  constructor(private http: Http,
              private router: Router ) {

    this.authService = new AuthService(this.http, this.router);
  }

  getObjectHistory(objectType: string, objectId: string, timerange: string): Observable<any> {
    const url = `${this.apiRoot}/history/${objectType}/${objectId}?`
      + `timerange=${timerange}&jwt=${this.authService.getToken()}`;
    return this.http.get(url, this.authService.enhanceConfig())
      .map((res: Response) => res.json())
      .do(data => console.log('All: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }
  getObjectLocationHistory(objectType: string, objectId: string, timerange: string): Observable<any> {
    const url = `${this.apiRoot}/history/${objectType}/${objectId}/location?`
      + `timerange=${timerange}&jwt=${this.authService.getToken()}`;
    return this.http.get(url, this.authService.enhanceConfig())
      .map((res: Response) => res.json())
      .do(data => console.log('All: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }
  getDateRanges(): any {
    return this.dateRanges;
  }

  handleError(error: Response | any) {
      console.error(error);

      if (error.status === 0) {
        sessionStorage.removeItem('authToken');
        this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
        return Observable.empty();
      }
      return Observable.throw(error.json().error || 'Server error');
   }
}
