import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AppConfigService} from "./services/app-config.service";

@Injectable({
  providedIn: 'root'
})
export class SentenceService {
  private baseUrl: string;

  constructor(private http: HttpClient,
              private config: AppConfigService) {
    this.baseUrl = `${this.config.apiUrl}/sentences`;
  }

  translate(sentence: string) {
    // TODO more secure would be to call java and then python
    //`${this.baseUrl}/fileinfo/${fileName}`
    const headers= new HttpHeaders()
      // TODO make this configurable and not visible from outside
      .set('Authorization', 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJoZWxsbyI6IndvcmxkIn0.bqxXg9VwcbXKoiWtp-osd0WKPX307RjcN7EuXbdq-CE');
    return this.http
      // TODO make this configurable and not visible from outside
      //.get<string>("http://94.16.104.209:9081/translator/all-MiniLM-L6-v2/?text=" + sentence,
      .get<string>(`${this.baseUrl}/translator/all-MiniLM-L6-v2/?text=` + sentence,
        { 'headers': headers })
  }
}
