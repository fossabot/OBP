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
export class SearchService {



  // The API Gateway is on obp-02.  The raw service is on obp-05.
  private apiRoot = environment.apiRoot || 'https://obp-02.esl.saic.com:8443';
  private serviceRoot = environment.searchServiceRoot || 'https://obp-05.esl.saic.com:8084';

  private authService: AuthService;

  constructor(private http: Http,
              private router: Router ) {

    this.authService = new AuthService(this.http, this.router);
  }

  searchBasic(searchString: string): Observable<any> {
    const url = `${this.apiRoot}/search/${searchString}?jwt=${this.authService.getToken()}`;
    return this.http.get(url, this.authService.enhanceConfig())
      .map((res: Response) => res.json())
      .do(data => console.log('All: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }

  searchAdvanced(searchString: string): Observable<any> {
    const url = `${this.serviceRoot}/search_advanced/${searchString}?jwt=${this.authService.getToken()}`;
    return this.http.get(url, this.authService.enhanceConfig())
      .map((res: Response) => res.json())
      .do(data => console.log('All: ' + JSON.stringify(data)))
      .catch(this.handleError);
  }

  searchSuggestion(searchField: string, searchString: string): Observable<any> {
    // create the search_logic object
    const query = `{ "${searchField}" : { "$regex" : "${searchString}" , "$options" : "i" }}`;
    return this.searchLogic(query);
  }

  searchLogic(searchString: string): Observable<any> {
    const url = `${this.serviceRoot}/search_logic/${searchString}?jwt=${this.authService.getToken()}`;
    return this.http.get(url, this.authService.enhanceConfig())
      .map((res: Response) => res.json())
      .do(data => console.log('All: ' + JSON.stringify(data)))
      .catch(this.handleError);
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
