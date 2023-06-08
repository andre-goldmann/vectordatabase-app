import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppConfigService} from "../../services/app-config.service";
import {Observable, of} from "rxjs";

export interface QueryResult {
  id: number
  score: number
  text: string
}
export type Root = Root2[]

export interface Root2 {
  id: number
  score: number
  text: string
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private baseUrl: string;

  constructor(private http: HttpClient,
              private config: AppConfigService) {
    this.baseUrl = `${this.config.apiUrl}/pinecone`;
  }

  // public fetchbyid(
  //   indexName:string,
  //   apiKey: string,
  //   environment: string,
  //   id:string): Observable<string> {
  //   return this.http.get<string>(`${this.baseUrl}/fetchbyid/?indexName=${indexName}&apiKey=${apiKey}&environment=${environment}&searchid=${id}`);
  // }

  public searchbyquery(indexName:string,
                       apiKey: string,
                       environment: string,
                       modelName: string,
                       query:string): Observable<string> {
    if(indexName == undefined){
      return of();
    }
    return this.http
      .get<string>(`${this.baseUrl}/searchbyquery/${apiKey}/${indexName}/${environment}/${modelName}/${query}`);
  }

}
