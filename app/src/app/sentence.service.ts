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
    return this.http
      .get<string>(`${this.baseUrl}/translator/all-MiniLM-L6-v2/text=` + sentence)
  }

  splitText(sentence: string) {
    return this.http
      .get<string>(`${this.baseUrl}/splitter/text=` + sentence)
  }
}
