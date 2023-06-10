import { Component } from '@angular/core';
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {map} from "rxjs/operators";
import {Router} from "@angular/router";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {


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
        { title: 'Pinecone', cols: 2, rows: 2, height: 540 },
        { title: 'Milvus', cols: 2, rows: 2, height: 440 },
        { title: 'Weaviate', cols: 2, rows: 2, height: 440 },
        { title: 'Qdrant', cols: 2, rows: 2, height: 440 }
      ];
    })
  );

  constructor(private breakpointObserver: BreakpointObserver,
              private router: Router) {}

  openDbSite(dbSite: string) {
    console.log(dbSite)
    this.router.navigate([`/database/${dbSite}`]);
  }
}
