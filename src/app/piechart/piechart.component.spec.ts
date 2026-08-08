import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { PiechartComponent } from './piechart.component';

describe('PiechartComponent', () => {
  let component: PiechartComponent;
  let fixture: ComponentFixture<PiechartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PiechartComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PiechartComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
