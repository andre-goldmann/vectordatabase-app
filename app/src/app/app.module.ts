import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavbarComponent } from './navbar/navbar.component';
import { DataUploadFormComponent } from './pinecone/data-upload-form/data-upload-form.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UploadService} from "./pinecone/data-upload-form/upload.service";
import {HttpClientModule} from "@angular/common/http";
import {AppConfigService} from "./services/app-config.service";
import {PineconeService} from "./pinecone/pinecone.service";
import { DataSearchFormComponent } from './pinecone/data-search-form/data-search-form.component';
import {DashboardComponent} from "./pinecone/dashboard/dashboard.component";
import {SearchService} from "./pinecone/data-search-form/search.service";
import { IndexConfigFormComponent } from './pinecone/index-config-form/index-config-form.component';
import {IndexForm} from "./pinecone/index.form";
import {LayoutModule} from "@angular/cdk/layout";

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    DashboardComponent,
    DataUploadFormComponent,
    DataSearchFormComponent,
    IndexConfigFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    LayoutModule,
    FormsModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      multi: true,
      deps: [AppConfigService],
      useFactory: (appConfigService: AppConfigService): (() => Promise<void>) => {
        return (): Promise<void> => {
          // Make sure to return a promise!
          return appConfigService.loadAppConfig();
        };
      }
    },
    IndexForm,
    UploadService,
    SearchService,
    PineconeService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
