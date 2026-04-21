import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileDrawer } from './profile-drawer';

describe('ProfileDrawer', () => {
  let component: ProfileDrawer;
  let fixture: ComponentFixture<ProfileDrawer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileDrawer],
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileDrawer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
