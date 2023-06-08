import {Component, ViewChild} from '@angular/core';
import {firstValueFrom, ReplaySubject, Subject} from "rxjs";
import {IndexForm} from "../index.form";
import {IndexInfoService} from "../index-info.service";
import {PineconeService} from "../pinecone.service";

@Component({
  selector: 'app-index-config-form',
  templateUrl: './index-config-form.component.html',
  styleUrls: ['./index-config-form.component.css']
})
export class IndexConfigFormComponent {

  @ViewChild('file', { static: false }) file:any;

  public files: Set<File> = new Set();

  checked: boolean = true;
  public models$!: Subject<string[]>;
  public indexes$!: Subject<string[]>;

  constructor(
    public indexForm: IndexForm,
    public indexInfoService:IndexInfoService,
    private pineconeService: PineconeService) {
    indexForm.indexForm.disable();
    this.indexes$ = new ReplaySubject<string[]>(1);
    this.models$ = new ReplaySubject<string[]>(1);
    this.indexInfoService.loadIndexes();
    this.loadModels();
  }

  disableField(checked:boolean) {
    if(checked){
      this.indexForm.indexForm.disable();
      this.indexForm.indexForm.controls.apikey.patchValue("DEFAULT-API-KEY");
      this.indexForm.indexForm.controls.modelName.patchValue("DEFAULT-MODEL-NAME");
      this.indexForm.indexForm.controls.metric.patchValue("DEFAULT-METRIC");
      this.indexForm.indexForm.controls.indexName.patchValue("DEFAULT-INDEX-NAME");
      this.indexForm.indexForm.controls.environment.patchValue("DEFAULT-ENVIRONMENT");
    }else {
      this.indexForm.indexForm.enable();
      this.indexForm.indexForm.controls.apikey.patchValue(null);
      this.indexForm.indexForm.controls.modelName.patchValue(null);
      this.indexForm.indexForm.controls.indexName.patchValue(null);
      this.indexForm.indexForm.controls.environment.patchValue(null);
    }
  }

  private loadModels() {
    firstValueFrom(this.pineconeService.getModels()).then(this.models$.next.bind(this.models$));
  }

}
