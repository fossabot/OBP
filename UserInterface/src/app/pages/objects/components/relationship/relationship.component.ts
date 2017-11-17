import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Network, DataSet, Node, Edge, IdType, Data } from 'vis';

import { Util } from '../shared/Util'; 
import { AuthService } from '../../../auth/auth.service';
import { GlobalState } from '../../../../global.state';
import { SearchService } from '../search/search.service';
import { environment } from '../../../../../environments/environment';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/toPromise';

@Component({
  selector: `oms-relationship`,
  templateUrl: 
  // './test.component.html', 
   './relationship.component.html',
  styleUrls: ['./relationship.component.css'],
  encapsulation: ViewEncapsulation.None,
})

export class RelationshipComponent implements OnInit {
  id: string;
  clazz: string;
  nodes: DataSet<Node>;
  edges: DataSet<Edge>;
  network: Network;
  token: string;
  firstObject: string;
  secondObject: string;
  relationship: string;
  response: any;
  errorMessage: string;

  /*  
  private neo4jVisRequestForSpecificNodeURL: string = 'https://localhost:8083/visualizeSpecificNode'; 
  private neo4jVisRequestForAllNodesURL: string = 'https://localhost:8083/visualizeAllNodes';  
  private displaySelectedNode: string = 'https://localhost:8083/displaySelectedNode'; 
  private getNode: string = 'https://localhost:8083/get_node';  
  private createlink: string = 'https://localhost:8083/create_links';  
  */

  private neo4jVisRequestForSpecificNodeURL: string = 'https://obp-05.esl.saic.com:8083/visualizeSpecificNode'; 
  private neo4jVisRequestForAllNodesURL: string = 'https://obp-05.esl.saic.com:8083/visualizeAllNodes';  
  private displaySelectedNode: string = 'https://obp-05.esl.saic.com:8083/displaySelectedNode'; 
  private createlink: string = 'https://obp-05.esl.saic.com:8083/create_links';  
  private getNode: string = 'https://obp-05.esl.saic.com:8083/get_node';  

  private authService: AuthService;
  name: string;
  email: string;
  address: Address;
  hobbies: string[];
  showHobbies: boolean;
  objects = ['Person', 'Organization', 'Equipment', 'Event', 'Facility'];
  relationships = ['Owned By (Owns)', 'Housed  at (Houses)', 'Participated in (Had Participants)', 
                   'Hosted at (Hosted)', 'Employed By (Employs)', 'Related to (Related to)', 
                   'Associated With (Associated With)', 
                   'Commanded By (Commands)', 'Member of (Has Member of)', 'Present At (Was attended by)', 
                   'Owns (Owned by)'];
  constructor(
    private http: Http,
    private router: Router,
    private route: ActivatedRoute,
    private state: GlobalState,
    private searchService: SearchService) {
    this.name = 'John Doe';
    this.email = 'johnd@gmail.com';
    this.address = {
      street: '12 Main st',
      city: 'Boston',
      state: 'MA',
    };
    this.clazz = 'Person';
    this.hobbies = ['Music', 'Movies', 'Sports'];
    this.showHobbies = false;
    this.authService = new AuthService(this.http, this.router);
    this.token = ``;

    // this.clazz = 'Person';
    // this.id = '595ff096b8b97c3c4a87db04';
  }
  ngOnInit() {
    this.route.params.map(params => params['clazz']).subscribe(clazz => {
      this.clazz = clazz;
      this.route.params.map(params => params['id']).subscribe(id => {
        this.id = id;
      });
    });
    
    console.log('Relationship Component Initialized.');
    // this.route.params.map(params => params['id']).subscribe(id => this.id = id);
    if (this.id) {
      this.displayObject();
    } else {
      this.displayAllObjects();
    }
  }
  viewNode() {
    console.log('inside add link.');

    const headers = new Headers();
    headers.set('Content-Type', 'application/json');
    // headers.set('Authorization', this.authService.getToken());

    const options = new RequestOptions({ headers });
    
    this.token = this.getNode.concat(`/`).concat(this.id).concat(`/`).concat(this.secondObject).concat(`/`);
    this.token = this.token.concat(`?jwt=`).concat(this.authService.getToken());

    this.http.get(this.token, 
      this.authService.enhanceConfig(options))
        .subscribe(
          (res) => {
            /* this function is executed every time there's a new output */
            //  console.log("VALUE RECEIVED: "+res);
            this.handleVisResponse(res.json());
          },
          (err) => {
            /* this function is executed when there's an ERROR */
            console.error('ERROR: ', err);
          },
          () => {
            /* this function is executed when the observable ends (completes) its stream */
            console.log('Objects displayed successfully.');
          });
  }
  addLink() {
    console.log('inside add link.');

    const headers = new Headers();
    headers.set('Content-Type', 'application/json');
    // headers.set('Authorization', this.authService.getToken());

    const options = new RequestOptions({ headers });
    
    this.token = this.createlink;
    this.token = this.token.concat(`?jwt=`).concat(this.authService.getToken());
    const visRequest = { object1: `Person`, object2: `Person`, 
                         node1: this.id, 
                         node2: this.secondObject, 
                         relationship: this.relationship };
    this.http.post(this.token , 
      JSON.stringify(visRequest), 
      this.authService.enhanceConfig(options))
        .subscribe(
          (res) => {
            /* this function is executed every time there's a new output */
            //  console.log("VALUE RECEIVED: "+res);
            // this.handleVisResponse(res.json());
               this.viewNode();
          },
          (err) => {
            /* this function is executed when there's an ERROR */
            console.error('ERROR: ', err);
          },
          () => {
            /* this function is executed when the observable ends (completes) its stream */
            console.log('Objects displayed successfully.');
          });
  }
  removeLink() {

  }
  submitChoice() {
    console.log(this.firstObject);
    console.log(this.secondObject);

    this.searchService.searchBasic(this.firstObject)
      .subscribe(data => {
          this.response = this.mergeResults(data);
        },
        error => {
          this.errorMessage = <any>error;
          this.state.notifyDataChanged('message.error', 'There was an issue searching.  Please try again.');
          // alert('There was an issue searching.  Please try again.');
        });
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
      
  toggleHobbies() {
    console.log('inside toggle hobbies');
    if (this.showHobbies) {
      this.showHobbies = false;
    }else {
      this.showHobbies = true;
    }
  }
  addHobby(hobby) {
    console.log(hobby);
    this.hobbies.push(hobby);
  }
  deleteHobby(i) {
    this.hobbies.splice(i, 1);
  }
  private displayObject() {
    
    const nodeClazzName = Util.capitalizeFirstLetter(this.clazz.concat('Node'));
    const visRequest = { id: this.id, className: nodeClazzName };
   
    const headers = new Headers();
    headers.set('Content-Type', 'application/json');

    const options = new RequestOptions({ headers });
    
    this.token = this.displaySelectedNode;
    this.token = this.token.concat(`?jwt=`).concat(this.authService.getToken());

    this.http.post(this.token , 
      JSON.stringify(visRequest), 
      this.authService.enhanceConfig(options))
        .subscribe(
          (res) => {
            /* this function is executed every time there's a new output */
            //  console.log("VALUE RECEIVED: "+res);
            this.handleVisResponse(res.json());
          },
          (err) => {
            /* this function is executed when there's an ERROR */
            console.error('ERROR: ', err);
          },
          () => {
            /* this function is executed when the observable ends (completes) its stream */
            console.log('Objects displayed successfully.');
          });
  }
  private displayAllObjects() {
    const visRequest = { id: ``, className: `` };
   
    const headers = new Headers();
    headers.set('Content-Type', 'application/json');

    const options = new RequestOptions({ headers });
    
    this.token = this.displaySelectedNode;
    this.token = this.token.concat(`?jwt=`).concat(this.authService.getToken());

    this.http.post(this.token , 
      JSON.stringify(visRequest), 
      this.authService.enhanceConfig(options))
        .subscribe(
          (res) => {
            /* this function is executed every time there's a new output */
            //  console.log("VALUE RECEIVED: "+res);
            this.handleVisResponse(res.json());
          },
          (err) => {
            /* this function is executed when there's an ERROR */
            console.error('ERROR: ', err);
          },
          () => {
            /* this function is executed when the observable ends (completes) its stream */
            console.log('Objects displayed successfully.');
          });
  }
        
  private handleVisResponse(visResponse: any) {

    const visNodes = visResponse['nodes'];
    const visEdges = visResponse['edges'];
    
    const tempNodes = new DataSet();
    visNodes.forEach(function (visNode) {
      tempNodes.add([
          { 
            id: visNode['id'], 
            label: visNode['label'],
            level: visNode['level'],
          },
        ]);
    });

    const tempEdges = new DataSet();
    visEdges.forEach(function (visEdge) {

      tempEdges.add([
          {
            from: visEdge['fromId'], 
            to: visEdge['toId'],
            label: visEdge['label'], 
            arrows: 'to',
          },
        ]);
    });

    this.nodes = tempNodes;
    this.edges = tempEdges;

    this.refreshDisplay(); 
  }
refreshDisplay() {
     // create a network
    const container = document.getElementById('mygraph');
    const data: Data = {
      nodes: this.nodes,
      edges: this.edges,
    };
    const options = {
      nodes: {
        physics: false,
      },
      edges: {
        smooth: true,
        font: {
          align: 'middle',
        },
      },
    };
    this.network = new Network(container, data, options);
  }  
}
interface Address {
  street: string;
  city: string;
  state: string;
}
