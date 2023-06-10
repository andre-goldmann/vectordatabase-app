import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppConfigService} from "../services/app-config.service";

@Injectable({
  providedIn: 'root'
})
export class MilvusService {
  private baseUrl: string;

  constructor(private http: HttpClient,
              private config: AppConfigService) {
    this.baseUrl = `${this.config.apiUrl}/milvus`;
  }

  search(searchString: string) {
    return this.http
      .get<string>(`${this.baseUrl}/search?searchedDomain=` + searchString);
  }
}
