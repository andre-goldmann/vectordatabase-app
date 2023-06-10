import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MilvusComponent } from './milvus.component';

describe('MilvusComponent', () => {
  let component: MilvusComponent;
  let fixture: ComponentFixture<MilvusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MilvusComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MilvusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
