import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Inside1Component } from './inside1.component';

describe('Inside1Component', () => {
  let component: Inside1Component;
  let fixture: ComponentFixture<Inside1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Inside1Component],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Inside1Component);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
