import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShopWindow } from './shop-window';

describe('ShopWindow', () => {
  let component: ShopWindow;
  let fixture: ComponentFixture<ShopWindow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShopWindow]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShopWindow);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
