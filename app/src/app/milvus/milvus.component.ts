import { Component } from '@angular/core';
import {MilvusService} from "./milvus.service";

@Component({
  selector: 'app-milvus',
  templateUrl: './milvus.component.html',
  styleUrls: ['./milvus.component.css']
})
export class MilvusComponent {


  constructor(private milvusService:MilvusService) {
  }

  search(searchStr: string) {
    this.milvusService.search(searchStr).subscribe({
      next: value => {
        console.log(value);
      }
    });
  }
}
