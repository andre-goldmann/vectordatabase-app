import { Component } from '@angular/core';
import {SentenceService} from "./sentence.service";
import {BackendService} from "./backend.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
  constructor(private sentenceService: SentenceService,
              private backendService:BackendService) {
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
    this.backendService.search("Hello World")
      .subscribe(
        {
          next: value => {
            console.log(value)
          }
        }
      );
  }
}
