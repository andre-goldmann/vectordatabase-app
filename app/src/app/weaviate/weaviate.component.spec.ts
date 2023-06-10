import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WeaviateComponent } from './weaviate.component';

describe('WeaviateComponent', () => {
  let component: WeaviateComponent;
  let fixture: ComponentFixture<WeaviateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WeaviateComponent]
    });
    fixture = TestBed.createComponent(WeaviateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
