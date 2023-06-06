import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class SentenceService {

  constructor(private http: HttpClient) { }

  translate(sentence: string) {
    const headers= new HttpHeaders()
      .set('Authorization', 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJoZWxsbyI6IndvcmxkIn0.bqxXg9VwcbXKoiWtp-osd0WKPX307RjcN7EuXbdq-CE');
    return this.http
      .get<string>("http://94.16.104.209:9081/translator/all-MiniLM-L6-v2/?text=" + sentence,
        { 'headers': headers })
  }
}
