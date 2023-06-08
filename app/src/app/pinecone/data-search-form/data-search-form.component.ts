import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl} from "@angular/forms";
import {
  debounceTime,
  defer,
  distinctUntilChanged,
  merge,
  Observable,
  of,
  share,
  switchMap
} from "rxjs";
import {QueryResult, SearchService} from "./search.service";
import {map} from "rxjs/operators";
import {IndexInfoService} from "../index-info.service";

@Component({
  selector: 'app-data-search-form',
  templateUrl: './data-search-form.component.html',
  styleUrls: ['./data-search-form.component.css']
})
export class DataSearchFormComponent implements OnInit {

  public searchControl!: FormControl;
  public searchResults$!: Observable<string>;
  public areMinimumCharactersTyped$!: Observable<boolean>;
  public areNoResultsFound$!: Observable<boolean>;
  public lastestResult!: QueryResult[];

  constructor(
    private searchService: SearchService,
    private formBuilder: FormBuilder,
    private indexInfoService:IndexInfoService) {
  }

  ngOnInit() {
    this.searchControl = this.formBuilder.control("");
    this.areMinimumCharactersTyped$ = this.searchControl.valueChanges.pipe(
      map(searchString => searchString.length >= 3)
    );

    const searchString$ = merge(
      defer(() => of(this.searchControl.value)),
      this.searchControl.valueChanges
    ).pipe(
      debounceTime(1000),
      distinctUntilChanged()
    );

    this.searchResults$ = searchString$.pipe(
      switchMap((searchString: string) =>
        this.searchService.searchbyquery(this.indexInfoService.info.indexName, "DEFAULT-API-KEY", "DEFAULT-ENVIRONMENT", "DEFAULT-MODEL-NAME" ,searchString)
      ),
      share()
    );

    this.searchResults$.subscribe({
      next: value => {
        //console.log(value)
        // TODO normal json-converting does not work
        this.lastestResult = JSON.parse(value);
      }
    });

    this.areNoResultsFound$ = this.searchResults$.pipe(
      map(results => results.length === 0)
    );
  }

}
