import { Component } from '@angular/core';
import {SentenceService} from "./sentence.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
  constructor(private sentenceService: SentenceService) {
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
}
