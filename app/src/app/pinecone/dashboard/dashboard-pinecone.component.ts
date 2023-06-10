import { Component } from '@angular/core';
import { map } from 'rxjs/operators';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard-pinecone.component.html',
  styleUrls: ['./dashboard-pinecone.component.css']
})
export class DashboardPineconeComponent {
  /** Based on the screen size, switch from standard to one column per row */
  cards = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => {
      if (matches) {
        return [
          { title: 'Card 1', cols: 1, rows: 1, height: 440 },
          { title: 'Card 2', cols: 1, rows: 1, height: 440 },
          { title: 'Card 3', cols: 1, rows: 1, height: 440 },
          { title: 'Card 4', cols: 1, rows: 1, height: 440 }
        ];
      }

      return [
        { title: 'Index-Config', cols: 2, rows: 1, height: 340 },
        { title: 'Data Upload', cols: 1, rows: 4, height: 440 },
        { title: 'Data Queries', cols: 1, rows: 4, height: 440 },
        /*{ title: 'Card 3', cols: 1, rows: 2 },
        { title: 'Card 4', cols: 1, rows: 1 }*/
      ];
    })
  );

  constructor(private breakpointObserver: BreakpointObserver) {}
}
