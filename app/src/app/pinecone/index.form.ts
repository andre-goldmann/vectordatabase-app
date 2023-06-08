import { Injectable } from '@angular/core';
import {FormControl, Validators, FormBuilder} from '@angular/forms';

@Injectable({ providedIn: 'root' })
export class IndexForm  {
  indexForm = this.fb.group({
    apikey: ["DEFAULT-API-KEY", Validators.required],
    modelName: ["DEFAULT-MODEL-NAME", Validators.required],
    metric: ["DEFAULT-METRIC", Validators.required],
    indexName: ["DEFAULT-INDEX-NAME", Validators.required],
    environment: ["DEFAULT-ENVIRONMENT", Validators.required],
    deleteIndex: new FormControl(false, [])
  });

  constructor(
    private fb: FormBuilder){
  }
}
