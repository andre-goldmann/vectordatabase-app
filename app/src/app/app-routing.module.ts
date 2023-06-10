import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {DashboardPineconeComponent} from "./pinecone/dashboard/dashboard-pinecone.component";
import {DashboardMilvusComponent} from "./milvus/dashboard/dashboard-milvus.component";
import {DashboardWeaviateComponent} from "./weaviate/dashboard/dashboard-weaviate.component";
import {DashboardQdrantComponent} from "./qdrant/dashboard/dashboard-qdrant.component";

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'database/pinecone', component: DashboardPineconeComponent },
  { path: 'database/milvus', component: DashboardMilvusComponent },
  { path: 'database/weaviate', component: DashboardWeaviateComponent },
  { path: 'database/qdrant', component: DashboardQdrantComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
