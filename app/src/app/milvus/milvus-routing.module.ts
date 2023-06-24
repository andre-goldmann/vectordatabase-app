import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardMilvusComponent} from "./dashboard/dashboard-milvus.component";

const routes: Routes = [
  { path: '', component: DashboardMilvusComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MilvusRoutingModule { }
