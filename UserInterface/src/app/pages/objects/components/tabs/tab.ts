import { Component, Input } from '@angular/core';

@Component({
  selector: 'tab',
  styles: [`
    .pane{
      padding: 1em;
      background-color: #1C2B36;
      border-radius: calc(0.25rem - 1px) calc(0.25rem - 1px) calc(0.25rem - 1px) calc(0.25rem - 1px);
    }
  `],
  template: `
    <div [hidden]="!active" class="pane">
      <ng-content></ng-content>
    </div>
  `
})
export class Tab {
  @Input('tabTitle') title: string;
  @Input() active = false;
}
