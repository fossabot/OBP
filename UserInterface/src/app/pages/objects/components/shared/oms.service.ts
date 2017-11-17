import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/auth.service';

import 'rxjs/add/operator/map';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../../../environments/environment';

@Injectable()
export class OmsService {
  
  private apiRoot: string = environment.apiRoot || 'https://obp-02.esl.saic.com:8443';  // API Gateway
  private dictionaryRoot: string = this.apiRoot;
  private optionsUrl: string = '/options';
  
  // private apiRoot: string = environment.apiRoot || 'https://localhost:8081';  // API Gateway
  // private dictionaryRoot: string = 'http://localhost:8085';
  // private optionsUrl: string = '';

  private authService: AuthService;

  constructor(private http: Http,
              private router: Router) {
      this.authService = new AuthService(this.http, this.router);
  }

  availableObjects(): Observable<string[]> {

    return this.http.options(`${this.apiRoot}${this.optionsUrl}?jwt=${this.authService.getToken()}`, this.authService.enhanceConfig())
      .map(data => data.json())
      .catch(this.handleError);

  }

  getAllProperties(): Observable<string[]> {
    let properties = this.http.get(`${this.dictionaryRoot}/dictionary/core?jwt=${this.authService.getToken()}`, this.authService.enhanceConfig())
      .map(data => data.json())
      .catch(this.handleError);
    return properties;
  }

  getAttributes( id: string, objectType: string ) {
    var url = `${this.apiRoot}/${objectType.toLowerCase()}/${id}/getAttributes?jwt=${this.authService.getToken()}`;

    return this.http.get(url, this.authService.enhanceConfig())
      .map(res => res.json());
  }

  getObject(id: string, objectType: string) {
    return this.http.get(`${this.getObjectUrl(id, objectType)}?jwt=${this.authService.getToken()}`, this.authService.enhanceConfig())
      .map(res => res.json())
      .catch(this.handleError);
  }

  addObject(omsObject) {
    const headers = new Headers({ 'Content-Type': 'application/json' });
    const options = new RequestOptions({ headers });
    return this.http.post(`${this.apiRoot}/${omsObject.clazz}?jwt=${this.authService.getToken()}`, JSON.stringify(omsObject), this.authService.enhanceConfig(options))
      .map(res => res.json())
      .catch(this.handleError);
  }

  updateObject(omsObject) {
    const headers = new Headers({ 'Content-Type': 'application/json' });
    const options = new RequestOptions({ headers });
    return this.http.put(`${this.apiRoot}/${omsObject.clazz}?jwt=${this.authService.getToken()}`, JSON.stringify(omsObject), this.authService.enhanceConfig(options))
      .map(res => res.json())
      .catch(this.handleError);
  }

  deleteObject(obj) {
    return this.http.delete(`${this.getObjectUrl(obj.id, obj.clazz)}?jwt=${this.authService.getToken()}`, this.authService.enhanceConfig())
      .map(res => res.json())
      .catch(this.handleError);
  }

  private getObjectUrl(id: string, objectType: string) {
    return this.apiRoot + '/' + objectType.toLowerCase() + '/' + id;
  }

  private handleError (error: Response | any) {
      console.error(error);

    /*  if (error.status === 0) {
        sessionStorage.removeItem('authToken');
        if (this.router !== undefined) {
          this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
        }
      } */
    return Observable.throw(error);
  }
}
