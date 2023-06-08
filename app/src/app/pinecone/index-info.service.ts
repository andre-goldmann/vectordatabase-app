import { Injectable } from '@angular/core';
import {BehaviorSubject, of} from "rxjs";
import {PineconeService} from "./pinecone.service";
import {HttpErrorResponse} from "@angular/common/http";

export interface PineconeIndexInfo {
  indexName:string
  dimension: number
  index_fullness: number
  total_vector_count: number
  error:string
}

@Injectable({
  providedIn: 'root'
})
export class IndexInfoService {
  private _info$ = new BehaviorSubject<PineconeIndexInfo>({} as PineconeIndexInfo);
  selectedInfo$ = this._info$.asObservable();

  constructor(private pineconeService: PineconeService) { }

  get info(): PineconeIndexInfo {
    return this._info$.value;
  }

  setInfo(info: PineconeIndexInfo) {
    console.log("Set index: " + info.indexName)
    this._info$.next(info);
  }

  updateInfo() {
    this.loadIndexes();
  }

  public loadIndexes() {
    this.pineconeService.getIndexes()
      .subscribe({
        next: value => {
          value.forEach(e => {
            this.loadIndexInfo(e);
          })
        },
        error: err => {
          let info = {} as PineconeIndexInfo
          info.error = this.identifyError(err)
          this.setInfo(info);
        }
      });
  }

  private loadIndexInfo(index:string) {
    this.pineconeService.getIndexInfo('DEFAULT-API-KEY', index, 'DEFAULT-ENVIRONMENT').subscribe({
      next: value => {
        this.setInfo(value);
      }
    });
  }

  private identifyError(error: any) {
    console.error(error);
    if (error instanceof HttpErrorResponse) {
      if (error.error instanceof ErrorEvent) {
        return "Error Event";
      } else {
        //console.log(`error status : ${error.status} ${error.statusText}`);
        return `error status : ${error.status} ${error.statusText}`;
        // switch (error.status) {
        //   case 401:      //login
        //     return "login failed";
        //   case 403:     //forbidden
        //     return "unauthorized";
        // }
        // return "Unknown"
      }
    } else {
      return "some thing else happened";
    }
  }


}
