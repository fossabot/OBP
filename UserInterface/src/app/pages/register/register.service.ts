import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Router } from '@angular/router';

import 'rxjs/add/operator/map';

@Injectable()

export class RegisterUserService {

  // The API Gateway is on obp-02.  The raw service is on obp-05.
  //private apiRoot = 'https://obp-02.esl.saic.com:8443';
  private serviceRoot = 'https://obp-05.esl.saic.com:8082';


  constructor(private http: Http, private router: Router ) {
  }

  createUser(username: string, password: string): Observable<any> {
    const headers = new Headers({ 'Content-Type': 'application/json' });
    const options = new RequestOptions({'headers': headers });

    const url = `${this.serviceRoot}//user`;
    const payload = {
      username: username,
      password: password
    };
    const jsonBody: string = JSON.stringify(payload);
    return this.http.post(url, jsonBody, options)
      .map((res: Response) => res.json())
      .catch(this.handleError);
  }

  private handleError(error: any) {
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    return Observable.throw(errMsg);
  }
}

