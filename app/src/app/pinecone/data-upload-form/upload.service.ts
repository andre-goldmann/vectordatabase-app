//https://www.bezkoder.com/angular-12-file-upload/
import {Injectable} from '@angular/core'
import {HttpClient, HttpRequest} from '@angular/common/http'
import {Observable, Subject, switchMap, takeUntil, timer} from "rxjs";
import {FormGroup} from "@angular/forms";
import {AppConfigService} from "../../services/app-config.service";
import {PineconeService} from "../pinecone.service";
import {FileExistence} from "./data-upload-form.component";

@Injectable()
export class UploadService {
  INTERVAL = 1000;  // <-- poll every 1 seconds
  private baseUrl: string;

  constructor(private http: HttpClient,
              private config: AppConfigService,
              private pineconeService: PineconeService) {
    this.baseUrl = `${this.config.apiUrl}/pinecone`;
  }

  public upload(files: Set<File>,
                addressForm: FormGroup,
                fileExistence: Set<FileExistence>,
                url: string | null):
    { [key: string]: { progress: Observable<number> } } {
    console.log(`url: ${url}`)
    console.log(`files: ${files.size}`)
    console.log(`fileExistences: ${fileExistence.size}`)
    // this will be the our resulting map
    const status: { [key: string]: { progress: Observable<number> } } = {};

    if(files.size > 0) {
      const formData: FormData = new FormData();
      let postUrl = `${this.baseUrl}/uploadfiles`;
      files.forEach(file => {
        formData.append('files', file, file.name);
        this.uploadFile(file.name, addressForm, fileExistence, status, formData, postUrl);
      });
    }
    else if(fileExistence.size > 0 && url) {
      const formData: FormData = new FormData();
      formData.append('url', url);
      let postUrl = `${this.baseUrl}/uploadfiles/url`;
       fileExistence.forEach(file => {
         this.uploadFile(file.fileName, addressForm, fileExistence, status, formData, postUrl);
       });
    }

    // return the map of progress.observables
    return status;

  }
  uploadFile(fileName:string,
             addressForm: FormGroup,
             fileExistence: Set<FileExistence>,
             status: { [key: string]: { progress: Observable<number> } },
             formData: FormData,
             postUrl:string) {
    let element!: FileExistence;
    for (let ex of fileExistence) {
      if (ex.fileName == fileName) {
        element = ex;
        break;
      }
    }
    // can not be null
    element.loading = true;

    // create a new multipart-form for every file

    formData.append('apiKey', addressForm.get('apikey')!.value);
    formData.append('modelName', addressForm.get('modelName')!.value);
    formData.append('indexName', addressForm.get('indexName')!.value);
    formData.append('environment', addressForm.get('environment')!.value);
    formData.append('metric', addressForm.get('metric')!.value);

    // create a http-post request and pass the form
    // tell it to report the upload progress
    let req = new HttpRequest('POST', postUrl, formData, {
      reportProgress: true,
    });

    // create a new progress-subject for every file
    const progress = new Subject<number>();

    this.http.request(req).toPromise()
      .then(() => {
        let closeTimer$ = new Subject<any>();
        timer(0, this.INTERVAL).pipe(      // <-- start immediately and poll every `INTERVAL` seconds
          switchMap(() => this.pineconeService.existsFiles(fileName)),  // <-- map to another observable
          takeUntil(closeTimer$)   // <-- close the subscription when `closeTimer$` emits
        ).subscribe({
          next: value => {
            console.log("result from poller: " + value)
            if (value) {
              closeTimer$.next(value);  // <-- stop polling
              console.log("Closing poller!")
              progress.next(100);
              progress.complete();
              element.loading = false;
              element.exists = true;
            }
            // do something else
          },
          error: (error: any) => {
            // handle errors
            // note that any errors would stop the polling here
          }
        });
      });

    // Save every progress-observable in a map of all observables
    status[fileName] = {
      progress: progress.asObservable()
    };
  }

}
