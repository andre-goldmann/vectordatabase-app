import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppConfigService} from "./services/app-config.service";

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
    //`${this.baseUrl}/fileinfo/${fileName}`
    return this.http
      // TODO make this configurable and not visible from outside
      //.get<string>("http://milvusbackend:7081/sites/milvus/search?searchedDomain=" + searchString);
      .get<string>(`${this.baseUrl}/search?searchedDomain=` + searchString);
  }
}
