import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardQdrantComponent } from './dashboard-qdrant.component';

describe('DashboardComponent', () => {
  let component: DashboardQdrantComponent;
  let fixture: ComponentFixture<DashboardQdrantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DashboardQdrantComponent]
    });
    fixture = TestBed.createComponent(DashboardQdrantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
