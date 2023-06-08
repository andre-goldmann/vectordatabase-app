import {Component, Input, ViewChild} from '@angular/core';
import {forkJoin, Observable, ReplaySubject, Subject} from "rxjs";
import {UploadService} from "./upload.service";
import {PineconeService} from "../pinecone.service";
import {IndexForm} from "../index.form";
import {IndexInfoService} from "../index-info.service";


export interface FileExistence {
  loading: boolean;
  exists: boolean;
  fileName: string
}

@Component({
  selector: 'app-data-upload-form',
  templateUrl: './data-upload-form.component.html',
  styleUrls: ['./data-upload-form.component.css']
})
export class DataUploadFormComponent {

  @ViewChild('file', { static: false }) file:any;
  public files: Set<File> = new Set();
  public fileExistence: Set<FileExistence> = new Set();

  progress: { [p: string]: { progress: Observable<number> } } = {};
  primaryButtonText = 'Upload';
  uploading = false;
  uploadSuccessful = false;
  public indexes$!: Subject<string[]>;

  @Input() url!: string;

  isFileUpload: boolean=true;

  constructor(
    private uploadService: UploadService,
    private pineconeService: PineconeService,
    public indexForm: IndexForm,
    private indexInfoService:IndexInfoService) {
    this.indexes$ = new ReplaySubject<string[]>(1);
  }

  async createFile(url:string){
    let fileName = this.url.split('/').pop();
    let response = await fetch(url, {mode: 'no-cors'});
    let data = await response.blob();
    let metadata = {
      type: 'image/jpeg'
    };
    return new File([data], fileName!, metadata);
  }

  onSubmit() {
    if(this.files.size == 0 && this.url == null){
      return;
    }
    if(this.url != null){
      let fileName = this.url.split('/').pop();
      if(fileName == undefined){
        return;
      }
      this.createFile(this.url).then(result => {
        this.files.add(result);
      });

      let e = {} as FileExistence
      e.fileName = fileName!;
      e.loading = false;
      this.pineconeService.existsFiles(fileName!)
        .subscribe(
          {
            next: value => {
              e.exists = value
            }
          });
      this.fileExistence.add(e)

    }

    console.log("Submitting ...")
    // set the component state to "uploading"
    this.uploading = true;

    //
    // start the upload and save the progress map
    this.progress = this.uploadService.upload(
      this.files,
      // TODO this needs to be taken from index-form
      this.indexForm.indexForm,
      this.fileExistence,
      this.url);

    // convert the progress map into an array
    let allProgressObservables = [];
    for (let key in this.progress) {
      allProgressObservables.push(this.progress[key].progress);
    }

    // The OK-button should have the text "Finish" now
    this.primaryButtonText = 'Finish';

    // When all progress-observables are completed...
    forkJoin(allProgressObservables).subscribe(end => {
            // ... the upload was successful...
      this.uploadSuccessful = true;

      // ... and the component is no longer uploading
      this.uploading = false;
      console.info("Finished upload!")

      this.indexInfoService.updateInfo();

    });
  }

  onFilesAdded() {
    const files: { [key: string]: File } = this.file.nativeElement.files;
    for (let key in files) {
      if (!isNaN(parseInt(key))) {
        this.files.add(files[key]);
        let e = {} as FileExistence
        e.fileName = files[key].name;
        e.loading = false;
        this.pineconeService.existsFiles(files[key].name)
          .subscribe(
  {
                next: value => {
                  e.exists = value
                }
              });
        this.fileExistence.add(e)
      }
    }
  }

  existsFile(fileName:string) {
    for (let ex of this.fileExistence) {
      if(ex.fileName == fileName){
        return ex.exists;
      }
    }
    return false;
  }

  deleteFile(file: File) {

    if (!this.files.delete(file)){
      throw Error(`${file} could not be deleted`)
    }else {
      this.file.nativeElement.value = '';
    }
    for (let ex of this.fileExistence) {
      if(ex.fileName == file.name){
        this.fileExistence.delete(ex);
        break;
      }
    }
  }

  fileIsLoading(fileName: string) {
    for (let ex of this.fileExistence) {
      if(ex.fileName == fileName){
        return ex.loading;
      }
    }
    return false;
  }

  disableUrlUpload(checked: boolean) {
    this.isFileUpload = checked;
  }
}
