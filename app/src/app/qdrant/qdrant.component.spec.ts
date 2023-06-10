import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QdrantComponent } from './qdrant.component';

describe('QdrantComponent', () => {
  let component: QdrantComponent;
  let fixture: ComponentFixture<QdrantComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QdrantComponent]
    });
    fixture = TestBed.createComponent(QdrantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
