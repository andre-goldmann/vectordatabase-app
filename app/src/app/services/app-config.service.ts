import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { ReplaySubject, firstValueFrom } from 'rxjs';
import { Subject, Observable } from 'rxjs';

export class AppConfig {
  apiUrl!: string;
  debug?: boolean = false;
}

const appConfigDefaults: Partial<AppConfig> = {
  debug: false
};

@Injectable({
  providedIn: 'root'
})
export class AppConfigService {

  appConfig: AppConfig | undefined;
  private appConfig$: Subject<AppConfig>;

  constructor(
    private http: HttpClient,
    @Inject(LOCALE_ID) private locale: string,
    @Inject(DOCUMENT) private document: Document
    ) {
    this.appConfig$ = new ReplaySubject<AppConfig>(1);

  }

  loadAppConfig(): Promise<void> {
    return firstValueFrom(this.http.get<AppConfig>('assets/ext/configuration.json'))
      .then((data: AppConfig): void => {
        this.appConfig = {
          ...appConfigDefaults,
          ...data
        };
        this.appConfig$.next(this.appConfig);
      });
  }

  public get apiUrl(): string {

    if (!this.appConfig) {
      throw Error('Config file not loaded!');
    }

    return this.appConfig.apiUrl;
  }

}

