import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardMilvusComponent } from './dashboard-milvus.component';

describe('DashboardComponent', () => {
  let component: DashboardMilvusComponent;
  let fixture: ComponentFixture<DashboardMilvusComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DashboardMilvusComponent]
    });
    fixture = TestBed.createComponent(DashboardMilvusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
