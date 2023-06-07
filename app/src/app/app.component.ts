import { Component } from '@angular/core';
import {SentenceService} from "./sentence.service";
import {MilvusService} from "./milvus.service";
import {PineconeService} from "./pinecone.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
  constructor(private sentenceService: SentenceService,
              private milvusService:MilvusService,
              private pineconeService:PineconeService) {
  }

  callSentenceService() {
    this.sentenceService.translate("Hello World")
      .subscribe(
        {
          next: value => {
            console.log(value)
          }
        }
      );
  }

  callBackendService() {
    this.milvusService.search("Hello World")
      .subscribe(
        {
          next: value => {
            console.log(value)
          }
        }
      );
  }

  callPinceService() {
      this.pineconeService.getIndexes().subscribe(
        {
          next: value => {
            console.log(value)
          }
        }
      );
      this.pineconeService.getModels().subscribe(
        {
          next: value => {
            console.log(value)
          }
        }
      );
  }
}
