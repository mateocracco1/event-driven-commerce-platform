import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileDrawerComponent } from './profile-drawer';

describe('ProfileDrawerComponent', () => {
  let component: ProfileDrawerComponent;
  let fixture: ComponentFixture<ProfileDrawerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileDrawerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileDrawerComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
