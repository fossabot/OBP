import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Network, DataSet, Node, Edge, IdType, Data } from 'vis';

import { Util } from '../shared/Util'; 
import { AuthService } from '../../../auth/auth.service';
import { GlobalState } from '../../../../global.state';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/toPromise';

@Component({
  selector: 'oms-visualization',
  styleUrls: ['./visualize.component.css', 
      '../../../../../../node_modules/vis/dist/vis-network.min.css', 
      '../../../../../../node_modules/vis/dist/vis.css'],
  templateUrl: './visualize.component.html',
  encapsulation: ViewEncapsulation.None,
})

export class VisualizeComponent implements OnInit {
  private id: string;
  private clazz: string;
  private nodes: DataSet<Node>;
  private edges: DataSet<Edge>;
  private network: Network;

  private container: any;
  private allVisOptionsContainer: Element;

  visJSOptions: any; 
  selectedVisJSOption: string;

  groups: any;
  availableShapes = ['ellipse', 'circle', 'database', 'box', 'text', 'diamond', 'dot', 
                    'star', 'triangle', 'triangleDown', 'square', 
                    'Category Image', 'Specific Image',
                    // 'icon', 'image', 'circularImage',
                  ];
  availableColors = ['lightblue', 'red', 'orange', 'yellow', 'green', 'blue', 'purple', 'white'];

  nodeDepth = 5;

  turnOnNodePhysics = false;
  turnOnHierarchy = false;
  turnOnEdgeLabels = true;
  turnOnEdgeLabelAlignment = true;

  private authService: AuthService;
  private neo4jVisRequestForSpecificNodeURL: string = 'https://obp-05.esl.saic.com:8083/visualizeSpecificNode'; 
  private neo4jVisRequestForAllNodesURL: string = 'https://obp-05.esl.saic.com:8083/visualizeAllNodes';  

  constructor(
    private http: Http,
    private router: Router,
    private route: ActivatedRoute,
    private state: GlobalState) {
      this.authService = new AuthService(this.http, this.router);

  }

  ngOnInit() {
    this.container = document.getElementById('mynetwork');
    this.allVisOptionsContainer = document.getElementById('allVisOptionsContainer');

    this.route.params.map(params => params['id']).subscribe(id => this.id = id);
    if (this.id) {
      this.displayObject();
    } else {
      this.displayAllObjects();
    }
 
    this.visJSOptions = [];
    this.visJSOptions.push({ 'name': 'None' });
    this.visJSOptions.push({ 'name': 'Simple' });
    this.visJSOptions.push({ 'name': 'All' });

    this.selectedVisJSOption = 'None';

    this.state.notifyDataChanged('title.change', 'Visualize');
  }

  private getOptionsFromConfiguration(): any {
    let options;
    if (!this.network) {

      options = {
       configure: {
          enabled: true,
          filter: true,
          container: this.allVisOptionsContainer,
          showButton: true,
        },

        layout: {
          hierarchical: {
            enabled: false,
            sortMethod: 'directed',   // hubsize, directed
          },
        },
        
        nodes: {
          physics: false,
        },
        edges: {
          smooth: true,
          font: {
            // size: 10, // px
            align: 'middle',
          },
        },
      };

      options.groups = {};
      this.groups.forEach(group => {
        options.groups[group] = {
                shape: 'ellipse',
                color: 'lightblue',
                /*
                color: {
                  background: 'lightblue',
                },
                */
        };
      });


    } else {
      options = this.network.getOptionsFromConfigurator();
    }
    return options;
  }

  private refreshDisplay() {
     // create a network
    const data: Data = {
      nodes: this.nodes,
      edges: this.edges,
    };
    
    if (this.network ) {
      this.network.setData(data);
    } else {
      this.network = new Network(this.container, data, this.getOptionsFromConfiguration());
    }
  }

  private displayObject() {
    this.route.params.map(params => params['clazz']).subscribe(clazz => this.clazz = clazz);

    // console.error('depth', this.nodeDepth);

    const nodeClazzName = Util.capitalizeFirstLetter(this.clazz.concat('Node'));
    const visRequest = { id: this.id, className: nodeClazzName, depth: this.nodeDepth };
   
    const headers = new Headers();
    headers.set('Content-Type', 'application/json');
    // headers.set('Authorization', this.authService.getToken());

    const options = new RequestOptions({ headers });
    
    this.http.post(`${this.neo4jVisRequestForSpecificNodeURL}?jwt=${this.authService.getToken()}`,
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
            console.error('COMPLETED');
          });
  }

  private displayAllObjects() {
    const headers = new Headers();
    headers.set('Content-Type', 'application/json');
    // headers.set('Authorization', this.authService.getToken());

    const options = new RequestOptions({ headers });
    
    this.http.post(`${this.neo4jVisRequestForAllNodesURL}?jwt=${this.authService.getToken()}`,
      JSON.stringify(''), 
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
            // console.error('COMPLETED');
          });
  }

  private handleVisResponse(visResponse: any) {

    // console.error('visResponse = ' , visResponse);
    // console.error('nodes = ', visResponse['nodes']);
    // console.error('edges = ', visResponse['edges']);

    const visNodes = visResponse['nodes'];
    const visEdges = visResponse['edges'];
    
    const tempNodes = new DataSet();
    visNodes.forEach(function (visNode) {
      // console.error('node = ', visNode);
      // console.error('id = ', visNode['id']);
      // console.error('label = ', visNode['label']);
      // console.error('category = ', visNode['category']);

      tempNodes.add([
          { 
            id: visNode['id'], 
            label: visNode['label'],
            level: visNode['level'],
            group: visNode['category'],
            categoryImageURL: visNode['categoryImageURL'],
            imageURL: visNode['imageURL'],
          },
        ]);
    });

    this.groups = tempNodes.distinct('group');

    const tempEdges = new DataSet();
    visEdges.forEach(function (visEdge) {
      // console.error('edge = ', visEdge);
      // console.error('fromId = ', visEdge['fromId']);
      // console.error('toId = ', visEdge['toId']);

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

  changeVisJSOption(visJSOption: any) {
    // console.error('changeVisJSOption ', visJSOption.name);
    
    this.selectedVisJSOption = visJSOption.name;
  }

  private updateTurnOnNodePhysics() {
    const options = this.getOptionsFromConfiguration();

    if (!options.nodes ) {
      options.nodes = {};
    }
    options.nodes.physics = this.turnOnNodePhysics;

    this.network.setOptions(options);
  }

  changeTurnOnNodePhysics() {
    this.turnOnNodePhysics = !this.turnOnNodePhysics;
    this.updateTurnOnNodePhysics();
  }

  changeTurnOnHierarchy() {
    this.turnOnHierarchy = !this.turnOnHierarchy;

    const options = this.getOptionsFromConfiguration();

    if (!options.layout ) {
      options.layout = {};
    }

    if (!options.layout.hierarchical ) {
      options.layout.hierarchical = {};
    }
    
    if (this.turnOnHierarchy) {
      options.layout.hierarchical.enabled = true;
      options.layout.hierarchical.sortMethod = 'directed';

      // undefine data then reapply after changing config
      let data: Data = {
        nodes: undefined,
        edges: undefined,
      };

      this.network.setData(data);
      this.network.setOptions(options);
      
      data = {
        nodes: this.nodes,
        edges: this.edges,
      };
      
      this.network.setData(data);

      return;

    } else {
      options.layout.hierarchical.enabled = false;
      options.layout.hierarchical.sortMethod = 'hubsize';
    }

    this.network.setOptions(options);
  }

  private updateTurnOnEdgeLabels() {
    const options = this.getOptionsFromConfiguration();

    if (!options.edges ) {
      options.edges = {};
    }
    if (!options.edges.font ) {
      options.edges.font = {};
    }

    if (this.turnOnEdgeLabels) {
      options.edges.font.size = 12;
      
      if (this.turnOnEdgeLabelAlignment) {
        options.edges.font.align = 'middle';
      } else {
        options.edges.font.align = 'horizontal';
      }
    } else {
      options.edges.font.size = 0;
    }

    this.network.setOptions(options);
  }

  changeTurnOnEdgeLabels() {
    this.turnOnEdgeLabels = !this.turnOnEdgeLabels;
    this.updateTurnOnEdgeLabels();
  }

  private updateTurnOnEdgeLabelAlignment() {
    const options = this.getOptionsFromConfiguration();

    if (!options.edges ) {
      options.edges = {};
    }
    if (!options.edges.font ) {
      options.edges.font = {};
    }

    if (this.turnOnEdgeLabelAlignment) {
      options.edges.font.align = 'middle';
    } else {
      options.edges.font.align = 'horizontal';
    }

    this.network.setOptions(options);
  }

  changeTurnOnEdgeLabelAlignment() {
    this.turnOnEdgeLabelAlignment = !this.turnOnEdgeLabelAlignment;
    this.updateTurnOnEdgeLabelAlignment();
  }

  changeNodeDepth() {
    // console.error('changed node depth ', this.nodeDepth);

    this.route.params.map(params => params['id']).subscribe(id => this.id = id);
    if (this.id) {
      this.displayObject();
    } else {
      this.displayAllObjects();
    }
  }

  changeGroupShape(selectedGroup, selectedShape) {
    // console.error('group', selectedGroup);
    // console.error('shape', selectedShape);

    if (selectedShape === 'Category Image') {
      this.nodes.forEach(node => {
        if (node.group === selectedGroup) {
          this.nodes.update( { id: node.id, shape: 'image', image: node['categoryImageURL'] } );
        }
      });
      selectedShape = null;
    } else if (selectedShape === 'Specific Image') {
      this.nodes.forEach(node => {
        if (node.group === selectedGroup) {
          let tempImageURL = node['imageURL'];
          if ( !tempImageURL ) {
            tempImageURL = node['categoryImageURL'];
          }
          this.nodes.update( { id: node.id, shape: 'image', image: tempImageURL } );
        }
      });
      selectedShape = null;
    } else {
      this.nodes.forEach(node => {
        if (node.group === selectedGroup) {
          this.nodes.update( { id: node.id, shape: selectedShape, image: null } );
        }
      });
    }
  }

  changeGroupColor(selectedGroup, selectedColor) {
    // console.error('group', selectedGroup);
    // console.error('shape', selectedColor);

    const options = this.getOptionsFromConfiguration();
    if (!options.groups) {
      options.groups = {};
    }
    /*
    if (!options.groups[selectedGroup]) {
      options.groups[selectedGroup] = {};
    }
    options.groups[selectedGroup].color = {
      background: selectedColor, 
    }; 
    */
    options.groups[selectedGroup] = {
      color: selectedColor,
    };
    this.network.setOptions(options);
  }
}
