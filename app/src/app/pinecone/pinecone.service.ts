import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {AppConfigService} from "../services/app-config.service";
import {PineconeIndexInfo} from "./index-info.service";



@Injectable({
  providedIn: 'root'
})
export class PineconeService {

  private baseUrl: string;

  constructor(private http: HttpClient,
              private config: AppConfigService) {
    this.baseUrl = `${this.config.apiUrl}/pinecone`;
  }

  public getIndexInfo(apiKey: string, indexName: string, environment:string): Observable<PineconeIndexInfo> {
    return this.http.get<PineconeIndexInfo>(`${this.baseUrl}/indexinfo/${apiKey}/${indexName}/${environment}`);
  }

  public existsFiles(fileName:string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/fileinfo/${fileName}`);
  }

  public getModels(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/listmodels`);
  }

  public getIndexes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/listindexes`);
  }
}
