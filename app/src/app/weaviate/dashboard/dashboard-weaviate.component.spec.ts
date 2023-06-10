import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardWeaviateComponent } from './dashboard-weaviate.component';

describe('DashboardComponent', () => {
  let component: DashboardWeaviateComponent;
  let fixture: ComponentFixture<DashboardWeaviateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DashboardWeaviateComponent]
    });
    fixture = TestBed.createComponent(DashboardWeaviateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
