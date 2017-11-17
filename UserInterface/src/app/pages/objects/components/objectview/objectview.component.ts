import { Component, OnInit, ViewEncapsulation } from '@angular/core';


@Component({
  styleUrls: ['./objectview.component.css'],
  templateUrl: './objectview.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class ObjectviewComponent implements OnInit {



  constructor() {
  }


  ngOnInit() {
    console.log("test");

  }


}
